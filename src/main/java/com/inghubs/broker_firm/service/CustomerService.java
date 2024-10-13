package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.CustomerDTO;
import com.inghubs.broker_firm.entity.Customer;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.exception.ResourceNotFoundException;
import com.inghubs.broker_firm.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CustomerDTO getOneById(UUID id){
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Customer with id " + id + " does not exist"));
        return convertToDTO(customer);
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws BadRequestException {
        Customer customerEntity = convertToEntity(customerDTO);
        return convertToDTO(customerRepository.save(customerEntity));
    }

    public CustomerDTO updateCustomer(CustomerDTO customerDTO) throws BadRequestException {
        if (!customerRepository.existsById(customerDTO.getId())){
            throw new ResourceNotFoundException("Customer with id " + customerDTO.getId() + " does not exist");
        }
        return convertToDTO(customerRepository.save(convertToEntity(customerDTO)));
    }

    public void deleteById(UUID id) throws BadRequestException {
        if (!customerRepository.existsById(id)){
            throw new ResourceNotFoundException("Customer with id " + id + " does not exist");
        }
        customerRepository.deleteById(id);
    }

    public CustomerDTO convertToDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    public Customer convertToEntity(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }

}
