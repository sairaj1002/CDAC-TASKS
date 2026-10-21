package com.company.enterprise.features.ams.dto.response;

import java.time.LocalDateTime;

import com.company.enterprise.features.ams.entity.AssetStatus;
import com.company.enterprise.features.ams.entity.AssetType;

public record AssetResponse(
        Long amsId,
        String assetCode,
        String assetName,
        AssetType assetType,
        String description,
        String location,
        AssetStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
