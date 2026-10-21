package com.company.enterprise.features.ams.dto.request;

import com.company.enterprise.features.ams.entity.AssetStatus;
import com.company.enterprise.features.ams.entity.AssetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssetRequest(
        @NotBlank(message = "Asset code is required")
        @Size(max = 50, message = "Asset code cannot exceed 50 characters")
        String assetCode,

        @NotBlank(message = "Asset name is required")
        @Size(max = 100, message = "Asset name cannot exceed 100 characters")
        String assetName,

        @NotNull(message = "Asset type is required")
        AssetType assetType,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotBlank(message = "Location is required")
        @Size(max = 150, message = "Location cannot exceed 150 characters")
        String location,

        @NotNull(message = "Asset status is required")
        AssetStatus status) {
}
