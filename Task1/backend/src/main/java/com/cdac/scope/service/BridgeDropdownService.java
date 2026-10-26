package com.cdac.scope.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cdac.scope.forms.DropdownOptionDto;

/**
 * Backward-compatible delegate for Bridge dropdown options.
 * Points to the consolidated DropdownOptionService.
 */
@Service
public class BridgeDropdownService {

    private final DropdownOptionService dropdownService;

    public BridgeDropdownService(DropdownOptionService dropdownService) {
        this.dropdownService = dropdownService;
    }

    public static String normalizeCategory(String category) {
        return DropdownOptionService.normalizeCategory(category);
    }

    public List<DropdownOptionDto> findActiveByCategory(String category) {
        return dropdownService.findActiveByCategory(category);
    }

    public List<DropdownOptionDto> findAllByCategory(String category) {
        return dropdownService.findAllByCategory(category);
    }

    public void validateActiveCode(String category, String code) {
        dropdownService.validateActiveCode(category, code);
    }

    public String getLabel(String category, String code) {
        return dropdownService.getLabel(category, code);
    }

    public void clearCache() {
        dropdownService.clearCache();
    }
}
