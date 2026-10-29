package com.cdac.egov.integration.gepnic.controller;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Screen 2: Pre-Tender Configuration & Office Lookup.
 */
@RestController
@RequestMapping(GePNICConstants.API_OFFICES)
@CrossOrigin(origins = "*")
public class OfficeLookupController {

    @GetMapping
    public ResponseEntity<ApiResponseForm<List<OfficeLookupItem>>> getPermittedOffices(
            @RequestParam(value = "search", required = false) String search) {

        // Returns permitted offices mapped with NIC organization IDs
        List<OfficeLookupItem> offices = List.of(
                OfficeLookupItem.builder()
                        .officeId("OFF-001")
                        .officeCode("PWD-PUN-01")
                        .officeName("Public Works Division, Pune Central")
                        .officeHead("Er. Rajesh Sharma, SE")
                        .nicOrgId(2297)
                        .preTenderConfigured(true)
                        .certificateConfigured(true)
                        .status("ACTIVE")
                        .build(),
                OfficeLookupItem.builder()
                        .officeId("OFF-002")
                        .officeCode("WRD-NSK-02")
                        .officeName("Water Resources Circle, Nashik")
                        .officeHead("Er. Anita Deshmukh, EE")
                        .nicOrgId(2298)
                        .preTenderConfigured(true)
                        .certificateConfigured(true)
                        .status("ACTIVE")
                        .build(),
                OfficeLookupItem.builder()
                        .officeId("OFF-003")
                        .officeCode("MJP-NGP-03")
                        .officeName("Maharashtra Jeevan Pradhikaran, Nagpur")
                        .officeHead("Er. Suresh Kulkarni, CE")
                        .nicOrgId(2299)
                        .preTenderConfigured(false)
                        .certificateConfigured(true)
                        .status("CONFIG_PENDING")
                        .build()
        );

        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();
            offices = offices.stream()
                    .filter(o -> o.getOfficeName().toLowerCase().contains(lower) || o.getOfficeCode().toLowerCase().contains(lower))
                    .toList();
        }

        return ResponseEntity.ok(ApiResponseForm.ok(offices, "Permitted offices retrieved successfully"));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfficeLookupItem {
        private String officeId;
        private String officeCode;
        private String officeName;
        private String officeHead;
        private int nicOrgId;
        private boolean preTenderConfigured;
        private boolean certificateConfigured;
        private String status;
    }
}
