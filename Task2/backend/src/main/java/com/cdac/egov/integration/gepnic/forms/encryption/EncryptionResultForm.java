package com.cdac.egov.integration.gepnic.forms.encryption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result data carrier of a payload encryption process.
 * Contains the constructed envelope, but NEVER exposes raw session keys.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionResultForm {

    private boolean success;
    private EncryptedEnvelopeForm envelope;
    private String keyId;
    private String algorithm;
    private long executionTimeMs;
    private String message;
}
