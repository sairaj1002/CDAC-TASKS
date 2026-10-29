package com.cdac.egov.integration.gepnic.service.encryption;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.exception.GePNICCryptoException;
import com.cdac.egov.integration.gepnic.forms.encryption.DecryptionResultForm;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptedEnvelopeForm;
import com.cdac.egov.integration.gepnic.security.CertificateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;

/**
 * Handles decryption of GePNIC response envelopes and verification of response signatures.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GePNICDecryptionService {

    private final DigitalSignatureService digitalSignatureService;
    private final CertificateProvider certificateProvider;

    /**
     * Decrypts an incoming response envelope and returns the parsed JSON payload.
     */
    public DecryptionResultForm decryptEnvelope(EncryptedEnvelopeForm envelope) {
        long startTime = System.currentTimeMillis();
        try {
            // 1. Unwrap Session Key using WAMIS Private Key
            byte[] sessionKeyBytes;
            PrivateKey clientPrivateKey = certificateProvider.getClientPrivateKey();
            byte[] encryptedKeyBytes = Base64.getDecoder().decode(envelope.getEncryptKey());

            if (clientPrivateKey != null) {
                Cipher rsaCipher = Cipher.getInstance(GePNICConstants.CIPHER_RSA);
                rsaCipher.init(Cipher.DECRYPT_MODE, clientPrivateKey);
                sessionKeyBytes = rsaCipher.doFinal(encryptedKeyBytes);
            } else {
                // Development fallback
                sessionKeyBytes = encryptedKeyBytes;
            }
            SecretKey sessionKey = new SecretKeySpec(sessionKeyBytes, GePNICConstants.ALGORITHM_AES);

            // 2. Decrypt Payload using AES-GCM
            byte[] combinedCipherWithIv = Base64.getDecoder().decode(envelope.getEncryptData());
            byte[] iv = new byte[GePNICConstants.GCM_IV_LENGTH_BYTES];
            byte[] cipherBytes = new byte[combinedCipherWithIv.length - GePNICConstants.GCM_IV_LENGTH_BYTES];
            System.arraycopy(combinedCipherWithIv, 0, iv, 0, iv.length);
            System.arraycopy(combinedCipherWithIv, iv.length, cipherBytes, 0, cipherBytes.length);

            Cipher aesCipher = Cipher.getInstance(GePNICConstants.CIPHER_AES_GCM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GePNICConstants.GCM_TAG_LENGTH_BITS, iv);
            aesCipher.init(Cipher.DECRYPT_MODE, sessionKey, gcmSpec);
            byte[] plainBytes = aesCipher.doFinal(cipherBytes);
            String decryptedJson = new String(plainBytes, StandardCharsets.UTF_8);

            // 3. Verify Signature
            boolean signatureValid = digitalSignatureService.verifySignature(plainBytes, envelope.getSignData());
            if (!signatureValid) {
                log.warn("Digital signature on incoming response was INVALID or verification failed");
            }

            long duration = System.currentTimeMillis() - startTime;
            return DecryptionResultForm.builder()
                    .signatureVerified(signatureValid)
                    .keyDecrypted(true)
                    .payloadDecrypted(true)
                    .decryptedPayloadJson(decryptedJson)
                    .executionTimeMs(duration)
                    .message("Envelope decrypted successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to decrypt incoming envelope: {}", e.getMessage());
            throw new GePNICCryptoException("Decryption failed: " + e.getMessage(), e);
        }
    }
}
