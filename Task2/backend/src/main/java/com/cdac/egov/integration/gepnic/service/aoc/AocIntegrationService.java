package com.cdac.egov.integration.gepnic.service.aoc;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import com.cdac.egov.integration.gepnic.entity.IntegrationTransaction;
import com.cdac.egov.integration.gepnic.enums.IntegrationOperation;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import com.cdac.egov.integration.gepnic.forms.aoc.AocDownloadForm;
import com.cdac.egov.integration.gepnic.forms.aoc.AocPullForm;
import com.cdac.egov.integration.gepnic.forms.aoc.AocResponseForm;
import com.cdac.egov.integration.gepnic.mapper.GePNICToWamisMapper;
import com.cdac.egov.integration.gepnic.service.integration.GePNICRequestService;
import com.cdac.egov.integration.gepnic.service.integration.GePNICResponseService;
import com.cdac.egov.integration.gepnic.service.integration.IntegrationOrchestrator;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles Award of Contract (AOC) information retrieval and document downloads from GePNIC.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AocIntegrationService {

    private final GePNICRequestService requestService;
    private final GePNICResponseService responseService;
    private final GePNICToWamisMapper gepnicToWamisMapper;
    private final IntegrationOrchestrator orchestrator;
    private final GePNICProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * Pulls AOC data for a specified created date (format: yyyyMMdd).
     */
    public List<AocResponseForm> pullAocByCreatedDate(AocPullForm form) {
        String path = properties.getEndpoint().getAocPullPath();
        String payload = "{\"createdDate\":\"" + form.getCreatedDate() + "\"}";

        IntegrationTransaction txn = orchestrator.startTransaction(
                form.getCreatedDate(),
                IntegrationOperation.AOC_PULL,
                null,
                path,
                payload
        );

        try {
            ResponseEntity<String> response = requestService.executeEncryptedRequest(path, payload);
            GePNICJsonParser.ParsedResponse parsed = responseService.processResponse(response.getBody());

            List<AocResponseForm> results = new ArrayList<>();
            if (parsed.rawJsonNode() != null && parsed.rawJsonNode().isArray()) {
                for (var node : parsed.rawJsonNode()) {
                    results.add(gepnicToWamisMapper.mapToAocResponseForm(node));
                }
            } else if (parsed.rawJsonNode() != null) {
                results.add(gepnicToWamisMapper.mapToAocResponseForm(parsed.rawJsonNode()));
            }

            orchestrator.completeTransaction(
                    txn,
                    IntegrationStatus.SUCCESS,
                    parsed.outerCode(),
                    "Retrieved " + results.size() + " AOC records",
                    response.getBody(),
                    results.size()
            );

            return results;
        } catch (Exception e) {
            log.error("Failed to pull AOC records: {}", e.getMessage());
            orchestrator.completeTransaction(txn, IntegrationStatus.FAILED, 0, e.getMessage(), null, 0);
            return Collections.emptyList();
        }
    }

    /**
     * Downloads an AOC document mapped according to the NIC specification.
     */
    public byte[] downloadAocDocument(AocDownloadForm form) {
        String path = properties.getEndpoint().getAocDownloadPath();
        try {
            String payload = objectMapper.writeValueAsString(form);
            ResponseEntity<String> response = requestService.executeEncryptedRequest(path, payload);
            // In demo/test mode, return file content or decoded bytes
            return response.getBody() != null ? response.getBody().getBytes(StandardCharsets.UTF_8) : new byte[0];
        } catch (Exception e) {
            log.error("Failed to download AOC document {}: {}", form.getFileName(), e.getMessage());
            return new byte[0];
        }
    }
}
