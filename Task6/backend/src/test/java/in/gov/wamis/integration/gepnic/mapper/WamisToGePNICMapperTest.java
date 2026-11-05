package in.gov.wamis.integration.gepnic.mapper;

import in.gov.wamis.master.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.wamis.integration.gepnic.config.GePNICConfig;
import in.gov.wamis.integration.gepnic.config.GePNICProperties;
import in.gov.wamis.procurement.pretender.PreTender;
import in.gov.wamis.procurement.pretender.PreTenderDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WamisToGePNICMapperTest {

    private WamisToGePNICMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        GePNICConfig config = new GePNICConfig(new GePNICProperties());
        this.objectMapper = config.gepnicObjectMapper();
        this.mapper = new WamisToGePNICMapper(objectMapper);
    }

    private PreTender createSamplePreTender() {
        PreTender pt = new PreTender();
        pt.setUniqueFileNo("WAMIS-FILE-UNIQUE-NO");
        pt.setTenderReferenceNo("WAMIS-TENDER-REFERENCE");
        pt.setOrgId(2297L);
        pt.setTenderTitle("Sample road improvement");
        pt.setTenderDescription("Sample work description");
        pt.setTenderType(new MasterTenderType("Open Tender"));
        pt.setFormContract(new MasterFormOfContract("Supply"));
        pt.setCategory(new MasterTenderCategory("Works"));
        pt.setCoversCount(2);
        pt.setBidOpeners(new MasterBidOpenersType("2 Off 2"));
        pt.setAutomaticEvaluation(false);
        pt.setAllowMultiCurrencyForBoq(true);
        pt.setCreatorName("Sample Creator");
        pt.setCreatorReference("WAMIS");
        pt.setTenderValue(new BigDecimal("469298.00"));
        pt.setBidValidityDays(60);
        pt.setDeliveryPeriodDays(0);
        pt.setLocation("Sample location");
        pt.setPincode(411001);
        pt.setPreBidMeeting(false);
        pt.setBidOpenPlace("Sample office");
        pt.setTendererClass(new MasterTendererClass("Class A"));
        pt.setInvitingOfficer("Sample Executive Engineer");
        pt.setInvitingOfficerAddress("Sample office address");
        pt.setFeeAmount(new BigDecimal("500.00"));
        pt.setEmdAmount(new BigDecimal("9386.00"));
        pt.setAllowMultiCurrencyForFee(false);

        LocalDateTime pubDate = LocalDateTime.of(2026, 11, 1, 10, 30, 0);
        pt.setPublishingDate(pubDate);
        pt.setDocDownloadStartDate(pubDate.plusMinutes(30));
        pt.setSeekClarificationStartDate(LocalDateTime.of(2026, 11, 2, 10, 15, 0));
        pt.setSeekClarificationEndDate(LocalDateTime.of(2026, 11, 5, 10, 15, 0));
        pt.setBidSubmissionStartDate(LocalDateTime.of(2026, 11, 10, 10, 0, 0));
        pt.setBidSubmissionEndDate(LocalDateTime.of(2026, 11, 20, 17, 0, 0));
        pt.setBidOpenDate(LocalDateTime.of(2026, 11, 21, 10, 0, 0));

        pt.addDocument(new PreTenderDocument(
                1L, "NITDOC", "tender-notice.pdf", "Notice inviting tender", ".pdf", 12000L, 1
        ));
        pt.addDocument(new PreTenderDocument(
                2L, "BOQDOC", "financial-boq.xls", "Bill of quantities", ".xls", 24000L, 2
        ));
        return pt;
    }

    @Test
    @DisplayName("Maps PreTender to exact GePNIC schema structure and casing")
    void testExactSchemaMapping() throws Exception {
        PreTender pt = createSamplePreTender();
        String json = mapper.mapToJson(pt);
        assertNotNull(json);

        JsonNode root = objectMapper.readTree(json);
        assertTrue(root.has("inputData"));

        JsonNode inputData = root.get("inputData");
        assertEquals("WAMIS-TENDER-REFERENCE", inputData.get("uniqueRefNo").asText());
        assertTrue(inputData.has("inputString"));

        JsonNode inputString = inputData.get("inputString");
        assertTrue(inputString.has("PRETENDER"));

        JsonNode pretender = inputString.get("PRETENDER");
        assertTrue(pretender.has("TENDER"));
        JsonNode tenderArray = pretender.get("TENDER");
        assertTrue(tenderArray.isArray());
        assertEquals(1, tenderArray.size());

        JsonNode tender = tenderArray.get(0);
        assertTrue(tender.has("TENDER_BASIC_DETAILS"));
        assertTrue(tender.has("TENDER_WORKITEM"));
        assertTrue(tender.has("TENDER_DOCS_DETAILS"));

        // BASICINFO check
        JsonNode basicInfo = tender.get("TENDER_BASIC_DETAILS").get("BASICINFO");
        assertEquals("WAMIS-FILE-UNIQUE-NO", basicInfo.get("UNIQUE_FILE_NO").asText());
        assertEquals(2297, basicInfo.get("ACTUAL_ORGID").asInt());
        assertEquals("Open Tender", basicInfo.get("T_TYPE").asText());
        assertEquals("Supply", basicInfo.get("T_FORM_CONTRACT").asText());
        assertEquals(2, basicInfo.get("T_NO_OF_COVERS").asInt());
        assertFalse(basicInfo.get("T_ALLOW_AUTOMATIC_EVALUATION").asBoolean());
        assertTrue(basicInfo.get("T_ALLOW_MULTI_CURRENCY_FOR_BOQ").asBoolean());

        // WORKITEM check
        JsonNode workItem = tender.get("TENDER_WORKITEM").get("WORKITEM");
        assertEquals("Sample road improvement", workItem.get("T_WORKITEM_TITLE").asText());
        assertEquals(469298.0, workItem.get("T_WORKITEM_TENDER_VALUE").asDouble());
        assertEquals(411001, workItem.get("T_PINCODE").asInt());

        // FINANCIALS & CRITICAL DATES check
        JsonNode fee = tender.get("TENDER_WORKITEM").get("TENDER_FEE_DETAILS");
        assertEquals(500.0, fee.get("T_FEE").asDouble());
        assertEquals(9386.0, fee.get("T_EMD_FEE").asDouble());

        JsonNode dates = tender.get("TENDER_WORKITEM").get("TENDER_CRITICAL_DATES");
        assertEquals("2026-11-01T10:30:00", dates.get("T_PUBLISHING_DATE").asText());
        assertEquals("2026-11-21T10:00:00", dates.get("T_BID_OPEN_DATE").asText());

        // DOCS check: NITDOC mapped to BASIC_DOC, BOQDOC to WORKITEM_DOC
        JsonNode docs = tender.get("TENDER_DOCS_DETAILS");
        assertEquals("NITDOC", docs.get("BASIC_DOC").get("DOC").get(0).get("T_DOC_CODE").asText());
        assertEquals("BOQDOC", docs.get("WORKITEM_DOC").get("DOC").get(0).get("T_DOC_CODE").asText());
    }
}

