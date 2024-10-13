package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.UserDTO;
import com.inghubs.broker_firm.entity.User;
import com.inghubs.broker_firm.request.CreateUserRequest;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.exception.ResourceNotFoundException;
import com.inghubs.broker_firm.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ModelMapper modelMapper;

    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO getOneById(UUID id){
        User user = userRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("User with id " + id + " does not exist"));
        return convertToDTO(user);
    }

    public UserDTO createUser(CreateUserRequest createUserRequest) throws BadRequestException {
        if (userRepository.findByUsername(createUserRequest.getUsername()).isPresent()){
           throw new BadRequestException("User with name " + createUserRequest.getUsername() + " already exists.");
        }

        User userEntity = new User();
        userEntity.setUsername(createUserRequest.getUsername());
        userEntity.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userEntity.setRole(createUserRequest.getRole());

        return convertToDTO(userRepository.save(userEntity));
    }

    public UserDTO updateUser(UserDTO userDTO) throws BadRequestException {
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        if (optionalUser.isEmpty()){
            throw new ResourceNotFoundException("User with id " + userDTO.getId() + " does not exist");
        }
        User toUpdate = optionalUser.get();
        toUpdate.setUsername(userDTO.getUsername());
        toUpdate.setRole(userDTO.getRole());

        User saved = userRepository.save(toUpdate);
        return convertToDTO(saved);
    }

    public void deleteById(UUID id) throws BadRequestException {
        if (!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User with id " + id + " does not exist");
        }
        userRepository.deleteById(id);
    }

    public UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
