package com.cdac.egov.integration.gepnic.controller;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import com.cdac.egov.integration.gepnic.forms.metadata.MetadataCheckForm;
import com.cdac.egov.integration.gepnic.forms.metadata.MetadataStatusResponseForm;
import com.cdac.egov.integration.gepnic.service.metadata.MetadataIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Screen 7: Metadata & Document Status Synchronization Check.
 */
@RestController
@RequestMapping(GePNICConstants.API_METADATA)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetadataIntegrationController {

    private final MetadataIntegrationService metadataService;

    @PostMapping("/check")
    public ResponseEntity<ApiResponseForm<MetadataStatusResponseForm>> checkMetadataStatus(@Valid @RequestBody MetadataCheckForm form) {
        MetadataStatusResponseForm result = metadataService.checkMetadataStatus(form);
        return ResponseEntity.ok(ApiResponseForm.ok(result, "Metadata synchronization verified"));
    }
}
