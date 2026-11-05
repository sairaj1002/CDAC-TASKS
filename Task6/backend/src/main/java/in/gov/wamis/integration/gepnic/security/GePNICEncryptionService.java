package in.gov.wamis.integration.gepnic.security;

import in.gov.wamis.common.exception.IntegrationException;
import in.gov.wamis.integration.gepnic.config.GePNICProperties;
import in.gov.wamis.integration.gepnic.form.GePNICEnvelopeForm;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Payload encryption service implementing hybrid AES-GCM and RSA encryption
 * according to the NIC-GePNIC PreTender security protocol.
 */
@Service
public class GePNICEncryptionService {

    private static final String AES_ALGO = "AES/GCM/NoPadding";
    private static final String RSA_ALGO = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    private final GePNICCertificateProvider certificateProvider;
    private final GePNICSignatureVerifier signatureVerifier;
    private final GePNICProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    public GePNICEncryptionService(GePNICCertificateProvider certificateProvider,
                                  GePNICSignatureVerifier signatureVerifier,
                                  GePNICProperties properties) {
        this.certificateProvider = certificateProvider;
        this.signatureVerifier = signatureVerifier;
        this.properties = properties;
    }

    public GePNICEnvelopeForm buildEnvelope(String plaintextJson) {
        try {
            byte[] plaintextBytes = plaintextJson.getBytes(StandardCharsets.UTF_8);

            // 1. Digital signature over plaintext
            String signData = signatureVerifier.sign(plaintextBytes);

            // 2. Generate random 256-bit AES session key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, secureRandom);
            SecretKey sessionKey = keyGen.generateKey();

            // 3. Encrypt payload with AES-GCM
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher aesCipher = Cipher.getInstance(AES_ALGO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            aesCipher.init(Cipher.ENCRYPT_MODE, sessionKey, parameterSpec);
            byte[] cipherText = aesCipher.doFinal(plaintextBytes);

            // Prepend IV to ciphertext
            byte[] combinedCipherWithIv = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combinedCipherWithIv, 0, iv.length);
            System.arraycopy(cipherText, 0, combinedCipherWithIv, iv.length, cipherText.length);
            String encryptData = Base64.getEncoder().encodeToString(combinedCipherWithIv);

            // 4. Encrypt session key with Portal RSA public key
            PublicKey portalPublicKey = certificateProvider.getPortalPublicKey();
            Cipher rsaCipher = Cipher.getInstance(RSA_ALGO);
            rsaCipher.init(Cipher.ENCRYPT_MODE, portalPublicKey);
            byte[] encryptedKeyBytes = rsaCipher.doFinal(sessionKey.getEncoded());
            String encryptKey = Base64.getEncoder().encodeToString(encryptedKeyBytes);

            // 5. Construct envelope
            return new GePNICEnvelopeForm(
                    encryptData,
                    signData,
                    properties.getUniqueUserId(),
                    encryptKey,
                    certificateProvider.getCurrentKeyId()
            );
        } catch (Exception e) {
            throw new IntegrationException("Failed to encrypt GePNIC payload envelope: " + e.getMessage(), e, null, "ENCRYPTION_FAILED");
        }
    }
}

