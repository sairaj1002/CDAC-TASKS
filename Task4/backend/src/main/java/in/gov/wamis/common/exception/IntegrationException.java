package in.gov.wamis.common.exception;

import in.gov.wamis.common.enums.IntegrationStatus;

public class IntegrationException extends RuntimeException {

    private final IntegrationStatus status;
    private final String errorCode;

    public IntegrationException(String message) {
        super(message);
        this.status = IntegrationStatus.FAILED;
        this.errorCode = "INTEGRATION_ERROR";
    }

    public IntegrationException(String message, IntegrationStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public IntegrationException(String message, Throwable cause, IntegrationStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public IntegrationStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

