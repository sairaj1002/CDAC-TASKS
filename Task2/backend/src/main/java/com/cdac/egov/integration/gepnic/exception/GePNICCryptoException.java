package com.cdac.egov.integration.gepnic.exception;

/**
 * Thrown when cryptographic operations (key generation, encryption, decryption, signing, certificate validation) fail.
 */
public class GePNICCryptoException extends GePNICException {

    public GePNICCryptoException(String message) {
        super(message);
    }

    public GePNICCryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
