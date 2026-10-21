package com.cdac.bridge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.api.ApiSuccessResponse;
import com.cdac.bridge.service.BridgeComponentService;

@RestController
@RequestMapping("/api/lookups")
public class BridgeDropdownController {

	private final BridgeComponentService service;

	public BridgeDropdownController(BridgeComponentService service) {
		this.service = service;
	}

	@GetMapping("/bridge-activities")
	public ApiSuccessResponse<List<String>> findBridgeActivities() {
		return ApiSuccessResponse.of(
				HttpStatus.OK,
				"Bridge activities loaded.",
				service.findBridgeActivities());
	}
}
