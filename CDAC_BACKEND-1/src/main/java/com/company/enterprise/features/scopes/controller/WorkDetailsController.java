package com.company.enterprise.features.scopes.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.company.enterprise.core.response.ApiResponse;
import com.company.enterprise.features.scopes.dto.request.WorkDetailsRequest;
import com.company.enterprise.features.scopes.dto.response.WorkDetailsResponse;
import com.company.enterprise.features.scopes.service.WorkDetailsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/scopes/work-details")
public class WorkDetailsController {

    private final WorkDetailsService service;

    public WorkDetailsController(WorkDetailsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkDetailsResponse>> create(
            @Valid @RequestBody WorkDetailsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Work details created successfully", service.create(request)));
    }

    @GetMapping
    public ApiResponse<List<WorkDetailsResponse>> getAll() {
        return ApiResponse.success("Work details retrieved successfully", service.getAll());
    }

    @GetMapping("/{workId}")
    public ApiResponse<WorkDetailsResponse> getById(@PathVariable Long workId) {
        return ApiResponse.success("Work details retrieved successfully", service.getById(workId));
    }

    @PutMapping("/{workId}")
    public ApiResponse<WorkDetailsResponse> update(
            @PathVariable Long workId,
            @Valid @RequestBody WorkDetailsRequest request) {
        return ApiResponse.success("Work details updated successfully", service.update(workId, request));
    }

    @DeleteMapping("/{workId}")
    public ApiResponse<Void> delete(@PathVariable Long workId) {
        service.delete(workId);
        return ApiResponse.success("Work details deleted successfully", null);
    }
}
