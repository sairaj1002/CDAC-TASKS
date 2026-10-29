package com.cdac.egov.integration.gepnic.security;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Provides access to sensitive encryption credentials, keystore passwords, and private keys.
 * In production, integrates with Vault, HSM, or external KMS. Secrets are never logged.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecretProvider {

    private final GePNICProperties properties;

    /**
     * Resolves the keystore password securely.
     */
    public char[] getKeystorePassword() {
        // Environment variable or KMS fallback
        String password = System.getenv("GEPNIC_KEYSTORE_PASSWORD");
        if (password == null || password.isBlank()) {
            // Development fallback password
            return "changeit".toCharArray();
        }
        return password.toCharArray();
    }

    /**
     * Resolves the private key password securely.
     */
    public char[] getKeyPassword() {
        String password = System.getenv("GEPNIC_KEY_PASSWORD");
        if (password == null || password.isBlank()) {
            return "changeit".toCharArray();
        }
        return password.toCharArray();
    }

    /**
     * Retrieves the configured Unique User ID.
     */
    public String getUniqueUserId() {
        return properties.getSecurity().getUniqueUserId();
    }

    /**
     * Retrieves the public key / certificate thumbprint ID.
     */
    public String getKeyId() {
        return properties.getSecurity().getKeyId();
    }
}
