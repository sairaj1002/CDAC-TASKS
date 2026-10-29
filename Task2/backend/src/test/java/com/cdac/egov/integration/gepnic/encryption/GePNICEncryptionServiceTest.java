package com.cdac.egov.integration.gepnic.encryption;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import com.cdac.egov.integration.gepnic.forms.encryption.DecryptionResultForm;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptionResultForm;
import com.cdac.egov.integration.gepnic.security.CertificateProvider;
import com.cdac.egov.integration.gepnic.security.SecretProvider;
import com.cdac.egov.integration.gepnic.service.encryption.DigitalSignatureService;
import com.cdac.egov.integration.gepnic.service.encryption.GePNICDecryptionService;
import com.cdac.egov.integration.gepnic.service.encryption.GePNICEncryptionService;
import com.cdac.egov.integration.gepnic.service.encryption.SessionKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.junit.jupiter.api.Assertions.*;

class GePNICEncryptionServiceTest {

    private GePNICEncryptionService encryptionService;
    private GePNICDecryptionService decryptionService;

    @BeforeEach
    void setUp() {
        GePNICProperties properties = new GePNICProperties();
        SecretProvider secretProvider = new SecretProvider(properties);
        CertificateProvider certProvider = new CertificateProvider(properties, secretProvider, new DefaultResourceLoader());
        SessionKeyService sessionKeyService = new SessionKeyService();
        DigitalSignatureService signatureService = new DigitalSignatureService(certProvider);

        encryptionService = new GePNICEncryptionService(sessionKeyService, signatureService, certProvider, secretProvider);
        decryptionService = new GePNICDecryptionService(signatureService, certProvider);
    }

    @Test
    void testEncryptAndDecryptPayload_roundTrip() {
        String testJson = "{\"sampleKey\":\"sampleValue\",\"number\":12345}";

        EncryptionResultForm encResult = encryptionService.encryptPayload(testJson);
        assertTrue(encResult.isSuccess());
        assertNotNull(encResult.getEnvelope());
        assertNotNull(encResult.getEnvelope().getEncryptData());
        assertNotNull(encResult.getEnvelope().getSignData());

        DecryptionResultForm decResult = decryptionService.decryptEnvelope(encResult.getEnvelope());
        assertTrue(decResult.isPayloadDecrypted());
        assertEquals(testJson, decResult.getDecryptedPayloadJson());
    }
}
