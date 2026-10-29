package com.cdac.egov.integration.gepnic.service.tender;

import com.cdac.egov.integration.gepnic.exception.GePNICException;
import com.cdac.egov.integration.gepnic.forms.tender.TenderPushForm;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Validates incoming tender business rules and document attachments.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenderValidationService {

    private final GePNICJsonValidationService jsonValidationService;

    /**
     * Validates a tender push request before processing.
     */
    public void validate(TenderPushForm form) {
        List<String> errors = jsonValidationService.validateTenderPushForm(form);
        if (!errors.isEmpty()) {
            throw new GePNICException("Tender validation failed: " + String.join(", ", errors));
        }
    }
}
