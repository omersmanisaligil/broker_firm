package com.inghubs.broker_firm.service;

import com.inghubs.broker_firm.dto.AssetDTO;
import com.inghubs.broker_firm.entity.Asset;
import com.inghubs.broker_firm.exception.BadRequestException;
import com.inghubs.broker_firm.exception.ResourceNotFoundException;
import com.inghubs.broker_firm.repository.AssetRepository;
import com.inghubs.broker_firm.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EntityManager entityManager;

    public List<AssetDTO> getAllAssets(){
        List<Asset> assets = assetRepository.findAll();
        return assets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public AssetDTO getOneById(UUID id){
        Asset asset = assetRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Asset with id " + id + " does not exist"));
        return convertToDTO(asset);
    }

    public List<AssetDTO> getByUserId(UUID userId){
        List<Asset> userAssets = assetRepository.findByUserId(userId);
        return userAssets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<AssetDTO> filterAssets(
        @RequestParam(required = false) UUID userId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Double lowerSizeLimit,
        @RequestParam(required = false) Double upperSizeLimit,
        @RequestParam(required = false) Double lowerUsableLimit,
        @RequestParam(required = false) Double upperUsableLimit
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Asset> cq = cb.createQuery(Asset.class);

        Root<Asset> orderRoot = cq.from(Asset.class);
        List<Predicate> predicates = new ArrayList<>();

        if (userId != null) {
            predicates.add(cb.equal(orderRoot.get("user").get("id"), userId));
        }
        if (name != null){
            predicates.add(cb.equal(orderRoot.get("name"), name));
        }
        if (lowerSizeLimit != null){
            predicates.add(cb.greaterThanOrEqualTo(orderRoot.get("size"), lowerSizeLimit));
        }
        if (upperSizeLimit != null) {
            predicates.add(cb.lessThanOrEqualTo(orderRoot.get("size"), upperSizeLimit));
        }
        if (lowerUsableLimit != null){
            predicates.add(cb.greaterThanOrEqualTo(orderRoot.get("usableSize"), lowerUsableLimit));
        }
        if (upperUsableLimit != null){
            predicates.add(cb.lessThanOrEqualTo(orderRoot.get("usableSize"), upperUsableLimit));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<Asset> ordersFiltered = entityManager.createQuery(cq).getResultList();
        return ordersFiltered.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

        public AssetDTO createAsset(AssetDTO assetDTO) throws BadRequestException {
        if (assetRepository.findByNameAndUserId(assetDTO.getName(), assetDTO.getUserId()).isPresent()){
           throw new BadRequestException("There's already an asset with name " + assetDTO.getName() +
               " exists for user with id " + assetDTO.getUserId());
        }
        if (assetDTO.getUsableSize()>assetDTO.getSize()){
            throw new BadRequestException("Usable size cannot be greater than full size.");
        }
        Asset assetEntity = convertToEntity(assetDTO);
        assetEntity.setUser(userRepository.findById(assetDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User with id: " + assetDTO.getUserId() + " provided in asset creation is not found.")));
        return convertToDTO(assetRepository.save(assetEntity));
    }

    public AssetDTO updateAsset(AssetDTO assetDTO) throws BadRequestException {
        if (!assetRepository.existsById(assetDTO.getId())){
            throw new ResourceNotFoundException("Asset with id " + assetDTO.getId() + " does not exist");
        }
        if (assetDTO.getUsableSize()>assetDTO.getSize()){
            throw new BadRequestException("Usable size cannot be greater than full size.");
        }

        Asset assetEntity = convertToEntity(assetDTO);
        assetEntity.setUser(userRepository.findById(assetDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User with id: " + assetDTO.getUserId() + " provided in asset creation is not found.")));
        return convertToDTO(assetRepository.save(assetEntity));
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
