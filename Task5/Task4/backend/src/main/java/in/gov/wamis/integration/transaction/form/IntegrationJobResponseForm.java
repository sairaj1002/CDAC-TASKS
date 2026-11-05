package in.gov.wamis.integration.transaction.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.integration.transaction.entity.IntegrationTransaction;

import java.time.LocalDateTime;

/**
 * Standard API response model for integration jobs/transactions.
 * Enforces the architectural rule of never returning JPA entities directly.
 */
public class IntegrationJobResponseForm {

    private Long id;
    private IntegrationOperation operation;
    private String portalCode;
    private Long preTenderId;
    private Long workId;
    private String correlationId;
    private String idempotencyKey;
    private IntegrationStatus status;
    private Integer attemptCount;
    private String externalReference;
    private String errorCode;
    private String sanitizedErrorMessage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    public IntegrationJobResponseForm() {
    }

    public static IntegrationJobResponseForm fromEntity(IntegrationTransaction entity) {
        if (entity == null) {
            return null;
        }
        IntegrationJobResponseForm form = new IntegrationJobResponseForm();
        form.setId(entity.getId());
        form.setOperation(entity.getOperation());
        form.setPortalCode(entity.getPortalCode());
        form.setPreTenderId(entity.getPreTenderId());
        form.setWorkId(entity.getWorkId());
        form.setCorrelationId(entity.getCorrelationId());
        form.setIdempotencyKey(entity.getIdempotencyKey());
        form.setStatus(entity.getStatus());
        form.setAttemptCount(entity.getAttemptCount());
        form.setExternalReference(entity.getExternalReference());
        form.setErrorCode(entity.getErrorCode());
        form.setSanitizedErrorMessage(entity.getSanitizedErrorMessage());
        form.setCreatedAt(entity.getCreatedAt());
        form.setUpdatedAt(entity.getUpdatedAt());
        form.setCompletedAt(entity.getCompletedAt());
        return form;
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

