package com.cdac.egov.integration.gepnic.service.integration;

import com.cdac.egov.integration.gepnic.forms.encryption.EncryptedEnvelopeForm;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptionResultForm;
import com.cdac.egov.integration.gepnic.service.encryption.GePNICEncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Encapsulates outbound GePNIC request preparation and execution.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GePNICRequestService {

    private final GePNICEncryptionService encryptionService;
    private final GePNICHttpClient httpClient;

    /**
     * Encrypts the payload, builds the envelope, and sends it to the target GePNIC endpoint.
     */
    public ResponseEntity<String> executeEncryptedRequest(String targetPath, String jsonPayload) {
        EncryptionResultForm encryptionResult = encryptionService.encryptPayload(jsonPayload);
        EncryptedEnvelopeForm envelope = encryptionResult.getEnvelope();
        return httpClient.postEnvelope(targetPath, envelope);
    }
}
