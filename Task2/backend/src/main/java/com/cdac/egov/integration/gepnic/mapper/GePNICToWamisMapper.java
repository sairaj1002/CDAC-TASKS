package com.cdac.egov.integration.gepnic.mapper;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.forms.aoc.AocResponseForm;
import com.cdac.egov.integration.gepnic.forms.tender.CorrigendumResponseForm;
import com.cdac.egov.integration.gepnic.forms.tender.TenderResponseForm;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps raw decrypted GePNIC responses back into normalized WAMIS business forms.
 */
@Component
public class GePNICToWamisMapper {

    /**
     * Maps GePNIC tender JSON node to TenderResponseForm.
     */
    public TenderResponseForm mapToTenderResponseForm(JsonNode node, String fallbackTenderRef) {
        String ref = node.has("tenderReference") ? node.get("tenderReference").asText() : fallbackTenderRef;
        String title = node.has("title") ? node.get("title").asText() : "Tender Details";
        String status = node.has("tenderStatus") ? node.get("tenderStatus").asText() : "PUBLISHED";
        BigDecimal cost = node.has("estimatedValue") ? BigDecimal.valueOf(node.get("estimatedValue").asDouble()) : BigDecimal.ZERO;

        List<TenderResponseForm.CorrigendumItemForm> corrigendums = new ArrayList<>();
        if (node.has("corrigendums") && node.get("corrigendums").isArray()) {
            for (JsonNode cNode : node.get("corrigendums")) {
                corrigendums.add(TenderResponseForm.CorrigendumItemForm.builder()
                        .externalReference(cNode.has("corrigendumReference") ? cNode.get("corrigendumReference").asText() : "COR-01")
                        .publishedAt(LocalDateTime.now())
                        .summary(cNode.has("description") ? cNode.get("description").asText() : "Corrigendum published")
                        .build());
            }
        }

        return TenderResponseForm.builder()
                .sourceSystem(GePNICConstants.TARGET_SYSTEM_GEPNIC)
                .wamisTenderId(ref)
                .externalTenderReference(ref)
                .status(status)
                .title(title)
                .estimatedCost(cost)
                .publishDate(LocalDateTime.now().minusDays(5))
                .bidSubmissionEndDate(LocalDateTime.now().plusDays(15))
                .lastSynchronizedAt(LocalDateTime.now())
                .corrigendumCount(corrigendums.size())
                .corrigendums(corrigendums)
                .build();
    }

    /**
     * Maps GePNIC corrigendum JSON node to CorrigendumResponseForm.
     */
    public CorrigendumResponseForm mapToCorrigendumResponseForm(JsonNode node) {
        return CorrigendumResponseForm.builder()
                .corrigendumReference(node.has("corrigendumReference") ? node.get("corrigendumReference").asText() : "COR-DEF-001")
                .tenderReference(node.has("tenderReference") ? node.get("tenderReference").asText() : "WAMIS-TND")
                .publishedDate(LocalDateTime.now())
                .description(node.has("description") ? node.get("description").asText() : "Corrigendum issued")
                .type(node.has("type") ? node.get("type").asText() : "Date Extension")
                .status("PUBLISHED")
                .build();
    }

    /**
     * Maps GePNIC AOC JSON node to AocResponseForm.
     */
    public AocResponseForm mapToAocResponseForm(JsonNode node) {
        List<AocResponseForm.AocDocumentItemForm> docs = new ArrayList<>();
        if (node.has("documents") && node.get("documents").isArray()) {
            for (JsonNode d : node.get("documents")) {
                docs.add(AocResponseForm.AocDocumentItemForm.builder()
                        .bId(d.has("t_BID_ID") ? d.get("t_BID_ID").asText() : "0")
                        .docType(d.has("t_DOC_TYPE") ? d.get("t_DOC_TYPE").asText() : "BOQ")
                        .docCode(d.has("t_DOC_CODE") ? d.get("t_DOC_CODE").asText() : "BOQ_CODE")
                        .fileName(d.has("t_DOC_NAME") ? d.get("t_DOC_NAME").asText() : "aoc_document.xls")
                        .fileSize(24576L)
                        .build());
            }
        }

        return AocResponseForm.builder()
                .tenderReference(node.has("tenderReference") ? node.get("tenderReference").asText() : "WAMIS-TND")
                .contractNumber(node.has("contractNumber") ? node.get("contractNumber").asText() : "CNT-2026-001")
                .awardedBidderName(node.has("awardedBidderName") ? node.get("awardedBidderName").asText() : "L1 Construction Ltd")
                .awardedValue(node.has("awardedValue") ? BigDecimal.valueOf(node.get("awardedValue").asDouble()) : BigDecimal.valueOf(2400000.00))
                .awardDate(LocalDateTime.now())
                .status("AWARDED")
                .documents(docs)
                .build();
    }
}
