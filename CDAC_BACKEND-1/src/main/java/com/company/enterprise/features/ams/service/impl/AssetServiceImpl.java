package com.company.enterprise.features.ams.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.enterprise.core.exception.BusinessException;
import com.company.enterprise.core.exception.ResourceNotFoundException;
import com.company.enterprise.features.ams.dto.request.AssetRequest;
import com.company.enterprise.features.ams.dto.response.AssetResponse;
import com.company.enterprise.features.ams.entity.AssetEntity;
import com.company.enterprise.features.ams.repository.AssetRepository;
import com.company.enterprise.features.ams.service.AssetService;

@Service
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponse> getAll() {
        return assetRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getById(Long amsId) {
        return toResponse(findAsset(amsId));
    }

    @Override
    public AssetResponse create(AssetRequest request) {
        String assetCode = normalizeCode(request.assetCode());
        if (assetRepository.existsByAssetCodeIgnoreCase(assetCode)) {
            throw new BusinessException("An asset with this asset code already exists");
        }

        AssetEntity asset = new AssetEntity();
        applyRequest(asset, request, assetCode);
        return toResponse(assetRepository.save(asset));
    }

    @Override
    public AssetResponse update(Long amsId, AssetRequest request) {
        AssetEntity asset = findAsset(amsId);
        String assetCode = normalizeCode(request.assetCode());
        if (assetRepository.existsByAssetCodeIgnoreCaseAndAmsIdNot(assetCode, amsId)) {
            throw new BusinessException("An asset with this asset code already exists");
        }

        applyRequest(asset, request, assetCode);
        return toResponse(assetRepository.save(asset));
    }

    @Override
    public void delete(Long amsId) {
        assetRepository.delete(findAsset(amsId));
    }

    private AssetEntity findAsset(Long amsId) {
        return assetRepository.findById(amsId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset " + amsId + " was not found"));
    }

    private void applyRequest(AssetEntity asset, AssetRequest request, String assetCode) {
        asset.setAssetCode(assetCode);
        asset.setAssetName(request.assetName().trim());
        asset.setAssetType(request.assetType());
        asset.setDescription(request.description() == null ? "" : request.description().trim());
        asset.setLocation(request.location().trim());
        asset.setStatus(request.status());
    }

    private String normalizeCode(String assetCode) {
        return assetCode.trim().toUpperCase(Locale.ROOT);
    }

    private AssetResponse toResponse(AssetEntity asset) {
        return new AssetResponse(
                asset.getAmsId(),
                asset.getAssetCode(),
                asset.getAssetName(),
                asset.getAssetType(),
                asset.getDescription(),
                asset.getLocation(),
                asset.getStatus(),
                asset.getCreatedAt(),
                asset.getUpdatedAt());
    }
}
