package com.company.enterprise.features.scopes.service;

import java.util.List;

import com.company.enterprise.features.scopes.dto.request.WorkDetailsRequest;
import com.company.enterprise.features.scopes.dto.response.WorkDetailsResponse;

public interface WorkDetailsService {
    WorkDetailsResponse create(WorkDetailsRequest request);
    List<WorkDetailsResponse> getAll();
    WorkDetailsResponse getById(Long workId);
    WorkDetailsResponse update(Long workId, WorkDetailsRequest request);
    void delete(Long workId);
}
