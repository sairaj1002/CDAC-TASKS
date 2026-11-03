package in.gov.wamis.integration.gepnic.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Internal WAMIS API request for initiating a GePNIC tender push.
 * Authoritative fields are loaded from the database by ID.
 */
public class TenderPushForm {

    @NotNull(message = "Pre-Tender ID is required")
    @Positive(message = "Pre-Tender ID must be positive")
    private Long preTenderId;

    private String correlationId;
    private String idempotencyKey;

    public TenderPushForm() {
    }

    public TenderPushForm(Long preTenderId) {
        this.preTenderId = preTenderId;
    }

    public Long getPreTenderId() {
        return preTenderId;
    }

    public void setPreTenderId(Long preTenderId) {
        this.preTenderId = preTenderId;
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
}

