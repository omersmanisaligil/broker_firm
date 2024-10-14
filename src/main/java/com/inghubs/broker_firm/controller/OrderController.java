package com.inghubs.broker_firm.controller;

import com.inghubs.broker_firm.dto.OrderDTO;
import com.inghubs.broker_firm.enums.SIDE;
import com.inghubs.broker_firm.enums.STATUS;
import com.inghubs.broker_firm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID id) {
        OrderDTO order = orderService.getOneById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/searchByCustomer/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerId(@PathVariable UUID userId) {
        List<OrderDTO> orders = orderService.getByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<OrderDTO>> filterOrders(
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
        List<OrderDTO> orders = orderService.filterOrders(
            userId,
            startDate,
            endDate,
            status,
            lowerPriceLimit,
            upperPriceLimit,
            lowerSize,
            upperSize,
            side,
            assetName
        );
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{userId}/create-order")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable UUID id) {
        OrderDTO cancelledOrder = orderService.deleteById(id);
        return ResponseEntity.ok(cancelledOrder);
    }
}
