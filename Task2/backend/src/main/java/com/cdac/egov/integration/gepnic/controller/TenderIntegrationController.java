package com.cdac.egov.integration.gepnic.controller;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import com.cdac.egov.integration.gepnic.forms.common.IntegrationResultForm;
import com.cdac.egov.integration.gepnic.forms.tender.*;
import com.cdac.egov.integration.gepnic.service.tender.TenderIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling Tender Push, Tender Pull, and Corrigendum Pull operations.
 * Implements Screen 3 (Tender JSON Push) and Screen 5 (Tender & Corrigendum Retrieval).
 */
@RestController
@RequestMapping(GePNICConstants.API_BASE_PATH)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TenderIntegrationController {

    private final TenderIntegrationService tenderService;

    @PostMapping("/tenders/push")
    public ResponseEntity<ApiResponseForm<IntegrationResultForm>> pushTender(@Valid @RequestBody TenderPushForm form) {
        IntegrationResultForm result = tenderService.pushTender(form);
        return ResponseEntity.ok(ApiResponseForm.ok(result, "Tender push processed: " + result.getMessage()));
    }

    @PostMapping("/tenders/pull")
    public ResponseEntity<ApiResponseForm<List<TenderResponseForm>>> pullTenders(@Valid @RequestBody TenderPullForm form) {
        List<TenderResponseForm> tenders = tenderService.pullTenders(form);
        return ResponseEntity.ok(ApiResponseForm.ok(tenders, "Pulled " + tenders.size() + " tenders"));
    }

    @PostMapping("/corrigendums/pull")
    public ResponseEntity<ApiResponseForm<List<CorrigendumResponseForm>>> pullCorrigendums(@Valid @RequestBody CorrigendumPullForm form) {
        List<CorrigendumResponseForm> corrigendums = tenderService.pullCorrigendums(form);
        return ResponseEntity.ok(ApiResponseForm.ok(corrigendums, "Pulled " + corrigendums.size() + " corrigendums"));
    }
}
