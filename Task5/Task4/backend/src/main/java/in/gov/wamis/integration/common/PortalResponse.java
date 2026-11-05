package in.gov.wamis.integration.common;

import in.gov.wamis.common.enums.IntegrationStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalized adapter-level outcome returned by e-procurement portal adapters.
 */
public class PortalResponse {

    private IntegrationStatus status;
    private String externalReference;
    private Integer responseCode;
    private String message;
    private String correlationId;
    private Map<String, Object> details = new HashMap<>();

    public PortalResponse() {
    }

    public static PortalResponse accepted(String externalReference, Integer responseCode, String message, String correlationId) {
        PortalResponse response = new PortalResponse();
        response.setStatus(IntegrationStatus.ACCEPTED);
        response.setExternalReference(externalReference);
        response.setResponseCode(responseCode);
        response.setMessage(message);
        response.setCorrelationId(correlationId);
        return response;
    }

    public static PortalResponse rejected(Integer responseCode, String message, String correlationId) {
        PortalResponse response = new PortalResponse();
        response.setStatus(IntegrationStatus.REJECTED);
        response.setResponseCode(responseCode);
        response.setMessage(message);
        response.setCorrelationId(correlationId);
        return response;
    }

    public static PortalResponse outcomeUnknown(String message, String correlationId) {
        PortalResponse response = new PortalResponse();
        response.setStatus(IntegrationStatus.OUTCOME_UNKNOWN);
        response.setMessage(message);
        response.setCorrelationId(correlationId);
        return response;
    }

    public static PortalResponse failed(String message, String correlationId) {
        PortalResponse response = new PortalResponse();
        response.setStatus(IntegrationStatus.FAILED);
        response.setMessage(message);
        response.setCorrelationId(correlationId);
        return response;
    }

    public IntegrationStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}

