package com.cdac.egov.integration.gepnic.controller;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.forms.aoc.AocDownloadForm;
import com.cdac.egov.integration.gepnic.forms.aoc.AocPullForm;
import com.cdac.egov.integration.gepnic.forms.aoc.AocResponseForm;
import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import com.cdac.egov.integration.gepnic.service.aoc.AocIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Screen 6: Award of Contract (AOC) Management and Document Downloads.
 */
@RestController
@RequestMapping(GePNICConstants.API_AOC)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AocIntegrationController {

    private final AocIntegrationService aocService;

    @PostMapping("/pull")
    public ResponseEntity<ApiResponseForm<List<AocResponseForm>>> pullAoc(@Valid @RequestBody AocPullForm form) {
        List<AocResponseForm> aocList = aocService.pullAocByCreatedDate(form);
        return ResponseEntity.ok(ApiResponseForm.ok(aocList, "Pulled " + aocList.size() + " AOC records"));
    }

    @PostMapping("/documents/download")
    public ResponseEntity<byte[]> downloadAocDocument(@Valid @RequestBody AocDownloadForm form) {
        byte[] fileBytes = aocService.downloadAocDocument(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + form.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }
}
