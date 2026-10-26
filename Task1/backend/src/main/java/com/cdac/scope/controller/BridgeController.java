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
import com.cdac.scope.constant.BridgeConstant;
import com.cdac.scope.forms.BridgeAuditRevisionView;
import com.cdac.scope.forms.BridgeForm;
import com.cdac.scope.forms.BridgeView;
import com.cdac.scope.service.BridgeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(BridgeConstant.Api.BASE_PATH)
public class BridgeController {

	private final BridgeService service;

	public BridgeController(BridgeService service) {
		this.service = service;
	}

	@GetMapping
	public ApiSuccessResponse<List<BridgeView>> findAll() {
		return ApiSuccessResponse.of(HttpStatus.OK, BridgeConstant.Messages.LOADED, service.findAll());
	}

	@PostMapping
	public ResponseEntity<ApiSuccessResponse<BridgeView>> create(
			@Valid @RequestBody BridgeForm form) {
		BridgeView created = service.create(form);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path(BridgeConstant.Api.PATH_ID)
				.buildAndExpand(created.getScopeId())
				.toUri();
		return ResponseEntity.created(location)
				.body(ApiSuccessResponse.of(HttpStatus.CREATED, BridgeConstant.Messages.CREATED, created));
	}

	@PutMapping(BridgeConstant.Api.PATH_ID)
	public ApiSuccessResponse<BridgeView> update(
			@PathVariable long id,
			@Valid @RequestBody BridgeForm form) {
		return ApiSuccessResponse.of(HttpStatus.OK, BridgeConstant.Messages.UPDATED, service.update(id, form));
	}

	@DeleteMapping(BridgeConstant.Api.PATH_ID)
	public ResponseEntity<ApiSuccessResponse<Void>> delete(@PathVariable long id) {
		service.delete(id);
		return ResponseEntity.ok(ApiSuccessResponse.of(HttpStatus.OK, BridgeConstant.Messages.DELETED, null));
	}

	@GetMapping(BridgeConstant.Api.PATH_AUDIT)
	public ApiSuccessResponse<List<BridgeAuditRevisionView>> findAuditHistory(@PathVariable long id) {
		return ApiSuccessResponse.of(HttpStatus.OK, BridgeConstant.Messages.AUDIT_HISTORY_LOADED, service.findAuditHistory(id));
	}
}
