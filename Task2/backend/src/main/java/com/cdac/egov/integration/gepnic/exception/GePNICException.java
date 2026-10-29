package com.cdac.egov.integration.gepnic.exception;

/**
 * Base unchecked exception for GePNIC integration failures.
 */
public class GePNICException extends RuntimeException {

    public GePNICException(String message) {
        super(message);
    }

    public GePNICException(String message, Throwable cause) {
        super(message, cause);
    }
}
