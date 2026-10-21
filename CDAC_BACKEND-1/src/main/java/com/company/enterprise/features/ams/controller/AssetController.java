package com.company.enterprise.features.ams.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.enterprise.features.ams.dto.request.AssetRequest;
import com.company.enterprise.features.ams.dto.response.AssetResponse;
import com.company.enterprise.features.ams.service.AssetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ams")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public List<AssetResponse> getAll() {
        return assetService.getAll();
    }

    @GetMapping("/{amsId}")
    public AssetResponse getById(@PathVariable Long amsId) {
        return assetService.getById(amsId);
    }

    @PostMapping
    public ResponseEntity<AssetResponse> create(@Valid @RequestBody AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(request));
    }

    @PutMapping("/{amsId}")
    public AssetResponse update(
            @PathVariable Long amsId,
            @Valid @RequestBody AssetRequest request) {
        return assetService.update(amsId, request);
    }

    @DeleteMapping("/{amsId}")
    public ResponseEntity<String> delete(@PathVariable Long amsId) {
        assetService.delete(amsId);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Asset deleted successfully");
    }
}
