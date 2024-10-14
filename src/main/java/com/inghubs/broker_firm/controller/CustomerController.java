package com.inghubs.broker_firm.controller;

import com.inghubs.broker_firm.dto.UserDTO;
import com.inghubs.broker_firm.request.CreateCustomerRequest;
import com.inghubs.broker_firm.request.AssetTransactionRequest;
import com.inghubs.broker_firm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllCustomers() {
        List<UserDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getCustomerById(@PathVariable UUID userId) {
        UserDTO customer = customerService.getOneById(userId);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createCustomer(@RequestBody CreateCustomerRequest customerRequest) {
        UserDTO createdCustomer = customerService.createCustomer(customerRequest);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/deposit")
    public ResponseEntity<UserDTO> depositMoney(@PathVariable UUID userId, @RequestBody AssetTransactionRequest depositRequest){
        UserDTO updatedCustomer = customerService.depositMoney(userId,depositRequest);
        return ResponseEntity.ok(updatedCustomer);
    }

    @PutMapping("/{userId}/withdraw")
    public ResponseEntity<UserDTO> withdrawMoney(@PathVariable UUID userId, @RequestBody AssetTransactionRequest withdrawRequest){
        UserDTO updatedCustomer = customerService.withdrawMoney(userId,withdrawRequest);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
