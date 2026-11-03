package com.cdac.egov.wms.work.subwork.others.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.egov.wms.work.subwork.others.dto.OthersRequest;
import com.cdac.egov.wms.work.subwork.others.constants.OtherWorkConstants;
import com.cdac.egov.wms.work.subwork.others.entity.Others;
import com.cdac.egov.wms.work.subwork.others.entity.OthersAudit;
import com.cdac.egov.wms.work.subwork.others.service.OthersService;
import com.cdac.egov.wms.common.constants.CommonConstants;
import jakarta.validation.Valid;

@RestController
@RequestMapping(OtherWorkConstants.Api.BASE_PATH)
@CrossOrigin(origins = CommonConstants.LOCAL_FRONTEND_ORIGIN)
public class OthersController {

    private final OthersService service;

    public OthersController(OthersService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Others> list(
            @RequestParam(defaultValue = OtherWorkConstants.Api.PAGE_DEFAULT) int page,
            @RequestParam(defaultValue = OtherWorkConstants.Api.SIZE_DEFAULT) int size) {
        return service.list(page, size);
    }

    @GetMapping(OtherWorkConstants.Api.PATH_ID)
    public Others get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public Others create(@Valid @RequestBody OthersRequest request) {
        return service.create(request);
    }

    @PutMapping(OtherWorkConstants.Api.PATH_ID)
    public Others update(
            @PathVariable Long id,
            @Valid @RequestBody OthersRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping(OtherWorkConstants.Api.PATH_ID)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping(OtherWorkConstants.Api.PATH_AUDIT)
    public List<OthersAudit> audit(@PathVariable Long id) {
        return service.auditHistory(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(
                Map.of(CommonConstants.MESSAGE_KEY, exception.getMessage()));
    }
}
