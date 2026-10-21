package com.cdac.road.controller;

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
import com.cdac.road.forms.RoadAuditRevisionView;
import com.cdac.road.forms.RoadComponentForm;
import com.cdac.road.forms.RoadComponentView;
import com.cdac.road.service.RoadComponentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/road-components")
public class RoadComponentController {

    private final RoadComponentService service;

    public RoadComponentController(RoadComponentService service) {
        this.service = service;
    }

    // Get all active road components
    @GetMapping
    public ApiSuccessResponse<List<RoadComponentView>> findAll() {
        return ApiSuccessResponse.of(
                HttpStatus.OK,
                "Road components loaded.",
                service.findAll());
    }

    // Create road component
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<RoadComponentView>> create(
            @Valid @RequestBody RoadComponentForm form) {

        RoadComponentView created = service.create(form);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(ApiSuccessResponse.of(
                        HttpStatus.CREATED,
                        "Road component created.",
                        created));
    }

    // Update road component
    @PutMapping("/{id}")
    public ApiSuccessResponse<RoadComponentView> update(
            @PathVariable long id,
            @Valid @RequestBody RoadComponentForm form) {

        return ApiSuccessResponse.of(
                HttpStatus.OK,
                "Road component updated.",
                service.update(id, form));
    }

    // Soft delete road component
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> delete(
            @PathVariable long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "Road component deleted.",
                        null));
    }

    // Get road component audit history
    @GetMapping("/{id}/audit")
    public ApiSuccessResponse<List<RoadAuditRevisionView>> findAuditHistory(
            @PathVariable long id) {

        return ApiSuccessResponse.of(
                HttpStatus.OK,
                "Road audit history loaded.",
                service.findAuditHistory(id));
    }
}