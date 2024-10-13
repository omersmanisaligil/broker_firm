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

    @GetMapping //TODO all
    public ResponseEntity<List<UserDTO>> getAllCustomers() {
        List<UserDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}") //TODO admin + customer special
    public ResponseEntity<UserDTO> getCustomerById(@PathVariable UUID id) {
        UserDTO customer = customerService.getOneById(id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createCustomer(@RequestBody CreateCustomerRequest customerRequest) {
        UserDTO createdCustomer = customerService.createCustomer(customerRequest);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/deposit")//TODO admin + customer
    public ResponseEntity<UserDTO> depositMoney(@PathVariable UUID id, @RequestBody AssetTransactionRequest depositRequest){
        UserDTO updatedCustomer = customerService.depositMoney(id,depositRequest);
        return ResponseEntity.ok(updatedCustomer);
    }

    @PutMapping("/{id}/withdraw")//TODO admin + customer
    public ResponseEntity<UserDTO> withdrawMoney(@PathVariable UUID id, @RequestBody AssetTransactionRequest withdrawRequest){
        UserDTO updatedCustomer = customerService.withdrawMoney(id,withdrawRequest);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}") //TODO admin + customer
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
