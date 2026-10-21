package com.company.enterprise.features.ams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.enterprise.features.ams.entity.AssetEntity;

public interface AssetRepository extends JpaRepository<AssetEntity, Long> {

    boolean existsByAssetCodeIgnoreCase(String assetCode);

    boolean existsByAssetCodeIgnoreCaseAndAmsIdNot(String assetCode, Long amsId);
}
