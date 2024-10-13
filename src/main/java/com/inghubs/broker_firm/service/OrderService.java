package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.OrderDTO;
import com.inghubs.broker_firm.entity.Order;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.exception.ResourceNotFoundException;
import com.inghubs.broker_firm.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

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

    public List<OrderDTO> getByCustomerId(UUID customerId){
        List<Order> customerOrders = orderRepository.findByCustomerId(customerId);
        return customerOrders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrderDTO createOrder(OrderDTO orderDTO) throws BadRequestException {
        Order orderEntity = convertToEntity(orderDTO);
        return convertToDTO(orderRepository.save(orderEntity));
    }

    public OrderDTO updateOrder(OrderDTO orderDTO) throws BadRequestException {
        if (!orderRepository.existsById(orderDTO.getId())){
            throw new ResourceNotFoundException("Order with id " + orderDTO.getId() + " does not exist");
        }
        return convertToDTO(orderRepository.save(convertToEntity(orderDTO)));
    }

    public void deleteById(UUID id) throws BadRequestException {
        if (!orderRepository.existsById(id)){
            throw new ResourceNotFoundException("Order with id " + id + " does not exist");
        }
        orderRepository.deleteById(id);
    }

    public OrderDTO convertToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    public Order convertToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

}
