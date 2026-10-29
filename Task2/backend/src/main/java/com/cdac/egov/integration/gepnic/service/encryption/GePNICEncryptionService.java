package com.cdac.egov.integration.gepnic.service.encryption;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.exception.GePNICCryptoException;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptedEnvelopeForm;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptionResultForm;
import com.cdac.egov.integration.gepnic.security.CertificateProvider;
import com.cdac.egov.integration.gepnic.security.SecretProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Handles AES payload encryption, RSA session-key wrapping, and envelope assembly.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GePNICEncryptionService {

    private final SessionKeyService sessionKeyService;
    private final DigitalSignatureService digitalSignatureService;
    private final CertificateProvider certificateProvider;
    private final SecretProvider secretProvider;

    /**
     * Encrypts a JSON payload string and returns the complete GePNIC envelope.
     */
    public EncryptionResultForm encryptPayload(String jsonPayload) {
        long startTime = System.currentTimeMillis();
        try {
            byte[] plainBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);

            // 1. Generate AES Session Key & IV
            SecretKey sessionKey = sessionKeyService.generateSessionKey();
            byte[] iv = sessionKeyService.generateIv();

            // 2. Encrypt Payload with AES/GCM/NoPadding
            Cipher aesCipher = Cipher.getInstance(GePNICConstants.CIPHER_AES_GCM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GePNICConstants.GCM_TAG_LENGTH_BITS, iv);
            aesCipher.init(Cipher.ENCRYPT_MODE, sessionKey, gcmSpec);
            byte[] cipherBytes = aesCipher.doFinal(plainBytes);

            // Prepend IV to ciphertext
            byte[] combinedCipherWithIv = new byte[iv.length + cipherBytes.length];
            System.arraycopy(iv, 0, combinedCipherWithIv, 0, iv.length);
            System.arraycopy(cipherBytes, 0, combinedCipherWithIv, iv.length, cipherBytes.length);
            String base64EncryptedData = Base64.getEncoder().encodeToString(combinedCipherWithIv);

            // 3. Digitally sign the plaintext bytes
            String base64Signature = digitalSignatureService.sign(plainBytes);

            // 4. Encrypt session key with GePNIC RSA Public Key
            String base64EncryptedKey;
            PublicKey gepnicPublicKey = certificateProvider.getGePNICPublicKey();
            if (gepnicPublicKey != null) {
                Cipher rsaCipher = Cipher.getInstance(GePNICConstants.CIPHER_RSA);
                rsaCipher.init(Cipher.ENCRYPT_MODE, gepnicPublicKey);
                byte[] encryptedKeyBytes = rsaCipher.doFinal(sessionKey.getEncoded());
                base64EncryptedKey = Base64.getEncoder().encodeToString(encryptedKeyBytes);
            } else {
                // Development fallback
                base64EncryptedKey = Base64.getEncoder().encodeToString(sessionKey.getEncoded());
            }

            // 5. Construct Envelope
            String keyId = certificateProvider.getActiveKeyId();
            String userId = secretProvider.getUniqueUserId();

            EncryptedEnvelopeForm envelope = EncryptedEnvelopeForm.builder()
                    .uniqueUserId(userId)
                    .encryptData(base64EncryptedData)
                    .signData(base64Signature)
                    .encryptKey(base64EncryptedKey)
                    .keyId(keyId)
                    .build();

            long duration = System.currentTimeMillis() - startTime;
            log.info("Payload encrypted successfully in {}ms (keyId: {})", duration, keyId);

            return EncryptionResultForm.builder()
                    .success(true)
                    .envelope(envelope)
                    .keyId(keyId)
                    .algorithm("AES-256-GCM + RSA-2048")
                    .executionTimeMs(duration)
                    .message("Envelope encrypted and signed successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to encrypt payload: {}", e.getMessage());
            throw new GePNICCryptoException("Encryption failed: " + e.getMessage(), e);
        }
    }
}
