package in.gov.wamis.audit.entity;

import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Append-only immutable audit log for integration transitions and events.
 * Preserves audit trail without storing passwords, private keys, or raw confidential payloads.
 */
@Entity
@Table(name = "wamis_integration_audit_events", indexes = {
        @Index(name = "idx_audit_transaction_id", columnList = "transaction_id"),
        @Index(name = "idx_audit_correlation_id", columnList = "correlation_id"),
        @Index(name = "idx_audit_timestamp", columnList = "event_timestamp")
})
public class IntegrationAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", length = 50, nullable = false)
    private IntegrationOperation operation;

    @Column(name = "portal_code", length = 30, nullable = false)
    private String portalCode;

    @Column(name = "actor", length = 100, nullable = false)
    private String actor;

    @Column(name = "correlation_id", length = 100, nullable = false)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private IntegrationStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", length = 30, nullable = false)
    private IntegrationStatus toStatus;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp = LocalDateTime.now();

    @Column(name = "sanitized_error_code", length = 100)
    private String sanitizedErrorCode;

    @Column(name = "sanitized_message", length = 1000)
    private String sanitizedMessage;

    @Column(name = "external_reference", length = 100)
    private String externalReference;

    public IntegrationAuditEvent() {
    }

    public IntegrationAuditEvent(Long transactionId, IntegrationOperation operation, String portalCode,
                                String actor, String correlationId, IntegrationStatus fromStatus,
                                IntegrationStatus toStatus, String sanitizedErrorCode,
                                String sanitizedMessage, String externalReference) {
        this.transactionId = transactionId;
        this.operation = operation;
        this.portalCode = portalCode;
        this.actor = actor;
        this.correlationId = correlationId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.sanitizedErrorCode = sanitizedErrorCode;
        this.sanitizedMessage = sanitizedMessage;
        this.externalReference = externalReference;
        this.eventTimestamp = LocalDateTime.now();
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

