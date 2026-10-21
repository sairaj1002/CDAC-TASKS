package com.company.enterprise.features.ams.service;

import java.util.List;

import com.company.enterprise.features.ams.dto.request.AssetRequest;
import com.company.enterprise.features.ams.dto.response.AssetResponse;

public interface AssetService {

    List<AssetResponse> getAll();

    AssetResponse getById(Long amsId);

    AssetResponse create(AssetRequest request);

    AssetResponse update(Long amsId, AssetRequest request);

    void delete(Long amsId);
}
