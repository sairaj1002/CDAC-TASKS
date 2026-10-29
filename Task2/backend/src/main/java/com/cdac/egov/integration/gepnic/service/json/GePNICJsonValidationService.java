package com.cdac.egov.integration.gepnic.service.json;

import com.cdac.egov.integration.gepnic.exception.GePNICException;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptedEnvelopeForm;
import com.cdac.egov.integration.gepnic.forms.tender.TenderPushForm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates payload structures, required schema attributes, and business rules before transmission.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GePNICJsonValidationService {

    private final ObjectMapper objectMapper;

    /**
     * Validates input TenderPushForm business constraints.
     */
    public List<String> validateTenderPushForm(TenderPushForm form) {
        List<String> errors = new ArrayList<>();
        if (form.getWamisTenderId() == null || form.getWamisTenderId().isBlank()) {
            errors.add("wamisTenderId is required");
        }
        if (form.getOfficeId() == null || form.getOfficeId().isBlank()) {
            errors.add("officeId is required");
        }
        if (form.getEstimatedCost() == null || form.getEstimatedCost().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("estimatedCost must be greater than zero");
        }
        if (form.getPublishDate() != null && form.getSubmissionEndDate() != null) {
            if (form.getSubmissionEndDate().isBefore(form.getPublishDate())) {
                errors.add("submissionEndDate cannot be before publishDate");
            }
        }
        return errors;
    }

    /**
     * Validates that an encrypted envelope has all 5 required GePNIC protocol keys.
     */
    public void validateEnvelope(EncryptedEnvelopeForm envelope) {
        if (envelope == null) {
            throw new GePNICException("Encrypted envelope is null");
        }
        if (envelope.getUniqueUserId() == null || envelope.getUniqueUserId().isBlank()) {
            throw new GePNICException("Missing uniqueUserId in envelope");
        }
        if (envelope.getEncryptData() == null || envelope.getEncryptData().isBlank()) {
            throw new GePNICException("Missing encryptData in envelope");
        }
        if (envelope.getSignData() == null || envelope.getSignData().isBlank()) {
            throw new GePNICException("Missing signData in envelope");
        }
        if (envelope.getEncryptKey() == null || envelope.getEncryptKey().isBlank()) {
            throw new GePNICException("Missing encryptKey in envelope");
        }
        if (envelope.getKeyId() == null || envelope.getKeyId().isBlank()) {
            throw new GePNICException("Missing keyId in envelope");
        }
    }

    /**
     * Validates that a raw string is well-formed JSON.
     */
    public boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            return false;
        }
        try {
            JsonNode node = objectMapper.readTree(jsonString);
            return node != null;
        } catch (Exception e) {
            return false;
        }
    }
}
