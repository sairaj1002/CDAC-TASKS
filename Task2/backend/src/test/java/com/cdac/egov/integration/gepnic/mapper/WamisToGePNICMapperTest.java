package com.cdac.egov.integration.gepnic.mapper;

import com.cdac.egov.integration.gepnic.config.JacksonConfig;
import com.cdac.egov.integration.gepnic.forms.tender.TenderPushForm;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WamisToGePNICMapperTest {

    private WamisToGePNICMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new JacksonConfig().objectMapper();
        GePNICJsonBuilder builder = new GePNICJsonBuilder(objectMapper);
        mapper = new WamisToGePNICMapper(builder);
    }

    @Test
    void testMapToGePNICTenderJson_validInput() throws Exception {
        TenderPushForm form = TenderPushForm.builder()
                .wamisTenderId("WAMIS-TND-2026-000123")
                .officeId("OFF-001")
                .tenderTitle("Road Improvement Work")
                .estimatedCost(BigDecimal.valueOf(2500000.00))
                .publishDate(LocalDateTime.now())
                .submissionEndDate(LocalDateTime.now().plusDays(20))
                .documents(List.of(
                        TenderPushForm.TenderDocumentItemForm.builder()
                                .documentId("DOC-01")
                                .fileName("nit.pdf")
                                .contentType(".pdf")
                                .docCode("NITDOC")
                                .build()
                ))
                .build();

        String json = mapper.mapToGePNICTenderJson(form, "WAMIS-REF-001", "FILE-001", 2297);
        assertNotNull(json);

        JsonNode root = objectMapper.readTree(json);
        assertTrue(root.has("inputData"));
        assertEquals("WAMIS-REF-001", root.get("inputData").get("uniqueRefNo").asText());
        JsonNode tenderArray = root.get("inputData").get("inputString").get("PRETENDER").get("TENDER");
        assertEquals(1, tenderArray.size());
    }
}
