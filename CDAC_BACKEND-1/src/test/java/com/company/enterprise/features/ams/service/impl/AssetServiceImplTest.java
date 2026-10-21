package com.company.enterprise.features.ams.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.company.enterprise.core.exception.BusinessException;
import com.company.enterprise.core.exception.ResourceNotFoundException;
import com.company.enterprise.features.ams.dto.request.AssetRequest;
import com.company.enterprise.features.ams.entity.AssetEntity;
import com.company.enterprise.features.ams.entity.AssetStatus;
import com.company.enterprise.features.ams.entity.AssetType;
import com.company.enterprise.features.ams.repository.AssetRepository;

class AssetServiceImplTest {

    @Test
    void createsAssetUsingTheFrontendFieldsAndNormalizesAssetCode() {
        AssetRepository repository = Mockito.mock(AssetRepository.class);
        when(repository.existsByAssetCodeIgnoreCase("ASSET-001")).thenReturn(false);
        when(repository.save(any(AssetEntity.class))).thenAnswer(invocation -> {
            AssetEntity saved = invocation.getArgument(0);
            saved.setAmsId(1L);
            return saved;
        });
        AssetServiceImpl service = new AssetServiceImpl(repository);

        var response = service.create(request(" asset-001 "));

        assertEquals(1L, response.amsId());
        assertEquals("ASSET-001", response.assetCode());
        assertEquals(AssetType.EQUIPMENT, response.assetType());
        assertEquals(AssetStatus.ACTIVE, response.status());
    }

    @Test
    void rejectsAnAssetCodeThatAlreadyExists() {
        AssetRepository repository = Mockito.mock(AssetRepository.class);
        when(repository.existsByAssetCodeIgnoreCase("ASSET-001")).thenReturn(true);
        AssetServiceImpl service = new AssetServiceImpl(repository);

        assertThrows(BusinessException.class, () -> service.create(request("asset-001")));
    }

    @Test
    void updateAllowsKeepingTheCurrentAssetCode() {
        AssetRepository repository = Mockito.mock(AssetRepository.class);
        AssetEntity existing = new AssetEntity();
        existing.setAmsId(7L);
        existing.setAssetCode("ASSET-001");
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        when(repository.existsByAssetCodeIgnoreCaseAndAmsIdNot("ASSET-001", 7L)).thenReturn(false);
        when(repository.save(any(AssetEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AssetServiceImpl service = new AssetServiceImpl(repository);

        var response = service.update(7L, request("asset-001"));

        assertEquals(7L, response.amsId());
        verify(repository).existsByAssetCodeIgnoreCaseAndAmsIdNot("ASSET-001", 7L);
    }

    @Test
    void reportsMissingAssetWhenGettingById() {
        AssetRepository repository = Mockito.mock(AssetRepository.class);
        when(repository.findById(404L)).thenReturn(Optional.empty());
        AssetServiceImpl service = new AssetServiceImpl(repository);

        assertThrows(ResourceNotFoundException.class, () -> service.getById(404L));
    }

    private AssetRequest request(String code) {
        return new AssetRequest(
                code,
                "Laptop",
                AssetType.EQUIPMENT,
                "Development equipment",
                "Building A",
                AssetStatus.ACTIVE);
    }
}
