package in.gov.wamis.integration.gepnic.controller;

import in.gov.wamis.audit.entity.IntegrationAuditEvent;
import in.gov.wamis.audit.form.AuditEventResponseForm;
import in.gov.wamis.audit.service.IntegrationAuditService;
import in.gov.wamis.common.constants.ApiConstants;
import in.gov.wamis.common.constants.CommonConstants;
import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.common.enums.PortalCode;
import in.gov.wamis.common.exception.BusinessValidationException;
import in.gov.wamis.common.security.SecurityContextHelper;
import in.gov.wamis.integration.common.IntegrationResult;
import in.gov.wamis.integration.common.PortalRequest;
import in.gov.wamis.integration.common.PortalResponse;
import in.gov.wamis.integration.gepnic.GePNICAdapter;
import in.gov.wamis.integration.gepnic.config.GePNICProperties;
import in.gov.wamis.integration.gepnic.form.*;
import in.gov.wamis.integration.transaction.entity.IntegrationTransaction;
import in.gov.wamis.integration.transaction.form.IntegrationJobResponseForm;
import in.gov.wamis.integration.transaction.service.IntegrationTransactionService;
import in.gov.wamis.procurement.pretender.PreTenderService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller exposing internal WAMIS routes for GePNIC operations and transaction management.
 * As defined in Section 7 of the development specification.
 */
@RestController
@RequestMapping(ApiConstants.GEPNIC_BASE)
public class GePNICController {

    private final GePNICAdapter gepnicAdapter;
    private final PreTenderService preTenderService;
    private final IntegrationTransactionService transactionService;
    private final IntegrationAuditService auditService;
    private final GePNICProperties properties;
    private final SecurityContextHelper securityContextHelper;

    public GePNICController(GePNICAdapter gepnicAdapter,
                            PreTenderService preTenderService,
                            IntegrationTransactionService transactionService,
                            IntegrationAuditService auditService,
                            GePNICProperties properties,
                            SecurityContextHelper securityContextHelper) {
        this.gepnicAdapter = gepnicAdapter;
        this.preTenderService = preTenderService;
        this.transactionService = transactionService;
        this.auditService = auditService;
        this.properties = properties;
        this.securityContextHelper = securityContextHelper;
    }

