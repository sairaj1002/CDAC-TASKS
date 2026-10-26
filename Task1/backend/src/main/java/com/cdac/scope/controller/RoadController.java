package com.cdac.scope.controller;

import java.net.URI;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cdac.api.ApiSuccessResponse;
import com.cdac.scope.constant.RoadConstant;
import com.cdac.scope.forms.RoadAuditRevisionView;
import com.cdac.scope.forms.RoadForm;
import com.cdac.scope.forms.RoadView;
import com.cdac.scope.service.RoadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(RoadConstant.Api.BASE_PATH)
public class RoadController {

    private final RoadService service;

    public RoadController(RoadService service) {
        this.service = service;
    }

    // Get all active road components
    @GetMapping
    public ApiSuccessResponse<List<RoadView>> findAll() {
        return ApiSuccessResponse.of(
                HttpStatus.OK,
                RoadConstant.Messages.LOADED,
                service.findAll());
    }

    // Create road component
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<RoadView>> create(
            @Valid @RequestBody RoadForm form) {

        RoadView created = service.create(form);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(RoadConstant.Api.PATH_ID)
                .buildAndExpand(created.getScopeId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(ApiSuccessResponse.of(
                        HttpStatus.CREATED,
                        RoadConstant.Messages.CREATED,
                        created));
    }

    // Update road component
    @PutMapping(RoadConstant.Api.PATH_ID)
    public ApiSuccessResponse<RoadView> update(
            @PathVariable long id,
            @Valid @RequestBody RoadForm form) {

        return ApiSuccessResponse.of(
                HttpStatus.OK,
                RoadConstant.Messages.UPDATED,
                service.update(id, form));
    }

    // Soft delete road component
    @DeleteMapping(RoadConstant.Api.PATH_ID)
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
            @PathVariable long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiSuccessResponse.of(
                        HttpStatus.OK,
                        RoadConstant.Messages.DELETED,
                        null));
    }

    // Get road component audit history
    @GetMapping(RoadConstant.Api.PATH_AUDIT)
    public ApiSuccessResponse<List<RoadAuditRevisionView>> findAuditHistory(
            @PathVariable long id) {

        return ApiSuccessResponse.of(
                HttpStatus.OK,
                RoadConstant.Messages.AUDIT_HISTORY_LOADED,
                service.findAuditHistory(id));
    }
}
