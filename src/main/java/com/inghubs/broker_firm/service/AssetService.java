package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.AssetDTO;
import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.exception.ResourceNotFoundException;
import com.inghubs.broker_firm.repository.AssetRepository;
import com.inghubs.broker_firm.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<AssetDTO> getAllAssets(){
        List<Asset> assets = assetRepository.findAll();
        return assets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public AssetDTO getOneById(UUID id){
        Asset asset = assetRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Asset with id " + id + " does not exist"));
        return convertToDTO(asset);
    }

    public List<AssetDTO> getByCustomerId(UUID customerId){
        List<Asset> customerAssets = assetRepository.findByCustomerId(customerId);
        return customerAssets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public AssetDTO createAsset(AssetDTO assetDTO) throws BadRequestException {
        if (assetRepository.findByNameAndCustomerId(assetDTO.getName(), assetDTO.getCustomerId()).isPresent()){
           throw new BadRequestException("There's already an asset with name " + assetDTO.getName() +
               " exists for customer with id " + assetDTO.getCustomerId());
        }
        Asset assetEntity = convertToEntity(assetDTO);
        assetEntity.setCustomer(customerRepository.findById(assetDTO.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer with id: " + assetDTO.getCustomerId() + " provided in asset creation is not found.")));
        return convertToDTO(assetRepository.save(assetEntity));
    }

    public AssetDTO updateAsset(AssetDTO assetDTO) throws BadRequestException {
        if (!assetRepository.existsById(assetDTO.getId())){
            throw new ResourceNotFoundException("Asset with id " + assetDTO.getId() + " does not exist");
        }
        return convertToDTO(assetRepository.save(convertToEntity(assetDTO)));
    }

    public void deleteById(UUID id) throws BadRequestException {
        if (!assetRepository.existsById(id)){
           throw new ResourceNotFoundException("Asset with id " + id + " does not exist");
        }
        assetRepository.deleteById(id);
    }

    public AssetDTO convertToDTO(Asset asset) {
        return modelMapper.map(asset, AssetDTO.class);
    }

    public Asset convertToEntity(AssetDTO assetDTO) {
        return modelMapper.map(assetDTO, Asset.class);
    }
}
