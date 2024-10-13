package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.AssetDTO;
import com.inghubs.broker_firm.dto.UserDTO;
import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.entity.User;
import com.inghubs.broker_firm.enums.ROLE;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.request.AssetTransactionRequest;
import com.inghubs.broker_firm.request.CreateCustomerRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.inghubs.broker_firm.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private CustomerService customerService;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void createCustomerExistingUsername() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setUsername("cust1");
        request.setPassword("cust123");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> customerService.createCustomer(request));
    }

    @Test
    public void createCustomerUniqueUsername() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setUsername("cust1");
        request.setPassword("cust123");

        when(userRepository.findByUsername(request.getUsername())).thenReturn((Optional.empty()));

        when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername(request.getUsername());
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(ROLE.CUSTOMER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("cust1");
        userDTO.setRole(ROLE.CUSTOMER);

        when(modelMapper.map(savedUser, UserDTO.class)).
            thenReturn(userDTO);

        UserDTO result = customerService.createCustomer(request);

        assertNotNull(result);
        assertEquals("cust1", result.getUsername());
        assertEquals(ROLE.CUSTOMER, result.getRole());
    }

    @Test
    public void depositMoney(){
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("cust1");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(ROLE.CUSTOMER);

        Asset asset = new Asset();
        asset.setSize(10.0);
        asset.setName("TRY");
        asset.setUsableSize(5.0);
        asset.setUser(savedUser);
        savedUser.setAssets(List.of(asset));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AssetTransactionRequest assetTransactionRequest = new AssetTransactionRequest();
        assetTransactionRequest.setAssetName("TRY");
        assetTransactionRequest.setAmount(100.0);

        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setSize(110.0);
        assetDTO.setUsableSize(105.0);
        assetDTO.setName("TRY");
        assetDTO.setUserId(savedUser.getId());

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("cust1");
        userDTO.setRole(ROLE.CUSTOMER);
        userDTO.setAssets(List.of(assetDTO));

        when(modelMapper.map(savedUser, UserDTO.class)).
            thenReturn(userDTO);

        UserDTO result = customerService.depositMoney(savedUser.getId(),assetTransactionRequest);
        assertThat(result.getAssets().get(0).getName()).isEqualTo("TRY");
        assertThat(result.getAssets().get(0).getSize()).isEqualTo(110.0);
        assertThat(result.getAssets().get(0).getUsableSize()).isEqualTo(105.0);
    }

    @Test
    public void validateCantDepositNonTRY(){
        AssetTransactionRequest assetTransactionRequest = new AssetTransactionRequest();
        assetTransactionRequest.setAssetName("EUR");
        assetTransactionRequest.setAmount(100.0);

        assertThrows(BadRequestException.class, () -> customerService.depositMoney(UUID.randomUUID(), assetTransactionRequest));
    }

    @Test
    public void withdrawMoney(){
            User savedUser = new User();
            savedUser.setId(UUID.randomUUID());
            savedUser.setUsername("cust1");
            savedUser.setPassword("hashedPassword");
            savedUser.setRole(ROLE.CUSTOMER);

            Asset asset = new Asset();
            asset.setSize(10.0);
            asset.setName("TRY");
            asset.setUsableSize(5.0);
            asset.setUser(savedUser);
            savedUser.setAssets(List.of(asset));
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(savedUser));
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            AssetTransactionRequest assetTransactionRequest = new AssetTransactionRequest();
            assetTransactionRequest.setAssetName("TRY");
            assetTransactionRequest.setAmount(5.0);

            AssetDTO assetDTO = new AssetDTO();
            assetDTO.setSize(5.0);
            assetDTO.setUsableSize(0.0);
            assetDTO.setName("TRY");
            assetDTO.setUserId(savedUser.getId());

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername("cust1");
            userDTO.setRole(ROLE.CUSTOMER);
            userDTO.setAssets(List.of(assetDTO));

            when(modelMapper.map(savedUser, UserDTO.class)).
                thenReturn(userDTO);

            UserDTO result = customerService.withdrawMoney(savedUser.getId(),assetTransactionRequest);
            assertThat(result.getAssets().get(0).getName()).isEqualTo("TRY");
            assertThat(result.getAssets().get(0).getSize()).isEqualTo(5.0);
            assertThat(result.getAssets().get(0).getUsableSize()).isEqualTo(0.0);
    }

    @Test
    public void validateCantWithdrawMoreThanBalance(){
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("cust1");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(ROLE.CUSTOMER);

        Asset asset = new Asset();
        asset.setSize(10.0);
        asset.setName("TRY");
        asset.setUsableSize(0.0);
        asset.setUser(savedUser);
        savedUser.setAssets(List.of(asset));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(savedUser));

        AssetTransactionRequest assetTransactionRequest = new AssetTransactionRequest();
        assetTransactionRequest.setAssetName("TRY");
        assetTransactionRequest.setAmount(5.0);

        assertThrows(BadRequestException.class, ()->customerService.withdrawMoney(savedUser.getId(), assetTransactionRequest));
    }

    @Test
    public void validateCantWithdrawIfNoTRYBalance(){
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("cust1");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(ROLE.CUSTOMER);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(savedUser));
        AssetTransactionRequest assetTransactionRequest = new AssetTransactionRequest();
        assetTransactionRequest.setAssetName("TRY");
        assetTransactionRequest.setAmount(5.0);

        assertThrows(BadRequestException.class, ()-> customerService.withdrawMoney(savedUser.getId(), assetTransactionRequest));
    }
}
