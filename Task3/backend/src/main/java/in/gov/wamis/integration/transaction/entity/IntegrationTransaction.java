package in.gov.wamis.integration.transaction.entity;

import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Tracks the current state of an integration transaction/job.
 * Used for lifecycle tracking, duplicate prevention, idempotency, and retries.
 */
@Entity
@Table(name = "wamis_integration_transactions", indexes = {
        @Index(name = "idx_txn_correlation_id", columnList = "correlation_id"),
        @Index(name = "idx_txn_idempotency_key", columnList = "idempotency_key"),
        @Index(name = "idx_txn_pretender_id", columnList = "pretender_id"),
        @Index(name = "idx_txn_status", columnList = "status")
})
public class IntegrationTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", length = 50, nullable = false)
    private IntegrationOperation operation;

    @Column(name = "portal_code", length = 30, nullable = false)
    private String portalCode;

    @Column(name = "pretender_id")
    private Long preTenderId;

    @Column(name = "work_id")
    private Long workId;

    @Column(name = "correlation_id", length = 100, nullable = false)
    private String correlationId;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private IntegrationStatus status;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "external_reference", length = 100)
    private String externalReference;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "sanitized_error_message", length = 1000)
    private String sanitizedErrorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public IntegrationTransaction() {
    }

    public IntegrationTransaction(IntegrationOperation operation, String portalCode, Long preTenderId,
                                  Long workId, String correlationId, String idempotencyKey, IntegrationStatus status) {
        this.operation = operation;
        this.portalCode = portalCode;
        this.preTenderId = preTenderId;
        this.workId = workId;
        this.correlationId = correlationId;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.attemptCount = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntegrationOperation getOperation() {
        return operation;
    }

    public void setOperation(IntegrationOperation operation) {
        this.operation = operation;
    }

    public String getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(String portalCode) {
        this.portalCode = portalCode;
    }

    public Long getPreTenderId() {
        return preTenderId;
    }

    public void setPreTenderId(Long preTenderId) {
        this.preTenderId = preTenderId;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public IntegrationStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getSanitizedErrorMessage() {
        return sanitizedErrorMessage;
    }

    public void setSanitizedErrorMessage(String sanitizedErrorMessage) {
        this.sanitizedErrorMessage = sanitizedErrorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

