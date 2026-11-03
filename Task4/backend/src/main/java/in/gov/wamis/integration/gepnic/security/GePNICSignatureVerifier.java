package in.gov.wamis.integration.gepnic.security;

import in.gov.wamis.common.exception.IntegrationException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * Signs outgoing requests and verifies incoming GePNIC response signatures.
 */
@Component
public class GePNICSignatureVerifier {

    private final GePNICCertificateProvider certificateProvider;

    public GePNICSignatureVerifier(GePNICCertificateProvider certificateProvider) {
        this.certificateProvider = certificateProvider;
    }

    public String sign(byte[] data) {
        try {
            PrivateKey privateKey = certificateProvider.getClientPrivateKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data);
            byte[] signedBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception e) {
            throw new IntegrationException("Failed to generate digital signature: " + e.getMessage(), e, null, "SIGNATURE_FAILED");
        }
    }

    public String sign(String text) {
        return sign(text.getBytes(StandardCharsets.UTF_8));
    }

    public boolean verify(byte[] data, String base64Signature) {
        if (data == null || base64Signature == null || base64Signature.isBlank()) {
            return false;
        }
        try {
            PublicKey publicKey = certificateProvider.getPortalPublicKey();
            byte[] sigBytes = Base64.getDecoder().decode(base64Signature);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sigBytes);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verify(String text, String base64Signature) {
        return verify(text.getBytes(StandardCharsets.UTF_8), base64Signature);
    }
}

