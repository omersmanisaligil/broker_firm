package com.inghubs.broker_firm.controller;

import com.inghubs.broker_firm.dto.OrderDTO;
import com.inghubs.broker_firm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;

    @PatchMapping("/match-order/{orderId}")
    public ResponseEntity<OrderDTO> matchOrder(@PathVariable UUID orderId){
        OrderDTO order = orderService.matchPendingOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

}
