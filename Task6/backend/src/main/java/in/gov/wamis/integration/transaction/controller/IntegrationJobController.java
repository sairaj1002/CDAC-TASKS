package in.gov.wamis.integration.transaction.controller;

import in.gov.wamis.audit.entity.IntegrationAuditEvent;
import in.gov.wamis.audit.form.AuditEventResponseForm;
import in.gov.wamis.audit.service.IntegrationAuditService;
import in.gov.wamis.common.constants.ApiConstants;
import in.gov.wamis.integration.transaction.entity.IntegrationTransaction;
import in.gov.wamis.integration.transaction.form.IntegrationJobResponseForm;
import in.gov.wamis.integration.transaction.service.IntegrationTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for tracking integration jobs and their immutable audit trails.
 */
@RestController
@RequestMapping(ApiConstants.INTEGRATION_JOBS)
public class IntegrationJobController {

    private final IntegrationTransactionService transactionService;
    private final IntegrationAuditService auditService;

    public IntegrationJobController(IntegrationTransactionService transactionService,
                                    IntegrationAuditService auditService) {
        this.transactionService = transactionService;
        this.auditService = auditService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntegrationJobResponseForm> getJobStatus(@PathVariable Long id) {
        IntegrationTransaction txn = transactionService.getTransaction(id);
        return ResponseEntity.ok(IntegrationJobResponseForm.fromEntity(txn));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<AuditEventResponseForm>> getJobHistory(@PathVariable Long id) {
        List<IntegrationAuditEvent> events = auditService.getAuditTrail(id);
        List<AuditEventResponseForm> response = events.stream()
                .map(AuditEventResponseForm::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }
}

