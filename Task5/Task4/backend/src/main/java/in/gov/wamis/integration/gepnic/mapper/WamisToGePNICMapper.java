package in.gov.wamis.integration.gepnic.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.wamis.common.constants.CommonConstants;
import in.gov.wamis.common.exception.IntegrationException;
import in.gov.wamis.procurement.pretender.PreTender;
import in.gov.wamis.procurement.pretender.PreTenderDocument;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Maps authoritative WAMIS PreTender entity into the exact GePNIC JSON schema
 * defined in Section 6.1 of the integration specification.
 * Preserves exact property casing, data types, and nesting hierarchy.
 */
@Component
public class WamisToGePNICMapper {

    private final ObjectMapper objectMapper;

    public WamisToGePNICMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String mapToJson(PreTender preTender) {
        Map<String, Object> payloadMap = mapToSchemaMap(preTender);
        try {
            return objectMapper.writeValueAsString(payloadMap);
        } catch (Exception e) {
            throw new IntegrationException("Failed to serialize GePNIC tender JSON: " + e.getMessage(), e, null, "SERIALIZATION_FAILED");
        }
    }

    public Map<String, Object> mapToSchemaMap(PreTender pt) {
        if (pt == null) {
            throw new IllegalArgumentException("PreTender cannot be null for mapping");
        }

        // --- 1. BASICINFO ---
        Map<String, Object> basicInfo = new LinkedHashMap<>();
        basicInfo.put("UNIQUE_FILE_NO", pt.getUniqueFileNo());
        basicInfo.put("ACTUAL_ORGID", pt.getOrgId() != null ? pt.getOrgId() : 2297);
        basicInfo.put("T_REF_NO", pt.getTenderReferenceNo());
        basicInfo.put("T_TYPE", pt.getTenderType() != null ? pt.getTenderType() : "Open Tender");
        basicInfo.put("T_FORM_CONTRACT", pt.getFormContract() != null ? pt.getFormContract() : "Supply");
        basicInfo.put("T_NO_OF_COVERS", pt.getCoversCount() != null ? pt.getCoversCount() : 2);
        basicInfo.put("T_TENDER_CATEGORY", pt.getCategory() != null ? pt.getCategory() : "Works");
        basicInfo.put("T_NO_OF_BID_OPENERS", pt.getBidOpeners() != null ? pt.getBidOpeners() : "2 Off 2");
        basicInfo.put("T_ALLOW_AUTOMATIC_EVALUATION", Boolean.TRUE.equals(pt.getAutomaticEvaluation()));
        basicInfo.put("T_ALLOW_MULTI_CURRENCY_FOR_BOQ", Boolean.TRUE.equals(pt.getAllowMultiCurrencyForBoq()));
        basicInfo.put("CREATOR_NAME", pt.getCreatorName() != null ? pt.getCreatorName() : "WAMIS Officer");
        basicInfo.put("CREATOR_REFERENCE", pt.getCreatorReference() != null ? pt.getCreatorReference() : "WAMIS");

        // --- COVERINFO ---
        List<Map<String, Object>> coverList = new ArrayList<>();
        int covers = pt.getCoversCount() != null ? pt.getCoversCount() : 2;
        for (int i = 1; i <= covers; i++) {
            Map<String, Object> cover = new LinkedHashMap<>();
            cover.put("T_COVER_NO", i);
            cover.put("T_DOC_DESC", i == 1 ? "Technical / Fee Details" : "Financial BOQ");
            cover.put("T_DOC_TYPE", i == 1 ? ".pdf" : ".xls");
            coverList.add(cover);
        }
        Map<String, Object> coverInfo = new LinkedHashMap<>();
        coverInfo.put("COVER", coverList);

        Map<String, Object> basicDetails = new LinkedHashMap<>();
        basicDetails.put("BASICINFO", basicInfo);
        basicDetails.put("MULTI_CURRENCY_FOR_BOQ", Collections.singletonMap("NAME", Collections.emptyList()));
        basicDetails.put("LIMITED_BIDDERS", Collections.singletonMap("BIDDER_LOGIN_ID", Collections.emptyList()));
        basicDetails.put("BOQINFO", null);
        basicDetails.put("COVERINFO", coverInfo);

        // --- 2. WORKITEM ---
        Map<String, Object> workItem = new LinkedHashMap<>();
        workItem.put("T_WORKITEM_TITLE", pt.getTenderTitle());
        workItem.put("T_WORKITEM_DESC", pt.getTenderDescription() != null ? pt.getTenderDescription() : pt.getTenderTitle());
        workItem.put("T_WORKITEM_PRE_QUAL", "NA");
        workItem.put("T_WORKITEM_PROD_CAT", "Civil Works");
        workItem.put("T_WORKITEM_PROD_SUB_CAT", "Road");
        workItem.put("T_WORKITEM_CONTRACT_TYPE", "Tender");
        workItem.put("T_WORKITEM_TENDER_VALUE", pt.getTenderValue() != null ? pt.getTenderValue().doubleValue() : 0.0);
        workItem.put("T_BID_VALIDITY_DAYS", pt.getBidValidityDays() != null ? pt.getBidValidityDays() : 60);
        workItem.put("T_DELIVERY_PERIOD_DAYS", pt.getDeliveryPeriodDays() != null ? pt.getDeliveryPeriodDays() : 0);
        workItem.put("T_ALLOW_GTE_DETAILS", false);
        workItem.put("T_TEMPLATE_NAME", "NA");
        workItem.put("T_LOCATION_DETAILS", pt.getLocation() != null ? pt.getLocation() : "Office Location");
        workItem.put("T_PINCODE", pt.getPincode() != null ? pt.getPincode() : 411001);
        workItem.put("T_PRE_BID_MEETING", Boolean.TRUE.equals(pt.getPreBidMeeting()));
        workItem.put("T_PRE_BID_MEETING_PLACE", pt.getPreBidMeetingPlace() != null ? pt.getPreBidMeetingPlace() : "NA");
        workItem.put("T_PRE_BID_MEETING_ADDRESS", pt.getPreBidMeetingAddress() != null ? pt.getPreBidMeetingAddress() : "NA");
        workItem.put("T_BID_OPENNING_PLACE", pt.getBidOpenPlace() != null ? pt.getBidOpenPlace() : "Division Office");
        workItem.put("T_TENDERER_CLASS", pt.getTendererClass() != null ? pt.getTendererClass() : "Class A");
        workItem.put("T_INVITING_OFFICER", pt.getInvitingOfficer());
        workItem.put("T_INVITING_OFF_ADDRESS", pt.getInvitingOfficerAddress());
        workItem.put("T_ALLOW_ITEMWISE_EVALUATION", Boolean.TRUE.equals(pt.getAllowItemwiseEvaluation()));

        // --- TENDER_FEE_DETAILS ---
        Map<String, Object> feeDetails = new LinkedHashMap<>();
        feeDetails.put("T_FEE", pt.getFeeAmount() != null ? pt.getFeeAmount().doubleValue() : 0.0);
        feeDetails.put("T_EMD_FEE", pt.getEmdAmount() != null ? pt.getEmdAmount().doubleValue() : 0.0);
        feeDetails.put("T_ALLOW_MULTI_CURRENCY_FOR_FEE", Boolean.TRUE.equals(pt.getAllowMultiCurrencyForFee()));
        feeDetails.put("MULTI_CURRENCY_FOR_FEE", Collections.singletonMap("CURRENCY", Collections.emptyList()));

        // --- TENDER_CRITICAL_DATES ---
        Map<String, Object> criticalDates = new LinkedHashMap<>();
        criticalDates.put("T_PUBLISHING_DATE", formatDate(pt.getPublishingDate()));
        criticalDates.put("T_DOC_DOWNLOAD_START_DATE", formatDate(pt.getDocDownloadStartDate()));
        criticalDates.put("T_SEEK_CLARIFICATION_START_DATE", formatDate(pt.getSeekClarificationStartDate()));
        criticalDates.put("T_PRE_BID_MEETING_DATE", formatDate(pt.getPreBidMeetingDate()));
        criticalDates.put("T_SEEK_CLARIFICATION_END_DATE", formatDate(pt.getSeekClarificationEndDate()));
        criticalDates.put("T_BIDSUB_START_DATE", formatDate(pt.getBidSubmissionStartDate()));
        criticalDates.put("T_BIDSUB_END_DATE", formatDate(pt.getBidSubmissionEndDate()));
        criticalDates.put("T_BID_OPEN_DATE", formatDate(pt.getBidOpenDate()));

        Map<String, Object> tenderWorkItem = new LinkedHashMap<>();
        tenderWorkItem.put("WORKITEM", workItem);
        tenderWorkItem.put("TENDER_FEE_DETAILS", feeDetails);
        tenderWorkItem.put("TENDER_CRITICAL_DATES", criticalDates);

        // --- 3. TENDER_DOCS_DETAILS ---
        List<Map<String, Object>> basicDocList = new ArrayList<>();
        List<Map<String, Object>> workitemDocList = new ArrayList<>();

        if (pt.getDocuments() != null) {
            for (PreTenderDocument doc : pt.getDocuments()) {
                Map<String, Object> docMap = new LinkedHashMap<>();
                docMap.put("T_DOC_CODE", doc.getDocCode());
                docMap.put("T_DOC_NAME", doc.getDocName());
                docMap.put("T_DOC_DESCRIPTION", doc.getDocDescription() != null ? doc.getDocDescription() : doc.getDocName());
                docMap.put("T_DOC_MIME_TYPE", doc.getDocMimeType());
                docMap.put("T_DOC_SIZE", doc.getDocSize());
                docMap.put("T_DOC_PRIORITY", doc.getDocPriority());
                docMap.put("T_DOC_UNIQUE_ID", doc.getDocUniqueId());

                if (doc.getDocCode() != null && doc.getDocCode().toUpperCase().contains("NIT")) {
                    basicDocList.add(docMap);
                } else {
                    workitemDocList.add(docMap);
                }
            }
        }

        Map<String, Object> docsDetails = new LinkedHashMap<>();
        docsDetails.put("BASIC_DOC", Collections.singletonMap("DOC", basicDocList));
        docsDetails.put("WORKITEM_DOC", Collections.singletonMap("DOC", workitemDocList));

        // --- Complete Tender Node ---
        Map<String, Object> tender = new LinkedHashMap<>();
        tender.put("TENDER_BASIC_DETAILS", basicDetails);
        tender.put("TENDER_WORKITEM", tenderWorkItem);
        tender.put("TENDER_DOCS_DETAILS", docsDetails);

        // --- Envelope: inputData -> uniqueRefNo + inputString -> PRETENDER -> TENDER[] ---
        Map<String, Object> pretenderWrapper = new LinkedHashMap<>();
        pretenderWrapper.put("TENDER", Collections.singletonList(tender));

        Map<String, Object> inputString = new LinkedHashMap<>();
        inputString.put("PRETENDER", pretenderWrapper);

        Map<String, Object> inputData = new LinkedHashMap<>();
        inputData.put("uniqueRefNo", pt.getTenderReferenceNo());
        inputData.put("inputString", inputString);

        return Collections.singletonMap("inputData", inputData);
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(CommonConstants.DATE_TIME_FORMATTER);
    }
}

