package com.cdac.egov.integration.gepnic.service.encryption;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.exception.GePNICCryptoException;
import com.cdac.egov.integration.gepnic.security.CertificateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * Handles digital signing of outgoing payloads and verification of incoming GePNIC signatures.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalSignatureService {

    private final CertificateProvider certificateProvider;

    /**
     * Signs input payload bytes using SHA256withRSA.
     */
    public String sign(byte[] data) {
        try {
            PrivateKey privateKey = certificateProvider.getClientPrivateKey();
            if (privateKey == null) {
                // Development fallback synthetic signature
                return Base64.getEncoder().encodeToString(("DEV_MOCK_SIGNATURE_" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
            }
            Signature signature = Signature.getInstance(GePNICConstants.SIGNATURE_SHA256_RSA);
            signature.initSign(privateKey);
            signature.update(data);
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new GePNICCryptoException("Failed to generate digital signature: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies the digital signature on received response bytes.
     */
    public boolean verifySignature(byte[] data, String base64Signature) {
        try {
            if (base64Signature == null || base64Signature.isBlank()) {
                return false;
            }
            PublicKey publicKey = certificateProvider.getGePNICPublicKey();
            if (publicKey == null) {
                // Development fallback
                return true;
            }
            Signature signature = Signature.getInstance(GePNICConstants.SIGNATURE_SHA256_RSA);
            signature.initVerify(publicKey);
            signature.update(data);
            byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            log.warn("Signature verification failed: {}", e.getMessage());
            return false;
        }
    }
}
