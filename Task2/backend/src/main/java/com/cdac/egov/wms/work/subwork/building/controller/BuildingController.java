package com.cdac.egov.wms.work.subwork.building.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
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

import com.cdac.egov.wms.work.subwork.building.dto.BuildingRequest;
import com.cdac.egov.wms.work.subwork.building.constants.BuildingWorkConstants;
import com.cdac.egov.wms.work.subwork.building.entity.Building;
import com.cdac.egov.wms.work.subwork.building.entity.BuildingAudit;
import com.cdac.egov.wms.work.subwork.building.entity.DropdownOption;
import com.cdac.egov.wms.work.subwork.building.service.BuildingService;
import com.cdac.egov.wms.common.constants.CommonConstants;
import jakarta.validation.Valid;

@RestController
@RequestMapping(BuildingWorkConstants.Api.BASE_PATH)
@CrossOrigin(origins = CommonConstants.LOCAL_FRONTEND_ORIGIN)
public class BuildingController {

    private final BuildingService service;

    public BuildingController(BuildingService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Building> list(
            @RequestParam(defaultValue = BuildingWorkConstants.Api.PAGE_DEFAULT) int page,
            @RequestParam(defaultValue = BuildingWorkConstants.Api.SIZE_DEFAULT) int size) {
        return service.list(page, size);
    }

    @GetMapping(BuildingWorkConstants.Api.PATH_ID)
    public Building get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Building create(@Valid @RequestBody BuildingRequest request) {
        return service.create(request);
    }

    @PutMapping(
    	    value = BuildingWorkConstants.Api.PATH_ID,
    	    consumes = MediaType.APPLICATION_JSON_VALUE
    	)
	public Building update(
	        @PathVariable Long id,
	        @Valid @RequestBody BuildingRequest request) {
	    return service.update(id, request);
    	}

    @DeleteMapping(BuildingWorkConstants.Api.PATH_ID)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(BuildingWorkConstants.Api.PATH_AUDIT)
    public List<BuildingAudit> audit(@PathVariable Long id) {
        return service.auditHistory(id);
    }

    @GetMapping(BuildingWorkConstants.Api.PATH_OPTIONS)
    public List<DropdownOption> options(@RequestParam(BuildingWorkConstants.Api.PARAM_CATEGORY) String category) {
        return service.dropdown(category);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(
                Map.of(CommonConstants.MESSAGE_KEY, exception.getMessage()));
    }
}

