package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.OrderDTO;
import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.entity.User;
import com.inghubs.broker_firm.entity.Order;
import com.inghubs.broker_firm.enums.SIDE;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.exception.ResourceNotFoundException;
import com.inghubs.broker_firm.repository.AssetRepository;
import com.inghubs.broker_firm.repository.UserRepository;
import com.inghubs.broker_firm.repository.OrderRepository;
import com.inghubs.broker_firm.enums.STATUS;
import com.inghubs.broker_firm.util.Constants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ModelMapper modelMapper;

    public List<OrderDTO> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrderDTO getOneById(UUID id){
        Order order = orderRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Order with id " + id + " does not exist"));
        return convertToDTO(order);
    }

    public List<OrderDTO> getByUserId(UUID userId){
        List<Order> userOrders = orderRepository.findByUserId(userId);
        return userOrders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> filterOrders(
        @RequestParam(required = false) UUID userId,
        @RequestParam(required = false) Long startDate,
        @RequestParam(required = false) Long endDate,
        @RequestParam(required = false) STATUS status,
        @RequestParam(required = false) Double lowerPriceLimit,
        @RequestParam(required = false) Double upperPriceLimit,
        @RequestParam(required = false) Double lowerSize,
        @RequestParam(required = false) Double upperSize,
        @RequestParam(required = false) SIDE side,
        @RequestParam(required = false) String assetName
    ){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);

        Root<Order> orderRoot = cq.from(Order.class);
        List<Predicate> predicates = new ArrayList<>();

        if (userId != null) {
            predicates.add(cb.equal(orderRoot.get("user").get("id"), userId));
        }
        if (startDate != null){
            predicates.add(cb.greaterThanOrEqualTo(orderRoot.get("createdAt"), startDate));
        }
        if (endDate != null){
            predicates.add(cb.lessThanOrEqualTo(orderRoot.get("createdAt"), endDate));
        }
        if (status != null){
            predicates.add(cb.equal(orderRoot.get("status"), status));
        }
        if (lowerPriceLimit != null){
           predicates.add(cb.greaterThanOrEqualTo(orderRoot.get("price"), lowerPriceLimit));
        }
        if (upperPriceLimit != null){
            predicates.add(cb.lessThanOrEqualTo(orderRoot.get("price"), upperPriceLimit));
        }
        if (lowerSize != null){
            predicates.add(cb.greaterThanOrEqualTo(orderRoot.get("size"), lowerSize));
        }
        if (upperSize != null){
            predicates.add(cb.lessThanOrEqualTo(orderRoot.get("size"), upperSize));
        }
        if(side != null){
            predicates.add(cb.equal(orderRoot.get("orderSide"), side));
        }
        if (assetName != null){
            predicates.add(cb.equal(orderRoot.get("assetName"), assetName));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<Order> ordersFiltered =  entityManager.createQuery(cq).getResultList();
        return ordersFiltered.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) throws BadRequestException {
        orderDTO.setStatus(STATUS.PENDING);
        // considering the case constraints
        // price will be the price of a unit share of an asset
        // in TRY, size will be the amount of shares the user is trying to buy/sell
        // could also be partial shares
        Double orderCost = orderDTO.getPrice()*orderDTO.getSize();
        UUID userId = orderDTO.getUserId();
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + userId + " is not found");
        }

        User user = userOptional.get();
        List<Asset> userAssets = user.getAssets();

        if (userAssets == null || userAssets.isEmpty()){
           throw new ResourceNotFoundException("User does not have any assets");
        }

        if(SIDE.BUY.equals(orderDTO.getOrderSide())){ //open buy order
            Asset assetTRY = userAssets.stream()
                .filter(asset -> Constants.TRY.equals(asset.getName()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("No TRY asset" +
                    " is not found for user with id: " + userId));

            Double usableTRYSize = assetTRY.getUsableSize();
            if (orderCost > usableTRYSize){
                throw new BadRequestException("User with id " + userId +
                    " does not have enough usable TRY asset to buy " + orderDTO.getAssetName() +
                    " at price: " + orderDTO.getPrice() +
                    " of size: " + orderDTO.getSize());
            }

            // set usable size to currentSize - orderSize*orderPrice because that amount of
            // TRY asset will be locked
            assetTRY.setUsableSize(usableTRYSize - (orderCost));
        } else { //open sell order
            Asset assetToBeSold = userAssets.stream()
                .filter(asset -> orderDTO.getAssetName().equals(asset.getName()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Asset with name " + orderDTO.getAssetName() +
                    " is not found for user with id: " + userId));

            Double currentUsableSize = assetToBeSold.getUsableSize();

            if (currentUsableSize < orderDTO.getSize()){
               throw new BadRequestException("User with id " + userId +
                   " does not have enough usable " + orderDTO.getAssetName() + " to sell." +
                   " current usable size to sell: " + assetToBeSold.getUsableSize());
            }
            // user wants to sell orderDTO.getSize amount of shares, so updating the size.
            assetToBeSold.setUsableSize(currentUsableSize - orderDTO.getSize());
        }

        userRepository.save(user);

        //save order
        Order orderEntity = convertToEntity(orderDTO);
        return convertToDTO(orderRepository.save(orderEntity));
    }

    @Transactional
    public OrderDTO matchPendingOrderById(UUID orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(
            () -> new ResourceNotFoundException("Order with id " + orderId + " is not found")
        );
        UUID userID = order.getUser().getId();

        User user = userRepository.findById(userID).orElseThrow(
            () -> new ResourceNotFoundException("User is not found with id: " + userID)
        );

        List<Asset> assets = user.getAssets();

        STATUS orderStatus = order.getStatus();
        if (!STATUS.PENDING.equals(orderStatus)){
            throw new BadRequestException("Order with id " + orderId +
                " is not in PENDING state, current state: " + orderStatus);
        }
        order.setStatus(STATUS.MATCHED);

        Asset assetTRY = assets.stream().filter(asset -> Constants.TRY.equals(asset.getName())).findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("No TRY assets found for user with id " + user.getId()));
        Double sizeTRY = assetTRY.getSize();
        Double orderCost = order.getPrice()* order.getSize();

        // re-adjust the usable size
        if (SIDE.BUY.equals(order.getOrderSide())){ //matched BUY order
            // we already decreased the usable size when we opened the order, now we need to decrease the
            // actual size of TRY asset

            assetTRY.setSize(sizeTRY - orderCost);
            // create a new asset for the bought asset if it does not exists.
            Asset assetBought = assets.stream().filter(asset -> order.getAssetName().equals(asset.getName()))
                .findFirst().orElse(null);
            if(assetBought == null){
                assetBought = new Asset();
                assetBought.setUser(order.getUser());
                assetBought.setSize(order.getSize());
                assetBought.setUsableSize(order.getSize());
                assetBought.setName(order.getAssetName());

                user.getAssets().add(assetBought);
            } else {
                assetBought.setSize(assetBought.getSize() + order.getSize());
                assetBought.setUsableSize(assetBought.getUsableSize() + order.getSize());
            }
        } else { //matched SELL order
            // increase both the usable and full TRY asset size,
            // decrease the sold asset's full size
            String assetName = order.getAssetName();
            Asset assetSold = assets.stream().filter(asset -> order.getAssetName().equals(asset.getName())).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No " + assetName + " assets found for user with id " + user.getId()));
            Double size = assetSold.getSize();
            assetSold.setSize(size - order.getSize());

            assetTRY.setSize(sizeTRY + orderCost);
            assetTRY.setUsableSize(sizeTRY + orderCost);
        }

        Order matchedOrder = orderRepository.save(order);

        userRepository.save(user);

        return convertToDTO(matchedOrder);
    }

    @Transactional
    public OrderDTO deleteById(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->{
            throw new ResourceNotFoundException("Order with id " + id + " does not exist");
        });

        STATUS status = order.getStatus();

        if (!STATUS.PENDING.equals(status)){
           throw new BadRequestException("Cannot remove orders that are in " + status + " status.");
        }

        // re-adjust usable size for BUY and SELL operations
        User user = order.getUser();
        List<Asset> assets = user.getAssets();
        Double orderCost = order.getPrice() * order.getSize();

        if (SIDE.BUY.equals(order.getOrderSide())){ //TODO function cancel BUY order
            //update TRY asset's usable size only, we haven't made any operations on the full size
            Asset assetTRY = assets.stream().filter(asset -> Constants.TRY.equals(asset.getName())).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No TRY assets found for user with id " + user.getId()));
            Double usableSize = assetTRY.getUsableSize();
            assetTRY.setUsableSize(usableSize + orderCost);
        } else { //TODO function CANCEL SELL ORDER
            // restore whatever asset user was trying to sell before
            // no need to make any operation on TRY asset sşnce we haven't modified it
            String assetName = order.getAssetName();
            Asset assetSell = assets.stream().filter(asset -> order.getAssetName().equals(asset.getName())).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No " + assetName + " assets found for user with id " + user.getId()));
            Double usableSize = assetSell.getUsableSize();
            assetSell.setUsableSize(usableSize + order.getSize());
        }

        //soft delete
        order.setStatus(STATUS.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        userRepository.save(user);

        return convertToDTO(cancelledOrder);
    }
    

    public OrderDTO convertToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    public Order convertToEntity(OrderDTO orderDTO) {
        Order orderEntity = modelMapper.map(orderDTO, Order.class);
        orderEntity.setUser(userRepository.findById(orderDTO.getUserId()).orElseThrow(
            () -> new ResourceNotFoundException("User with id " + orderDTO.getUserId() + " is not found")
            ));
        return orderEntity;
    }

}
