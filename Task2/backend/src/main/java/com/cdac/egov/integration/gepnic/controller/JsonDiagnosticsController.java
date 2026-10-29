package com.cdac.egov.integration.gepnic.controller;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptionResultForm;
import com.cdac.egov.integration.gepnic.forms.tender.TenderPushForm;
import com.cdac.egov.integration.gepnic.mapper.WamisToGePNICMapper;
import com.cdac.egov.integration.gepnic.service.encryption.GePNICEncryptionService;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for Screen 8: JSON Preview & Validation.
 * Returns synthetic diagnostic stages during development. Disabled server-side in production.
 */
@RestController
@RequestMapping(GePNICConstants.API_DIAGNOSTICS)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JsonDiagnosticsController {

    private final GePNICProperties properties;
    private final WamisToGePNICMapper wamisMapper;
    private final GePNICJsonValidationService validationService;
    private final GePNICEncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    @PostMapping("/sample-flow")
    public ResponseEntity<ApiResponseForm<DiagnosticsFlowResponse>> getSampleDiagnosticsFlow(
            @RequestBody(required = false) TenderPushForm customForm) {

        if (!properties.isDiagnosticsEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseForm.error("Diagnostics endpoints are disabled in this environment", "DIAGNOSTICS_DISABLED"));
        }

        try {
            // Stage 1: Synthetic WAMIS Source Form
            TenderPushForm form = (customForm != null && customForm.getWamisTenderId() != null) ? customForm :
                    TenderPushForm.builder()
                            .wamisTenderId("WAMIS-TND-2026-000123")
                            .officeId("OFF-001")
                            .tenderTitle("Road Improvement Work")
                            .workDescription("Improvement of internal road")
                            .estimatedCost(BigDecimal.valueOf(2500000.00))
                            .tenderFee(BigDecimal.valueOf(1500.00))
                            .emdAmount(BigDecimal.valueOf(50000.00))
                            .publishDate(LocalDateTime.now().plusDays(1))
                            .submissionEndDate(LocalDateTime.now().plusDays(20))
                            .documents(List.of(
                                    TenderPushForm.TenderDocumentItemForm.builder()
                                            .documentId("DOC-101")
                                            .fileName("tender-notice.pdf")
                                            .contentType(".pdf")
                                            .fileSize(15240L)
                                            .docCode("NITDOC")
                                            .build()
                            ))
                            .build();

            String stage1WamisJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(form);

            // Stage 2: Mapped GePNIC JSON
            String stage2MappedJson = wamisMapper.mapToGePNICTenderJson(form, "WAMIS-SAMPLE-REF", "FILE-SAMPLE-001", 2297);
            Object mappedObj = objectMapper.readValue(stage2MappedJson, Object.class);
            String stage2PrettyMappedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mappedObj);

            // Stage 3: Validation Result
            List<String> validationErrors = validationService.validateTenderPushForm(form);
            boolean isValid = validationErrors.isEmpty();

            // Stage 4: Encrypted Request Envelope
            EncryptionResultForm encryptionResult = encryptionService.encryptPayload(stage2MappedJson);

            // Stage 5: Synthetic HTTP Response Envelope
            String stage5ResponseEnvelope = """
                    {
                      "uniqueUserId": "WAMIS-REGISTERED-USER-ID",
                      "encryptData": "V1RNU19SRVNQT05TRV9EQVRBX0VOQ1JZUFRFRA==",
                      "signData": "V1RNU19SRVNQT05TRV9TSUdOQVRVUkU=",
                      "encryptKey": "V1RNU19TRVNTSU9OX0tFWQ==",
                      "keyId": "GEPNIC-CERTIFICATE-THUMBPRINT-001"
                    }
                    """;

            // Stage 6: Decrypted GePNIC Response JSON
            String stage6DecryptedResponse = """
                    {
                      "responseCode": 1,
                      "responseMessage": "{\\"responseCode\\":101,\\"responseMessage\\":\\"Inserted Successfully\\"}"
                    }
                    """;

            // Stage 7: Final Normalized Result
            String stage7FinalResult = """
                    {
                      "transactionId": "TXN-2026-DIAG-0001",
                      "operation": "TENDER_PUSH",
                      "status": "SUCCESS",
                      "sourceSystem": "GePNIC",
                      "correlationId": "WAMIS-TND-2026-000123",
                      "recordsProcessed": 1,
                      "message": "Tender request received and processed successfully by GePNIC"
                    }
                    """;

            DiagnosticsFlowResponse flow = DiagnosticsFlowResponse.builder()
                    .stage1WamisJson(stage1WamisJson)
                    .stage2MappedJson(stage2PrettyMappedJson)
                    .stage3ValidationResult(isValid ? "VALID: Schema and business constraints passed" : "INVALID: " + validationErrors)
                    .stage4EncryptedEnvelope(encryptionResult.getEnvelope())
                    .stage5HttpResponseEnvelope(stage5ResponseEnvelope)
                    .stage6DecryptedResponse(stage6DecryptedResponse)
                    .stage7FinalNormalizedResult(stage7FinalResult)
                    .build();

            return ResponseEntity.ok(ApiResponseForm.ok(flow, "Diagnostics flow executed successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseForm.error("Diagnostics generation failed: " + e.getMessage(), "DIAGNOSTICS_ERROR"));
        }
    }

    @Data
    @Builder
    public static class DiagnosticsFlowResponse {
        private String stage1WamisJson;
        private String stage2MappedJson;
        private String stage3ValidationResult;
        private Object stage4EncryptedEnvelope;
        private String stage5HttpResponseEnvelope;
        private String stage6DecryptedResponse;
        private String stage7FinalNormalizedResult;
    }
}
