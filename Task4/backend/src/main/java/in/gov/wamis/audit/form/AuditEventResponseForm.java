package in.gov.wamis.audit.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.gov.wamis.audit.entity.IntegrationAuditEvent;
import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;

import java.time.LocalDateTime;

/**
 * Standard API response model for sanitized audit trail events.
 */
public class AuditEventResponseForm {

    private Long id;
    private Long transactionId;
    private IntegrationOperation operation;
    private String portalCode;
    private String actor;
    private String correlationId;
    private IntegrationStatus fromStatus;
    private IntegrationStatus toStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTimestamp;

    private String sanitizedErrorCode;
    private String sanitizedMessage;
    private String externalReference;

    public AuditEventResponseForm() {
    }

    public static AuditEventResponseForm fromEntity(IntegrationAuditEvent entity) {
        if (entity == null) {
            return null;
        }
        AuditEventResponseForm form = new AuditEventResponseForm();
        form.setId(entity.getId());
        form.setTransactionId(entity.getTransactionId());
        form.setOperation(entity.getOperation());
        form.setPortalCode(entity.getPortalCode());
        form.setActor(entity.getActor());
        form.setCorrelationId(entity.getCorrelationId());
        form.setFromStatus(entity.getFromStatus());
        form.setToStatus(entity.getToStatus());
        form.setEventTimestamp(entity.getEventTimestamp());
        form.setSanitizedErrorCode(entity.getSanitizedErrorCode());
        form.setSanitizedMessage(entity.getSanitizedMessage());
        form.setExternalReference(entity.getExternalReference());
        return form;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public IntegrationStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(IntegrationStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public IntegrationStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(IntegrationStatus toStatus) {
        this.toStatus = toStatus;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getSanitizedErrorCode() {
        return sanitizedErrorCode;
    }

    public void setSanitizedErrorCode(String sanitizedErrorCode) {
        this.sanitizedErrorCode = sanitizedErrorCode;
    }

    public String getSanitizedMessage() {
        return sanitizedMessage;
    }

    public void setSanitizedMessage(String sanitizedMessage) {
        this.sanitizedMessage = sanitizedMessage;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }
}

