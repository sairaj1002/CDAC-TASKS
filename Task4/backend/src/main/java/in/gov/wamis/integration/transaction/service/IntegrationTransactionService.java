package in.gov.wamis.integration.transaction.service;

import in.gov.wamis.audit.service.IntegrationAuditService;
import in.gov.wamis.common.constants.IntegrationConstants;
import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.common.exception.BusinessValidationException;
import in.gov.wamis.common.exception.ResourceNotFoundException;
import in.gov.wamis.common.security.Sanitizer;
import in.gov.wamis.integration.transaction.entity.IntegrationTransaction;
import in.gov.wamis.integration.transaction.repository.IntegrationTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IntegrationTransactionService {

    private final IntegrationTransactionRepository transactionRepository;
    private final IntegrationAuditService auditService;

    public IntegrationTransactionService(IntegrationTransactionRepository transactionRepository,
                                       IntegrationAuditService auditService) {
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public IntegrationTransaction createTransaction(IntegrationOperation operation, String portalCode,
                                                   Long preTenderId, Long workId, String correlationId,
                                                   String idempotencyKey, String actor) {
        // Idempotency verification
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IntegrationTransaction> existingOpt = transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (existingOpt.isPresent()) {
                IntegrationTransaction existing = existingOpt.get();
                if (existing.getStatus() == IntegrationStatus.ACCEPTED) {
                    return existing; // idempotent success response
                }
                if (existing.getStatus() == IntegrationStatus.IN_PROGRESS || existing.getStatus() == IntegrationStatus.QUEUED) {
                    throw new BusinessValidationException("A transaction is already in progress for idempotency key: " + idempotencyKey);
                }
                if (existing.getStatus() == IntegrationStatus.OUTCOME_UNKNOWN) {
                    throw new BusinessValidationException("Previous transaction has uncertain outcome. Reconcile before retrying.");
                }
            }
        }

        IntegrationTransaction txn = new IntegrationTransaction(
                operation,
                portalCode,
                preTenderId,
                workId,
                correlationId,
                idempotencyKey,
                IntegrationStatus.READY
        );
        txn = transactionRepository.save(txn);

        auditService.recordEvent(
                txn.getId(),
                operation,
                portalCode,
                actor,
                correlationId,
                null,
                IntegrationStatus.READY,
                null,
                "Transaction created and ready for execution",
                null
        );

        return txn;
    }

    @Transactional
    public IntegrationTransaction transitionStatus(Long transactionId, IntegrationStatus newStatus,
                                                  String errorCode, String rawErrorMessage,
                                                  String externalReference, String actor) {
        IntegrationTransaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("IntegrationTransaction", transactionId));

        IntegrationStatus previousStatus = txn.getStatus();
        txn.setStatus(newStatus);
        txn.setUpdatedAt(LocalDateTime.now());

        if (externalReference != null && !externalReference.isBlank()) {
            txn.setExternalReference(externalReference);
        }
        if (errorCode != null && !errorCode.isBlank()) {
            txn.setErrorCode(errorCode);
        }
        if (rawErrorMessage != null && !rawErrorMessage.isBlank()) {
            txn.setSanitizedErrorMessage(Sanitizer.sanitize(rawErrorMessage));
        }
        if (newStatus == IntegrationStatus.ACCEPTED || newStatus == IntegrationStatus.REJECTED || newStatus == IntegrationStatus.FAILED) {
            txn.setCompletedAt(LocalDateTime.now());
        }

        txn = transactionRepository.save(txn);

        auditService.recordEvent(
                txn.getId(),
                txn.getOperation(),
                txn.getPortalCode(),
                actor,
                txn.getCorrelationId(),
                previousStatus,
                newStatus,
                errorCode,
                rawErrorMessage,
                externalReference
        );

        return txn;
    }

    @Transactional
    public IntegrationTransaction incrementAttempt(Long transactionId) {
        IntegrationTransaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("IntegrationTransaction", transactionId));

        txn.setAttemptCount(txn.getAttemptCount() + 1);
        txn.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(txn);
    }

    @Transactional(readOnly = true)
    public IntegrationTransaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IntegrationTransaction", id));
    }

    @Transactional(readOnly = true)
    public Optional<IntegrationTransaction> findByCorrelationId(String correlationId) {
        return transactionRepository.findByCorrelationId(correlationId);
    }

    @Transactional(readOnly = true)
    public List<IntegrationTransaction> getTransactionsByPreTender(Long preTenderId) {
        return transactionRepository.findByPreTenderIdOrderByCreatedAtDesc(preTenderId);
    }

    @Transactional(readOnly = true)
    public List<IntegrationTransaction> getTransactionsByPortal(String portalCode) {
        return transactionRepository.findByPortalCodeOrderByCreatedAtDesc(portalCode);
    }

    public boolean isEligibleForRetry(IntegrationTransaction txn) {
        if (txn == null) return false;
        if (txn.getStatus() == IntegrationStatus.ACCEPTED) return false;
        if (txn.getStatus() == IntegrationStatus.IN_PROGRESS) return false;
        if (txn.getStatus() == IntegrationStatus.OUTCOME_UNKNOWN) {
            // Section 9: Cannot blindly retry OUTCOME_UNKNOWN without reconciliation!
            return false;
        }
        return txn.getAttemptCount() < IntegrationConstants.MAX_RETRY_ATTEMPTS;
    }
}

