package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.UserDTO;
import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.entity.User;
import com.inghubs.broker_firm.enums.ROLE;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.exception.ResourceNotFoundException;
import com.inghubs.broker_firm.repository.AssetRepository;
import com.inghubs.broker_firm.repository.UserRepository;
import com.inghubs.broker_firm.request.CreateCustomerRequest;
import com.inghubs.broker_firm.request.AssetTransactionRequest;
import com.inghubs.broker_firm.util.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public List<UserDTO> getAllCustomers(){
        List<User> users = userRepository.findAllCustomers();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO getOneById(UUID id){
        User user = userRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Customer with id " + id + " does not exist"));
        return convertToDTO(user);
    }

    public UserDTO createCustomer(CreateCustomerRequest customerRequest) throws BadRequestException {
        if (userRepository.findByUsername(customerRequest.getUsername()).isPresent()){
           throw new BadRequestException("User with username " + customerRequest.getUsername() +
               " already exists, please choose another username.");
        }
        User user = new User();
        user.setUsername(customerRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(customerRequest.getPassword()));
        user.setRole(ROLE.CUSTOMER);

        User userEntity = userRepository.save(user);

        return convertToDTO(userRepository.save(userEntity));
    }

    @Transactional
    public UserDTO depositMoney(@PathVariable UUID id, @RequestBody AssetTransactionRequest depositRequest){
        String assetName = depositRequest.getAssetName();
        Double depositAmount = depositRequest.getAmount();
        if (!Constants.TRY.equals(assetName)){
            throw new BadRequestException("Only TRY can be deposited into a customer's balance, " + assetName + " is not a valid option to deposit.");
        }
        User user = userRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Customer with id " + id + " does not exist"));

        List<Asset> customerAssets = user.getAssets();
        Asset asset = customerAssets.stream().filter(customerAsset -> Constants.TRY.equals(customerAsset.getName())).findFirst().orElse(null);
        //if the customer has no TRY assets yet, then create one and save it
        if (asset == null){
            Asset newTRYAsset = new Asset();
            newTRYAsset.setUsableSize(depositAmount);
            newTRYAsset.setSize(depositAmount);
            newTRYAsset.setUser(user);
            newTRYAsset.setName(Constants.TRY);

            user.getAssets().add(newTRYAsset);
        } else {
            Double currentUsableSize = asset.getUsableSize();
            Double currentSize = asset.getSize();
            asset.setUsableSize(currentUsableSize + depositAmount);
            asset.setSize(currentSize + depositAmount);
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO withdrawMoney(@PathVariable UUID id, @RequestBody AssetTransactionRequest withdrawRequest){
        String assetName = withdrawRequest.getAssetName();
        Double withdrawAmount = withdrawRequest.getAmount();

        if (!Constants.TRY.equals(assetName)){
            throw new BadRequestException("Only TRY can be withdrawn into a customer's balance, " + assetName + " is not a valid option to withdraw.");
        }
        User user = userRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Customer with id " + id + " does not exist"));

        List<Asset> customerAssets = user.getAssets();
        Asset asset = null;

        if(customerAssets != null){
            asset = customerAssets.stream().filter(customerAsset -> Constants.TRY.equals(customerAsset.getName())).findFirst().orElse(null);
        } else {
            throw new BadRequestException("Customer has no assets to withdraw from.");
        }

        if (asset == null){
           throw new BadRequestException("There's no TRY balance for the customer with id " + user.getId());
        }else if(asset.getUsableSize() < withdrawAmount){
            throw new BadRequestException("There's not enough usable balance to withdraw " + withdrawAmount + " "
                + withdrawRequest.getAssetName() + " from this customer's asset. customer id: " + user.getId() + " "
                + " usable balance " + asset.getUsableSize());
        }
        // withdraw the money since we got the validations out of the way
        asset.setUsableSize(asset.getUsableSize() - withdrawAmount);
        asset.setSize(asset.getSize() - withdrawAmount);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public void deleteById(UUID id) throws BadRequestException {
        if (!userRepository.existsById(id)){
            throw new ResourceNotFoundException("Customer with id " + id + " does not exist");
        }
        userRepository.deleteById(id);
    }

    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    public User convertToEntity(UserDTO userDTO) {
        User userEntity = modelMapper.map(userDTO, User.class);
        return userEntity;
    }

}
