package com.inghubs.broker_firm.repository;

import com.inghubs.broker_firm.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {
    List<Asset> findByUserId(UUID userId);

    Optional<Asset> findByNameAndUserId(String name, UUID userId);
}
