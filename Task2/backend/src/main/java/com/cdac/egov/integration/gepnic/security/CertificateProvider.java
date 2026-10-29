package com.cdac.egov.integration.gepnic.security;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import com.cdac.egov.integration.gepnic.exception.GePNICCryptoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Loads, verifies, and provides X.509 certificates and keys for signing and envelope encryption.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateProvider {

    private final GePNICProperties properties;
    private final SecretProvider secretProvider;
    private final ResourceLoader resourceLoader;

    /**
     * Loads the client private key from keystore for digital signatures.
     */
    public PrivateKey getClientPrivateKey() {
        try {
            Resource resource = resourceLoader.getResource(properties.getSecurity().getKeystorePath());
            if (!resource.exists()) {
                log.warn("Keystore resource not found at {}. Using mock for development mode.", properties.getSecurity().getKeystorePath());
                return null;
            }
            KeyStore keyStore = KeyStore.getInstance(properties.getSecurity().getKeystoreType());
            try (InputStream is = resource.getInputStream()) {
                keyStore.load(is, secretProvider.getKeystorePassword());
            }
            return (PrivateKey) keyStore.getKey(properties.getSecurity().getKeyAlias(), secretProvider.getKeyPassword());
        } catch (Exception e) {
            throw new GePNICCryptoException("Failed to load client private key: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the target GePNIC public certificate for encrypting the session key.
     */
    public PublicKey getGePNICPublicKey() {
        try {
            Resource resource = resourceLoader.getResource(properties.getSecurity().getGepnicCertPath());
            if (!resource.exists()) {
                log.warn("GePNIC certificate not found at {}. Using mock for development mode.", properties.getSecurity().getGepnicCertPath());
                return null;
            }
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            try (InputStream is = resource.getInputStream()) {
                X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
                cert.checkValidity();
                return cert.getPublicKey();
            }
        } catch (Exception e) {
            throw new GePNICCryptoException("Failed to load GePNIC public certificate: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the active key thumbprint identifier.
     */
    public String getActiveKeyId() {
        return secretProvider.getKeyId();
    }
}