    @PostMapping("/tenders/push")
    public ResponseEntity<IntegrationResult> pushTender(
            @Valid @RequestBody TenderPushForm form,
            @RequestHeader(value = CommonConstants.HEADER_CORRELATION_ID, required = false) String correlationId,
            @RequestHeader(value = CommonConstants.HEADER_IDEMPOTENCY_KEY, required = false) String idempotencyKey) {

        String corrId = form.getCorrelationId() != null ? form.getCorrelationId() : correlationId;
        String idempKey = form.getIdempotencyKey() != null ? form.getIdempotencyKey() : idempotencyKey;

        IntegrationResult result = preTenderService.submitPreTender(form.getPreTenderId(), PortalCode.GEPNIC, corrId, idempKey);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PortalResponse> uploadDocument(
            @Valid @ModelAttribute DocumentUploadForm uploadForm,
            @RequestHeader(value = CommonConstants.HEADER_CORRELATION_ID, required = false) String correlationId) {

        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        PortalRequest request = new PortalRequest();
        request.setPortalCode(PortalCode.GEPNIC);
        request.setCorrelationId(corrId);

        PortalResponse response = gepnicAdapter.uploadDocument(request, uploadForm);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tenders/pull")
    public ResponseEntity<PortalResponse> pullTender(
            @Valid @RequestBody TenderPullForm form,
            @RequestHeader(value = CommonConstants.HEADER_CORRELATION_ID, required = false) String correlationId) {

        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        PortalRequest request = new PortalRequest();
        request.setPortalCode(PortalCode.GEPNIC);
        request.setCorrelationId(corrId);

        PortalResponse response = gepnicAdapter.pullTender(request, form);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/corrigendums/pull")
    public ResponseEntity<PortalResponse> pullCorrigendum(
            @Valid @RequestBody CorrigendumPullForm form,
            @RequestHeader(value = CommonConstants.HEADER_CORRELATION_ID, required = false) String correlationId) {

        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        PortalRequest request = new PortalRequest();
        request.setPortalCode(PortalCode.GEPNIC);
        request.setCorrelationId(corrId);

        PortalResponse response = gepnicAdapter.pullCorrigendum(request, form);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/aoc/pull")
    public ResponseEntity<PortalResponse> pullAoc(
            @Valid @RequestBody AocPullForm form,
            @RequestHeader(value = CommonConstants.HEADER_CORRELATION_ID, required = false) String correlationId) {

        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        PortalRequest request = new PortalRequest();
        request.setPortalCode(PortalCode.GEPNIC);
        request.setCorrelationId(corrId);

        PortalResponse response = gepnicAdapter.pullAoc(request, form);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/aoc/documents/download")
    public ResponseEntity<PortalResponse> downloadAocDocument(
            @Valid @RequestBody AocFileDownloadForm form,
            @RequestHeader(value = CommonConstants.HEADER_CORRELATION_ID, required = false) String correlationId) {

        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        PortalRequest request = new PortalRequest();
        request.setPortalCode(PortalCode.GEPNIC);
        request.setCorrelationId(corrId);

        PortalResponse response = gepnicAdapter.downloadAocDocument(request, form);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<IntegrationJobResponseForm>> searchTransactions(
            @RequestParam(required = false) Long preTenderId) {

        List<IntegrationTransaction> transactions;
        if (preTenderId != null) {
            transactions = transactionService.getTransactionsByPreTender(preTenderId);
        } else {
            transactions = transactionService.getTransactionsByPortal(PortalCode.GEPNIC.name());
        }

        List<IntegrationJobResponseForm> response = transactions.stream()
                .map(IntegrationJobResponseForm::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<IntegrationJobResponseForm> getTransaction(@PathVariable Long id) {
        IntegrationTransaction txn = transactionService.getTransaction(id);
        return ResponseEntity.ok(IntegrationJobResponseForm.fromEntity(txn));
    }

    @GetMapping("/transactions/{id}/audit")
    public ResponseEntity<List<AuditEventResponseForm>> getTransactionAudit(@PathVariable Long id) {
        List<IntegrationAuditEvent> events = auditService.getAuditTrail(id);
        List<AuditEventResponseForm> response = events.stream()
                .map(AuditEventResponseForm::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transactions/{id}/retry")
    public ResponseEntity<IntegrationResult> retryTransaction(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean forceReconcile) {

        IntegrationTransaction txn = transactionService.getTransaction(id);

        if (!transactionService.isEligibleForRetry(txn) && !forceReconcile) {
            throw new BusinessValidationException("Transaction " + id + " with status " + txn.getStatus() +
                    " is not eligible for automatic retry. If previous status was OUTCOME_UNKNOWN, reconcile before retrying.");
        }

        transactionService.incrementAttempt(id);
        String retryCorrelationId = txn.getCorrelationId() + "-R" + txn.getAttemptCount();

        IntegrationResult result = preTenderService.submitPreTender(
                txn.getPreTenderId(),
                PortalCode.GEPNIC,
                retryCorrelationId,
                txn.getIdempotencyKey()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/configuration")
    public ResponseEntity<Map<String, Object>> getConfiguration() {
        // Expose authorized, non-secret configuration only
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("portalCode", PortalCode.GEPNIC.name());
        config.put("enabled", properties.isEnabled());
        config.put("environment", properties.getEnvironment());
        config.put("baseUrl", properties.getBaseUrl());
        config.put("connectTimeoutSeconds", properties.getConnectTimeout().toSeconds());
        config.put("readTimeoutSeconds", properties.getReadTimeout().toSeconds());
        config.put("maxUploadSize", properties.getMaxUploadSize());
        config.put("uniqueUserId", properties.getUniqueUserId());
        config.put("keyId", properties.getKeyId());
        return ResponseEntity.ok(config);
    }

    @PutMapping("/configuration")
    public ResponseEntity<Map<String, Object>> updateConfiguration(@RequestBody Map<String, Object> updates) {
        if (updates.containsKey("uniqueUserId") && updates.get("uniqueUserId") != null) {
            properties.setUniqueUserId(String.valueOf(updates.get("uniqueUserId")));
        }
        if (updates.containsKey("baseUrl") && updates.get("baseUrl") != null) {
            properties.setBaseUrl(String.valueOf(updates.get("baseUrl")));
        }
        if (updates.containsKey("environment") && updates.get("environment") != null) {
            properties.setEnvironment(String.valueOf(updates.get("environment")));
        }
        if (updates.containsKey("enabled") && updates.get("enabled") != null) {
            properties.setEnabled(Boolean.parseBoolean(String.valueOf(updates.get("enabled"))));
        }
        return getConfiguration();
    }
}

