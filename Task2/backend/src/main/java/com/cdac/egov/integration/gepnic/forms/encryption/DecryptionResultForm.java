package com.cdac.egov.integration.gepnic.forms.encryption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result data carrier of an envelope decryption process.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecryptionResultForm {

    private boolean signatureVerified;
    private boolean keyDecrypted;
    private boolean payloadDecrypted;
    private String decryptedPayloadJson;
    private long executionTimeMs;
    private String message;
}
