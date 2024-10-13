package com.inghubs.broker_firm.controller;

import com.inghubs.broker_firm.dto.AssetDTO;
import com.inghubs.broker_firm.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @GetMapping
    public ResponseEntity<List<AssetDTO>> getAllAssets() {
        List<AssetDTO> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetDTO> getAssetById(@PathVariable UUID id) {
        AssetDTO asset = assetService.getOneById(id);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AssetDTO>> getAssetsByCustomerId(@PathVariable UUID customerId) {
        List<AssetDTO> assets = assetService.getByCustomerId(customerId);
        return ResponseEntity.ok(assets);
    }

    @PostMapping
    public ResponseEntity<AssetDTO> createAsset(@RequestBody AssetDTO assetDTO) {
        AssetDTO createdAsset = assetService.createAsset(assetDTO);
        return new ResponseEntity<>(createdAsset, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetDTO> updateAsset(@PathVariable UUID id, @RequestBody AssetDTO assetDTO) {
        assetDTO.setId(id);
        AssetDTO updatedAsset = assetService.updateAsset(assetDTO);
        return ResponseEntity.ok(updatedAsset);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable UUID id) {
        assetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
