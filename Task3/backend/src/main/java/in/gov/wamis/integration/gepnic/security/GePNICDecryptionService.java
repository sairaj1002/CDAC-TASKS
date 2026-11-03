package in.gov.wamis.integration.gepnic.security;

import in.gov.wamis.common.exception.IntegrationException;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;

/**
 * Decrypts encrypted responses and payloads received from GePNIC endpoints.
 */
@Service
public class GePNICDecryptionService {

    private static final String AES_ALGO = "AES/GCM/NoPadding";
    private static final String RSA_ALGO = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    private final GePNICCertificateProvider certificateProvider;

    public GePNICDecryptionService(GePNICCertificateProvider certificateProvider) {
        this.certificateProvider = certificateProvider;
    }

    public String decryptEnvelope(String base64EncryptData, String base64EncryptKey) {
        try {
            // 1. Decrypt AES session key with Client RSA private key
            PrivateKey privateKey = certificateProvider.getClientPrivateKey();
            Cipher rsaCipher = Cipher.getInstance(RSA_ALGO);
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] sessionKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(base64EncryptKey));
            SecretKey sessionKey = new SecretKeySpec(sessionKeyBytes, "AES");

            // 2. Extract IV and ciphertext
            byte[] combined = Base64.getDecoder().decode(base64EncryptData);
            if (combined.length <= IV_LENGTH) {
                throw new IllegalArgumentException("Encrypted payload data too short to contain IV");
            }
            byte[] iv = new byte[IV_LENGTH];
            byte[] cipherText = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, cipherText, 0, cipherText.length);

            // 3. Decrypt ciphertext with AES-GCM
            Cipher aesCipher = Cipher.getInstance(AES_ALGO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            aesCipher.init(Cipher.DECRYPT_MODE, sessionKey, parameterSpec);
            byte[] plainBytes = aesCipher.doFinal(cipherText);

            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IntegrationException("Failed to decrypt GePNIC response payload: " + e.getMessage(), e, null, "DECRYPTION_FAILED");
        }
    }
}

