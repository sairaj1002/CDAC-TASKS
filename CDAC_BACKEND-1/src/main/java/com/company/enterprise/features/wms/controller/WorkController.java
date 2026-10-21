package com.company.enterprise.features.wms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.enterprise.core.response.ApiResponse;
import com.company.enterprise.features.wms.dto.request.WorkCreateRequest;
import com.company.enterprise.features.wms.dto.request.WorkUpdateRequest;
import com.company.enterprise.features.wms.dto.response.WorkAuditResponse;
import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.service.WorkService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/work")
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkResponse>> create(@Valid @RequestBody WorkCreateRequest request) {
        WorkResponse response = workService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Work created successfully", response));
    }

    @GetMapping
    public ApiResponse<List<WorkResponse>> getAll(
            @RequestParam(required = false) String search) {
        return ApiResponse.success("Work records retrieved successfully", workService.getAll(search));
    }

    @GetMapping("/{workRegNo}")
    public ApiResponse<WorkResponse> getById(@PathVariable Long workRegNo) {
        return ApiResponse.success("Work record retrieved successfully", workService.getById(workRegNo));
    }

    @GetMapping("/{workRegNo}/audit")
    public ApiResponse<List<WorkAuditResponse>> getAuditHistory(@PathVariable Long workRegNo) {
        return ApiResponse.success("Work audit history retrieved successfully",
                workService.getAuditHistory(workRegNo));
    }

    @PutMapping("/{workRegNo}")
    public ApiResponse<WorkResponse> update(
            @PathVariable Long workRegNo,
            @Valid @RequestBody WorkUpdateRequest request) {
        return ApiResponse.success("Work updated successfully", workService.update(workRegNo, request));
    }

    @DeleteMapping("/{workRegNo}")
    public ApiResponse<Void> delete(@PathVariable Long workRegNo) {
        workService.delete(workRegNo);
        return ApiResponse.success("Work deleted successfully", null);
    }
}
