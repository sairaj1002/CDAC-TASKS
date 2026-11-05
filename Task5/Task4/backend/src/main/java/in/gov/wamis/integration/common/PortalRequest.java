package in.gov.wamis.integration.common;

import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.PortalCode;

/**
 * Internal request context passed into portal adapters.
 * Authoritative fields are loaded from the database by the service layer.
 */
public class PortalRequest {

    private Long preTenderId;
    private Long workId;
    private Long officeId;
    private PortalCode portalCode;
    private IntegrationOperation operation;
    private String correlationId;
    private String idempotencyKey;
    private Object payload;

    public PortalRequest() {
    }

    public PortalRequest(Long preTenderId, Long workId, Long officeId, PortalCode portalCode,
                         IntegrationOperation operation, String correlationId, String idempotencyKey) {
        this.preTenderId = preTenderId;
        this.workId = workId;
        this.officeId = officeId;
        this.portalCode = portalCode;
        this.operation = operation;
        this.correlationId = correlationId;
        this.idempotencyKey = idempotencyKey;
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

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
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

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}

