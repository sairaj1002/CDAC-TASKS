package com.cdac.egov.integration.gepnic.service.encryption;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.exception.GePNICCryptoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Generates dynamic, cryptographically strong AES session keys per transaction.
 */
@Slf4j
@Service
public class SessionKeyService {

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a 256-bit AES symmetric session key.
     */
    public SecretKey generateSessionKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(GePNICConstants.ALGORITHM_AES);
            keyGen.init(GePNICConstants.AES_KEY_SIZE, secureRandom);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new GePNICCryptoException("AES algorithm not supported in this runtime", e);
        }
    }

    /**
     * Generates a random initialization vector (IV) for AES-GCM.
     */
    public byte[] generateIv() {
        byte[] iv = new byte[GePNICConstants.GCM_IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);
        return iv;
    }
}
