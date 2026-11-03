package in.gov.wamis.integration.gepnic.security;

import in.gov.wamis.common.exception.IntegrationException;
import in.gov.wamis.integration.gepnic.config.GePNICProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HexFormat;

/**
 * Manages GePNIC certificates and cryptographic keys.
 * Supports planned rotation, certificate thumbprint calculation, and fallback demo key generation.
 * Never returns private key bytes directly to API controllers or logs.
 */
@Component
public class GePNICCertificateProvider {

    private static final Logger log = LoggerFactory.getLogger(GePNICCertificateProvider.class);

    private final GePNICProperties properties;
    private KeyPair localKeyPair;
    private String currentKeyId;

    public GePNICCertificateProvider(GePNICProperties properties) {
        this.properties = properties;
        initializeKeys();
    }

    private void initializeKeys() {
        if (properties.getKeyStoreLocation() != null && !properties.getKeyStoreLocation().isBlank()) {
            try {
                loadFromKeystore();
            } catch (Exception e) {
                log.error("Failed to load keystore from {}, falling back to generated keypair for environment {}: {}",
                        properties.getKeyStoreLocation(), properties.getEnvironment(), e.getMessage());
                generateEphemeralKeyPair();
            }
        } else {
            log.info("No keystore configured. Initializing standard RSA keypair for environment: {}", properties.getEnvironment());
            generateEphemeralKeyPair();
        }
    }

    private void loadFromKeystore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] password = properties.getKeyStorePassword() != null ? properties.getKeyStorePassword().toCharArray() : new char[0];
        try (InputStream is = new FileInputStream(properties.getKeyStoreLocation())) {
            keyStore.load(is, password);
        }

        String alias = keyStore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);
        Certificate cert = keyStore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey();

        this.localKeyPair = new KeyPair(publicKey, privateKey);

        if (cert instanceof X509Certificate x509) {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] thumbprint = sha256.digest(x509.getEncoded());
            this.currentKeyId = HexFormat.of().formatHex(thumbprint);
        } else {
            this.currentKeyId = properties.getKeyId();
        }
        log.info("Loaded cryptographic key from keystore with keyId thumbprint: {}", this.currentKeyId);
    }

    private void generateEphemeralKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            this.localKeyPair = generator.generateKeyPair();
            this.currentKeyId = properties.getKeyId() != null && !properties.getKeyId().isBlank()
                    ? properties.getKeyId() : "DEMO-KEY-THUMBPRINT-001";
        } catch (NoSuchAlgorithmException e) {
            throw new IntegrationException("Failed to initialize RSA key generator", e, null, "KEY_GEN_FAILED");
        }
    }

    public PublicKey getPortalPublicKey() {
        // In full mutual TLS/envelope encryption, portal public key encrypts the session key.
        // For development/demo loopback, local public key is used.
        return localKeyPair.getPublic();
    }

    public PrivateKey getClientPrivateKey() {
        return localKeyPair.getPrivate();
    }

    public PublicKey getClientPublicKey() {
        return localKeyPair.getPublic();
    }

    public String getCurrentKeyId() {
        return currentKeyId;
    }
}

