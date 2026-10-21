package com.company.enterprise.features.wms.service;

import java.util.List;

import com.company.enterprise.features.wms.dto.request.WorkCreateRequest;
import com.company.enterprise.features.wms.dto.request.WorkUpdateRequest;
import com.company.enterprise.features.wms.dto.response.WorkAuditResponse;
import com.company.enterprise.features.wms.dto.response.WorkResponse;

public interface WorkService {

    WorkResponse create(WorkCreateRequest request);

    List<WorkResponse> createMany(List<WorkCreateRequest> requests);

    WorkResponse getById(Long workRegNo);

    List<WorkResponse> getAll(String search);

    List<WorkAuditResponse> getAuditHistory(Long workRegNo);

    WorkResponse update(Long workRegNo, WorkUpdateRequest request);

    void delete(Long workRegNo);
}
