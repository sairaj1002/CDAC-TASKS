package com.cdac.egov.integration.gepnic.exception;

import lombok.Getter;

/**
 * Thrown when GePNIC external services return an error code or HTTP transport fails.
 */
@Getter
public class GePNICApiException extends GePNICException {

    private final Integer responseCode;
    private final String rawResponseBody;

    public GePNICApiException(String message) {
        super(message);
        this.responseCode = null;
        this.rawResponseBody = null;
    }

    public GePNICApiException(String message, Integer responseCode, String rawResponseBody) {
        super(message);
        this.responseCode = responseCode;
        this.rawResponseBody = rawResponseBody;
    }

    public GePNICApiException(String message, Throwable cause) {
        super(message, cause);
        this.responseCode = null;
        this.rawResponseBody = null;
    }
}
