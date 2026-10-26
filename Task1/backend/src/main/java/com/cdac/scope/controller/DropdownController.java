package com.cdac.scope.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.api.ApiSuccessResponse;
import com.cdac.scope.constant.DropdownConstant;
import com.cdac.scope.forms.CreateDropdownOptionRequest;
import com.cdac.scope.forms.DropdownOptionDto;
import com.cdac.scope.forms.UpdateDropdownOptionRequest;
import com.cdac.scope.service.DropdownOptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(DropdownConstant.Api.BASE_PATH)
public class DropdownController {

    private final DropdownOptionService dropdownService;

    public DropdownController(DropdownOptionService dropdownService) {
        this.dropdownService = dropdownService;
    }

    /**
     * Get active (or all) options for a given category.
     */
    @GetMapping(DropdownConstant.Api.PATH_CATEGORY)
    public ApiSuccessResponse<List<DropdownOptionDto>> getOptionsByCategory(
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "false") boolean includeInactive) {
        List<DropdownOptionDto> options = includeInactive
                ? dropdownService.findAllByCategory(category)
                : dropdownService.findActiveByCategory(category);
        return ApiSuccessResponse.of(HttpStatus.OK, DropdownConstant.Messages.OPTIONS_LOADED, options);
    }

    /**
     * Get all dropdown options, optionally filtered by category.
     */
    @GetMapping
    public ApiSuccessResponse<List<DropdownOptionDto>> getAllOptions(
            @RequestParam(required = false) String category) {
        List<DropdownOptionDto> options = dropdownService.findAll(category);
        return ApiSuccessResponse.of(HttpStatus.OK, DropdownConstant.Messages.ALL_OPTIONS_LOADED, options);
    }

    /**
     * Get list of all distinct categories.
     */
    @GetMapping(DropdownConstant.Api.PATH_CATEGORIES)
    public ApiSuccessResponse<List<String>> getCategories() {
        List<String> categories = dropdownService.getCategories();
        return ApiSuccessResponse.of(HttpStatus.OK, DropdownConstant.Messages.CATEGORIES_LOADED, categories);
    }

    /**
     * Get a specific option by ID.
     */
    @GetMapping(DropdownConstant.Api.PATH_ITEM_ID)
    public ApiSuccessResponse<DropdownOptionDto> getOptionById(@PathVariable Long id) {
        DropdownOptionDto dto = dropdownService.findById(id);
        return ApiSuccessResponse.of(HttpStatus.OK, DropdownConstant.Messages.OPTION_LOADED, dto);
    }

    /**
     * Create a new dropdown option.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiSuccessResponse<DropdownOptionDto> createOption(
            @Valid @RequestBody CreateDropdownOptionRequest request) {
        DropdownOptionDto created = dropdownService.createOption(request);
        return ApiSuccessResponse.of(HttpStatus.CREATED, DropdownConstant.Messages.CREATED, created);
    }

    /**
     * Update an existing dropdown option.
     */
    @PutMapping(DropdownConstant.Api.PATH_ID)
    public ApiSuccessResponse<DropdownOptionDto> updateOption(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDropdownOptionRequest request) {
        DropdownOptionDto updated = dropdownService.updateOption(id, request);
        return ApiSuccessResponse.of(HttpStatus.OK, DropdownConstant.Messages.UPDATED, updated);
    }

    /**
     * Activate or deactivate a dropdown option.
     */
    @PatchMapping(DropdownConstant.Api.PATH_STATUS)
    public ApiSuccessResponse<DropdownOptionDto> toggleActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        DropdownOptionDto updated = dropdownService.toggleActive(id, active);
        String msg = active ? DropdownConstant.Messages.ACTIVATED : DropdownConstant.Messages.DEACTIVATED;
        return ApiSuccessResponse.of(HttpStatus.OK, msg, updated);
    }

    /**
     * Delete an unreferenced dropdown option.
     */
    @DeleteMapping(DropdownConstant.Api.PATH_ID)
    public ApiSuccessResponse<Void> deleteOption(@PathVariable Long id) {
        dropdownService.deleteOption(id);
        return ApiSuccessResponse.of(HttpStatus.OK, DropdownConstant.Messages.DELETED, null);
    }
}
