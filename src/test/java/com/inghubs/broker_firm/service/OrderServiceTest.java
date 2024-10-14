package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.OrderDTO;
import com.inghubs.broker_firm.dto.UserDTO;
import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.entity.Order;
import com.inghubs.broker_firm.enums.ROLE;
import com.inghubs.broker_firm.enums.SIDE;
import com.inghubs.broker_firm.enums.STATUS;
import com.inghubs.broker_firm.repository.AssetRepository;
import com.inghubs.broker_firm.repository.UserRepository;
import com.inghubs.broker_firm.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.inghubs.broker_firm.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ModelMapper modelMapper;

    @BeforeAll
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void validateCreateSellOrder(){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setStatus(STATUS.PENDING);
        orderDTO.setPrice(10.0);
        orderDTO.setSize(5.0);
        orderDTO.setOrderSide(SIDE.SELL);
        orderDTO.setAssetName("USD");
        orderDTO.setUserId(UUID.randomUUID());
        orderDTO.setId(UUID.randomUUID());

        Order savedOrder = new Order();
        savedOrder.setStatus(orderDTO.getStatus());
        savedOrder.setPrice(orderDTO.getPrice());
        savedOrder.setSize(orderDTO.getSize());
        savedOrder.setOrderSide(orderDTO.getOrderSide());
        savedOrder.setAssetName(orderDTO.getAssetName());
        savedOrder.setId(orderDTO.getId());

        User savedUser = new User();
        User userAfter = new User();
        Asset asset = new Asset();
        Asset assetAfter = new Asset();

        asset.setSize(10.0);
        asset.setName("USD");
        asset.setUsableSize(5.0);
        asset.setUser(savedUser);

        assetAfter.setSize(asset.getSize());
        assetAfter.setName(asset.getName());
        assetAfter.setUsableSize(0.0);
        assetAfter.setUser(savedUser);

        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("cust1");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(ROLE.CUSTOMER);
        savedUser.setAssets(List.of(
            asset
        ));

        userAfter.setId(savedUser.getId());
        userAfter.setUsername(savedUser.getUsername());
        userAfter.setPassword(savedUser.getPassword());
        userAfter.setRole(savedUser.getRole());
        userAfter.setAssets(List.of(
            assetAfter
        ));
        savedOrder.setUser(userAfter);

        when(userRepository.findById(any(UUID.class))).
            thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).
            thenReturn(userAfter);
        when(orderRepository.save(any(Order.class))).
            thenReturn(savedOrder);
        when(modelMapper.map(orderDTO, Order.class)).
            thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderDTO.class)).
            thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertThat(result.getStatus()).isEqualTo(STATUS.PENDING);
    }

    @Test
    public void validateCreateBuyOrder(){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setStatus(STATUS.PENDING);
        orderDTO.setPrice(10.0);
        orderDTO.setSize(5.0);
        orderDTO.setOrderSide(SIDE.BUY);
        orderDTO.setAssetName("USD");
        orderDTO.setUserId(UUID.randomUUID());
        orderDTO.setId(UUID.randomUUID());

        Order savedOrder = new Order();
        savedOrder.setStatus(orderDTO.getStatus());
        savedOrder.setPrice(orderDTO.getPrice());
        savedOrder.setSize(orderDTO.getSize());
        savedOrder.setOrderSide(orderDTO.getOrderSide());
        savedOrder.setAssetName(orderDTO.getAssetName());
        savedOrder.setId(orderDTO.getId());

        User savedUser = new User();
        User userAfter = new User();
        Asset asset = new Asset();
        Asset assetAfter = new Asset();
        Asset assetTRY = new Asset();
        Asset assetTRYAfter = new Asset();

        asset.setSize(10.0);
        asset.setName("USD");
        asset.setUsableSize(5.0);
        asset.setUser(savedUser);

        assetTRY.setSize(100000.0);
        assetTRY.setName("TRY");
        assetTRY.setUsableSize(50000.0);
        assetTRY.setUser(savedUser);

        assetTRY.setSize(100000.0);
        assetTRY.setName("TRY");
        assetTRY.setUsableSize(49500.0);
        assetTRY.setUser(savedUser);

        assetAfter.setSize(asset.getSize());
        assetAfter.setName(asset.getName());
        assetAfter.setUsableSize(0.0);
        assetAfter.setUser(savedUser);

        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("cust1");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(ROLE.CUSTOMER);
        savedUser.setAssets(List.of(
            asset, assetTRY
        ));

        userAfter.setId(savedUser.getId());
        userAfter.setUsername(savedUser.getUsername());
        userAfter.setPassword(savedUser.getPassword());
        userAfter.setRole(savedUser.getRole());
        userAfter.setAssets(List.of(
            assetAfter, assetTRYAfter
        ));
        savedOrder.setUser(userAfter);

        when(userRepository.findById(any(UUID.class))).
            thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).
            thenReturn(userAfter);
        when(orderRepository.save(any(Order.class))).
            thenReturn(savedOrder);
        when(modelMapper.map(orderDTO, Order.class)).
            thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderDTO.class)).
            thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertThat(result.getStatus()).isEqualTo(STATUS.PENDING);
    }

    @Test
    public void validateBuyOrderMatching(){

    }

    @Test
    public void validateSellOrderMatching(){

    }

    @Test
    public void validateBuyOrderCancellation(){

    }

    @Test
    public void validateSellOrderCancellation(){

    }
}
