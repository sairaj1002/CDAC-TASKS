package com.cdac.bridge.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cdac.bridge.forms.BridgeAuditRevisionView;
import com.cdac.bridge.forms.BridgeComponentForm;
import com.cdac.bridge.forms.BridgeComponentView;
import com.cdac.bridge.service.BridgeComponentService;
import com.cdac.api.ApiSuccessResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bridge-components")
public class BridgeComponentController {

	private final BridgeComponentService service;

	public BridgeComponentController(BridgeComponentService service) {
		this.service = service;
	}

	@GetMapping
	public ApiSuccessResponse<List<BridgeComponentView>> findAll() {
		return ApiSuccessResponse.of(HttpStatus.OK, "Bridge components loaded.", service.findAll());
	}

	@PostMapping
	public ResponseEntity<ApiSuccessResponse<BridgeComponentView>> create(
			@Valid @RequestBody BridgeComponentForm form) {
		BridgeComponentView created = service.create(form);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location)
				.body(ApiSuccessResponse.of(HttpStatus.CREATED, "Bridge component created.", created));
	}

	@PutMapping("/{id}")
	public ApiSuccessResponse<BridgeComponentView> update(
			@PathVariable long id,
			@Valid @RequestBody BridgeComponentForm form) {
		return ApiSuccessResponse.of(HttpStatus.OK, "Bridge component updated.", service.update(id, form));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiSuccessResponse<Void>> delete(@PathVariable long id) {
		service.delete(id);
		return ResponseEntity.ok(ApiSuccessResponse.of(HttpStatus.OK, "Bridge component deleted.", null));
	}

	@GetMapping("/{id}/audit")
	public ApiSuccessResponse<List<BridgeAuditRevisionView>> findAuditHistory(@PathVariable long id) {
		return ApiSuccessResponse.of(HttpStatus.OK, "Bridge audit history loaded.", service.findAuditHistory(id));
	}
}
