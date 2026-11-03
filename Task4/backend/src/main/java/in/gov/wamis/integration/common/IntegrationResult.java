package in.gov.wamis.integration.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.common.enums.PortalCode;

import java.time.LocalDateTime;

/**
 * Standard integration result exposed by WAMIS application services to API clients.
 * Clearly distinguishes WAMIS internal job acceptance from the remote portal's business acceptance.
 */
public class IntegrationResult {

    private Long transactionId;
    private String correlationId;
    private PortalCode portalCode;
    private IntegrationOperation operation;
    private IntegrationStatus status;
    private boolean jobAccepted;
    private boolean remoteAccepted;
    private String externalReference;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public IntegrationResult() {
        this.timestamp = LocalDateTime.now();
    }

    public static IntegrationResult from(Long transactionId, String correlationId, PortalCode portalCode,
                                         IntegrationOperation operation, IntegrationStatus status,
                                         String externalReference, String message) {
        IntegrationResult result = new IntegrationResult();
        result.setTransactionId(transactionId);
        result.setCorrelationId(correlationId);
        result.setPortalCode(portalCode);
        result.setOperation(operation);
        result.setStatus(status);
        result.setJobAccepted(status != IntegrationStatus.VALIDATION_FAILED && status != IntegrationStatus.FAILED);
        result.setRemoteAccepted(status == IntegrationStatus.ACCEPTED);
        result.setExternalReference(externalReference);
        result.setMessage(message);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public PortalCode getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(PortalCode portalCode) {
        this.portalCode = portalCode;
    }

    public IntegrationOperation getOperation() {
        return operation;
    }

    public void setOperation(IntegrationOperation operation) {
        this.operation = operation;
    }

    public IntegrationStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }

    public boolean isJobAccepted() {
        return jobAccepted;
    }

    public void setJobAccepted(boolean jobAccepted) {
        this.jobAccepted = jobAccepted;
    }

    public boolean isRemoteAccepted() {
        return remoteAccepted;
    }

    public void setRemoteAccepted(boolean remoteAccepted) {
        this.remoteAccepted = remoteAccepted;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

