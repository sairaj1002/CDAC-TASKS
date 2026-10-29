package com.cdac.egov.integration.gepnic.controller;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.entity.DocumentTransaction;
import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import com.cdac.egov.integration.gepnic.forms.document.DocumentResponseForm;
import com.cdac.egov.integration.gepnic.forms.document.DocumentStatusForm;
import com.cdac.egov.integration.gepnic.forms.document.DocumentUploadForm;
import com.cdac.egov.integration.gepnic.service.document.DocumentIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller handling tender document multipart upload and status queries for Screen 4.
 */
@RestController
@RequestMapping(GePNICConstants.API_DOCUMENTS)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentIntegrationController {

    private final DocumentIntegrationService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseForm<DocumentResponseForm>> uploadDocument(
            @RequestPart("form") @Valid DocumentUploadForm form,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        DocumentResponseForm result = documentService.uploadDocument(form, file);
        return ResponseEntity.ok(ApiResponseForm.ok(result, "Document upload processed: " + result.getResponseMessage()));
    }

    @PostMapping("/status")
    public ResponseEntity<ApiResponseForm<DocumentResponseForm>> checkDocumentStatus(@Valid @RequestBody DocumentStatusForm form) {
        DocumentResponseForm result = documentService.checkDocumentStatus(form);
        return ResponseEntity.ok(ApiResponseForm.ok(result, "Document status retrieved"));
    }

    @GetMapping("/tender/{tenderRef}")
    public ResponseEntity<ApiResponseForm<List<DocumentTransaction>>> getDocumentsByTender(@PathVariable("tenderRef") String tenderRef) {
        List<DocumentTransaction> docs = documentService.getDocumentTransactions(tenderRef);
        return ResponseEntity.ok(ApiResponseForm.ok(docs, "Found " + docs.size() + " documents for tender"));
    }
}
