package com.cdac.egov.integration.gepnic.service.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Builds the canonical GePNIC tender JSON hierarchy per the NIC integration specification:
 * inputData -> uniqueRefNo, inputString -> PRETENDER -> TENDER[]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GePNICJsonBuilder {

    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Builds the complete NIC-GePNIC JSON push payload from mapped tender parameters.
     */
    public String buildTenderPushJson(
            String uniqueRefNo,
            String uniqueFileNo,
            int actualOrgId,
            String tenderRefNo,
            String title,
            String description,
            BigDecimal estimatedValue,
            BigDecimal tenderFee,
            BigDecimal emdAmount,
            LocalDateTime publishDate,
            LocalDateTime bidSubEndDate,
            List<Map<String, Object>> documents) {

        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode inputData = root.putObject("inputData");
        inputData.put("uniqueRefNo", uniqueRefNo);

        ObjectNode inputString = inputData.putObject("inputString");
        ObjectNode pretender = inputString.putObject("PRETENDER");
        ArrayNode tenderArray = pretender.putArray("TENDER");

        ObjectNode tenderEntry = tenderArray.addObject();

        // 1. TENDER_BASIC_DETAILS
        ObjectNode basicDetails = tenderEntry.putObject("TENDER_BASIC_DETAILS");
        ObjectNode basicInfo = basicDetails.putObject("BASICINFO");
        basicInfo.put("UNIQUE_FILE_NO", uniqueFileNo);
        basicInfo.put("ACTUAL_ORGID", actualOrgId);
        basicInfo.put("T_REF_NO", tenderRefNo);
        basicInfo.put("T_TYPE", "Open Tender");
        basicInfo.put("T_FORM_CONTRACT", "Supply");
        basicInfo.put("T_NO_OF_COVERS", 2);
        basicInfo.put("T_TENDER_CATEGORY", "Works");
        basicInfo.put("T_ALLOW_AUTOMATIC_EVALUATION", false);
        basicInfo.put("T_ALLOW_MULTI_CURRENCY_FOR_BOQ", false);
        basicInfo.put("CREATOR_NAME", "WAMIS Integration");
        basicInfo.put("CREATOR_REFERENCE", "WAMIS");

        // 2. TENDER_WORKITEM
        ObjectNode workItemNode = tenderEntry.putObject("TENDER_WORKITEM");
        ObjectNode workItem = workItemNode.putObject("WORKITEM");
        workItem.put("T_WORKITEM_TITLE", title);
        workItem.put("T_WORKITEM_DESC", description != null ? description : title);
        workItem.put("T_WORKITEM_PRE_QUAL", "NA");
        workItem.put("T_WORKITEM_PROD_CAT", "Civil Works");
        workItem.put("T_WORKITEM_PROD_SUB_CAT", "Road");
        workItem.put("T_WORKITEM_CONTRACT_TYPE", "Tender");
        workItem.put("T_WORKITEM_TENDER_VALUE", estimatedValue);
        workItem.put("T_BID_VALIDITY_DAYS", 90);
        workItem.put("T_DELIVERY_PERIOD_DAYS", 180);
        workItem.put("T_PRE_BID_MEETING", false);

        // Fee Details
        ObjectNode feeDetails = workItemNode.putObject("TENDER_FEE_DETAILS");
        feeDetails.put("T_FEE", tenderFee != null ? tenderFee : BigDecimal.ZERO);
        feeDetails.put("T_EMD_FEE", emdAmount != null ? emdAmount : BigDecimal.ZERO);
        feeDetails.put("T_ALLOW_MULTI_CURRENCY_FOR_FEE", false);

        // Critical Dates
        ObjectNode criticalDates = workItemNode.putObject("TENDER_CRITICAL_DATES");
        String pubStr = publishDate != null ? publishDate.format(DATE_FORMATTER) : LocalDateTime.now().format(DATE_FORMATTER);
        String endStr = bidSubEndDate != null ? bidSubEndDate.format(DATE_FORMATTER) : LocalDateTime.now().plusDays(21).format(DATE_FORMATTER);

        criticalDates.put("T_PUBLISHING_DATE", pubStr);
        criticalDates.put("T_DOC_DOWNLOAD_START_DATE", pubStr);
        criticalDates.put("T_BIDSUB_START_DATE", pubStr);
        criticalDates.put("T_BIDSUB_END_DATE", endStr);
        criticalDates.put("T_BID_OPEN_DATE", bidSubEndDate != null ? bidSubEndDate.plusDays(1).format(DATE_FORMATTER) : endStr);

        // 3. TENDER_DOCS_DETAILS
        ObjectNode docsDetails = tenderEntry.putObject("TENDER_DOCS_DETAILS");
        ObjectNode basicDoc = docsDetails.putObject("BASIC_DOC");
        ArrayNode docArray = basicDoc.putArray("DOC");

        if (documents != null && !documents.isEmpty()) {
            int priority = 1;
            for (Map<String, Object> doc : documents) {
                ObjectNode docItem = docArray.addObject();
                docItem.put("T_DOC_CODE", doc.getOrDefault("docCode", "NITDOC").toString());
                docItem.put("T_DOC_NAME", doc.getOrDefault("fileName", "tender-document.pdf").toString());
                docItem.put("T_DOC_DESCRIPTION", doc.getOrDefault("description", "Tender Document").toString());
                docItem.put("T_DOC_MIME_TYPE", doc.getOrDefault("mimeType", ".pdf").toString());
                docItem.put("T_DOC_SIZE", ((Number) doc.getOrDefault("size", 10240L)).longValue());
                docItem.put("T_DOC_PRIORITY", priority);
                docItem.put("T_DOC_UNIQUE_ID", priority);
                priority++;
            }
        }

        return root.toString();
    }
}
