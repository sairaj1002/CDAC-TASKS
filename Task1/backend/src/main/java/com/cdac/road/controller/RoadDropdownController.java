package com.cdac.road.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.api.ApiSuccessResponse;
import com.cdac.road.service.RoadComponentService;


@RestController
@RequestMapping("/api/lookups")
public class RoadDropdownController {

    private final RoadComponentService service;

    public RoadDropdownController(RoadComponentService service) {
        this.service = service;
    }

    @GetMapping("/nature-of-works")
    public ApiSuccessResponse<List<String>> findNatureOfWorks() {
        return ApiSuccessResponse.of(
                HttpStatus.OK,
                "Nature of works loaded.",
                service.findNatureOfWorks());
    }

    @GetMapping("/proposed-road-types")
    public ApiSuccessResponse<List<String>> findProposedRoadTypes() {
        return ApiSuccessResponse.of(
                HttpStatus.OK,
                "Proposed road types loaded.",
                service.findProposedRoadTypes());
    }

    @GetMapping("/cd-work-types")
    public ApiSuccessResponse<List<String>> findCdWorkTypes() {
        return ApiSuccessResponse.of(
                HttpStatus.OK,
                "CD work types loaded.",
                service.findCdWorkTypes());
    }

    @GetMapping("/repair-maintenance-types")
    public ApiSuccessResponse<List<String>> findRepairMaintenanceTypes() {
        return ApiSuccessResponse.of(
                HttpStatus.OK,
                "Repair and maintenance types loaded.",
                service.findRepairMaintenanceTypes());
    }

    @GetMapping("/work-plan-types")
    public ApiSuccessResponse<List<String>> findWorkPlanTypes() {
        return ApiSuccessResponse.of(
                HttpStatus.OK,
                "Work plan types loaded.",
                service.findWorkPlanTypes());
    }
}
