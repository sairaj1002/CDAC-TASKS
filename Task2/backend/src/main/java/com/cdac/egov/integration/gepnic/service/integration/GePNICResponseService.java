package com.cdac.egov.integration.gepnic.service.integration;

import com.cdac.egov.integration.gepnic.forms.encryption.DecryptionResultForm;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptedEnvelopeForm;
import com.cdac.egov.integration.gepnic.service.encryption.GePNICDecryptionService;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Encapsulates inbound GePNIC response processing, decryption, and verification.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GePNICResponseService {

    private final GePNICDecryptionService decryptionService;
    private final GePNICJsonParser jsonParser;
    private final ObjectMapper objectMapper;

    /**
     * Decrypts and parses an incoming response string.
     */
    public GePNICJsonParser.ParsedResponse processResponse(String rawResponseBody) {
        try {
            // Check if response is an encrypted envelope JSON
            if (rawResponseBody.contains("encryptData") && rawResponseBody.contains("encryptKey")) {
                EncryptedEnvelopeForm envelope = objectMapper.readValue(rawResponseBody, EncryptedEnvelopeForm.class);
                DecryptionResultForm decryptionResult = decryptionService.decryptEnvelope(envelope);
                return jsonParser.parseResponse(decryptionResult.getDecryptedPayloadJson());
            }

            // Otherwise direct JSON (e.g. standard JSON response or development mock)
            return jsonParser.parseResponse(rawResponseBody);
        } catch (Exception e) {
            log.error("Failed to process GePNIC response: {}", e.getMessage());
            return new GePNICJsonParser.ParsedResponse(0, 0, "Response processing failed: " + e.getMessage(), null);
        }
    }
}
