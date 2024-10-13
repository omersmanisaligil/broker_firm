package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.AssetDTO;
import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.repository.AssetRepository;
import com.inghubs.broker_firm.repository.UserRepository;
import jakarta.persistence.EntityManager;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AssetServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private AssetService assetService;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void validateUniqueConstraintInCreatingAsset(){
        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setUserId(UUID.randomUUID());
        assetDTO.setSize(15.0);
        assetDTO.setUsableSize(5.0);

        when(assetRepository.findByNameAndUserId(assetDTO.getName(), assetDTO.getUserId())).thenReturn(Optional.of(new Asset()));
        assertThrows(BadRequestException.class, () -> assetService.createAsset(assetDTO));
    }

    @Test
    public void validateUsableSizeCannotBeGTSize(){
        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setUserId(UUID.randomUUID());
        assetDTO.setSize(5.0);
        assetDTO.setUsableSize(15.0);

        when(assetRepository.findByNameAndUserId(assetDTO.getName(), assetDTO.getUserId())).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> assetService.createAsset(assetDTO));
    }

}
