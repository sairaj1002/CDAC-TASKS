package in.gov.wamis.integration.gepnic.security;

import in.gov.wamis.common.exception.IntegrationException;
import in.gov.wamis.common.security.Sanitizer;
import in.gov.wamis.integration.gepnic.config.GePNICProperties;
import in.gov.wamis.integration.gepnic.form.GePNICEnvelopeForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServicesTest {

    private GePNICCertificateProvider certificateProvider;
    private GePNICSignatureVerifier signatureVerifier;
    private GePNICEncryptionService encryptionService;
    private GePNICDecryptionService decryptionService;

    @BeforeEach
    void setUp() {
        GePNICProperties props = new GePNICProperties();
        props.setEnvironment("test");
        props.setUniqueUserId("TEST_WAMIS_USER");
        props.setKeyId("TEST-KEY-001");

        this.certificateProvider = new GePNICCertificateProvider(props);
        this.signatureVerifier = new GePNICSignatureVerifier(certificateProvider);
        this.encryptionService = new GePNICEncryptionService(certificateProvider, signatureVerifier, props);
        this.decryptionService = new GePNICDecryptionService(certificateProvider);
    }

    @Test
    @DisplayName("Encrypts into envelope and successfully decrypts back to original payload")
    void testEncryptAndDecryptRoundTrip() {
        String originalJson = "{\"inputData\":{\"uniqueRefNo\":\"TND-12345\",\"tenderValue\":500000}}";

        GePNICEnvelopeForm envelope = encryptionService.buildEnvelope(originalJson);
        assertNotNull(envelope);
        assertEquals("TEST_WAMIS_USER", envelope.getUniqueUserId());
        assertEquals("TEST-KEY-001", envelope.getKeyId());
        assertNotNull(envelope.getEncryptData());
        assertNotNull(envelope.getEncryptKey());
        assertNotNull(envelope.getSignData());

        // Decrypt
        String decrypted = decryptionService.decryptEnvelope(envelope.getEncryptData(), envelope.getEncryptKey());
        assertEquals(originalJson, decrypted);
    }

    @Test
    @DisplayName("Digital signature verification verifies valid data and rejects tampered data")
    void testSignatureVerification() {
        String data = "Important tender submission payload";
        String signature = signatureVerifier.sign(data);
        assertNotNull(signature);

        assertTrue(signatureVerifier.verify(data, signature));
        assertFalse(signatureVerifier.verify("Tampered tender payload", signature));
        assertFalse(signatureVerifier.verify(data, "invalidBase64Signature"));
    }

    @Test
    @DisplayName("Tampered ciphertext fails decryption with IntegrationException")
    void testTamperedCiphertext() {
        String originalJson = "{\"status\":\"ok\"}";
        GePNICEnvelopeForm envelope = encryptionService.buildEnvelope(originalJson);

        // Corrupt the ciphertext slightly
        char first = envelope.getEncryptData().charAt(0);
        String corruptedData = (first == 'A' ? "B" : "A") + envelope.getEncryptData().substring(1);

        assertThrows(IntegrationException.class, () ->
                decryptionService.decryptEnvelope(corruptedData, envelope.getEncryptKey())
        );
    }

    @Test
    @DisplayName("Sanitizer masks passwords and private keys from strings")
    void testSanitizer() {
        String logMessage = "Error connecting: password='SuperSecretPassword123' and encryptKey='XYZ123'";
        String sanitized = Sanitizer.sanitize(logMessage);

        assertFalse(sanitized.contains("SuperSecretPassword123"));
        assertTrue(sanitized.contains("[REDACTED]"));
    }
}

