package in.gov.wamis.audit.service;

import in.gov.wamis.audit.entity.IntegrationAuditEvent;
import in.gov.wamis.audit.repository.IntegrationAuditRepository;
import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.common.security.Sanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for recording immutable integration audit events.
 * Guarantees sanitization before writing to database and logs.
 */
@Service
public class IntegrationAuditService {

    private static final Logger log = LoggerFactory.getLogger(IntegrationAuditService.class);

    private final IntegrationAuditRepository auditRepository;

    public IntegrationAuditService(IntegrationAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IntegrationAuditEvent recordEvent(Long transactionId, IntegrationOperation operation, String portalCode,
                                             String actor, String correlationId, IntegrationStatus fromStatus,
                                             IntegrationStatus toStatus, String errorCode, String rawMessage,
                                             String externalReference) {
        String sanitizedMessage = Sanitizer.sanitize(rawMessage);
        String sanitizedError = Sanitizer.sanitize(errorCode);

        log.info("Audit transition [correlationId={}, txnId={}]: {} -> {} (ref={})",
                correlationId, transactionId, fromStatus, toStatus, externalReference);

        IntegrationAuditEvent event = new IntegrationAuditEvent(
                transactionId,
                operation,
                portalCode,
                actor != null ? actor : "SYSTEM",
                correlationId,
                fromStatus,
                toStatus,
                sanitizedError,
                sanitizedMessage,
                externalReference
        );
        return auditRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<IntegrationAuditEvent> getAuditTrail(Long transactionId) {
        return auditRepository.findByTransactionIdOrderByEventTimestampAsc(transactionId);
    }

    @Transactional(readOnly = true)
    public List<IntegrationAuditEvent> getAuditTrailByCorrelationId(String correlationId) {
        return auditRepository.findByCorrelationIdOrderByEventTimestampAsc(correlationId);
    }
}

