package com.cdac.egov.integration.gepnic.json;

import com.cdac.egov.integration.gepnic.config.JacksonConfig;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptedEnvelopeForm;
import com.cdac.egov.integration.gepnic.forms.tender.TenderPushForm;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GePNICJsonValidationServiceTest {

    private GePNICJsonValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new GePNICJsonValidationService(new JacksonConfig().objectMapper());
    }

    @Test
    void testValidateTenderPushForm_validForm() {
        TenderPushForm form = TenderPushForm.builder()
                .wamisTenderId("TND-100")
                .officeId("OFF-01")
                .tenderTitle("Pipeline work")
                .estimatedCost(BigDecimal.valueOf(100000))
                .publishDate(LocalDateTime.now())
                .submissionEndDate(LocalDateTime.now().plusDays(10))
                .build();

        List<String> errors = validationService.validateTenderPushForm(form);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateEnvelope_allFieldsPresent() {
        EncryptedEnvelopeForm envelope = EncryptedEnvelopeForm.builder()
                .uniqueUserId("USER-1")
                .encryptData("ENCRYPTED")
                .signData("SIGNATURE")
                .encryptKey("KEY")
                .keyId("THUMBPRINT")
                .build();

        assertDoesNotThrow(() -> validationService.validateEnvelope(envelope));
    }
}
