package com.cdac.egov.integration.gepnic.service.tender;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import com.cdac.egov.integration.gepnic.entity.IntegrationTransaction;
import com.cdac.egov.integration.gepnic.enums.IntegrationOperation;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import com.cdac.egov.integration.gepnic.forms.tender.CorrigendumPullForm;
import com.cdac.egov.integration.gepnic.forms.tender.CorrigendumResponseForm;
import com.cdac.egov.integration.gepnic.forms.tender.TenderPullForm;
import com.cdac.egov.integration.gepnic.forms.tender.TenderResponseForm;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Executes Tender and Corrigendum retrieval operations from GePNIC by publish date.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenderPullService {

    private final GePNICRequestService requestService;
    private final GePNICResponseService responseService;
    private final GePNICToWamisMapper gepnicToWamisMapper;
    private final IntegrationOrchestrator orchestrator;
    private final GePNICProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * Pulls tenders published on the given date (format: yyyyMMdd).
     */
    public List<TenderResponseForm> pullTendersByDate(TenderPullForm form) {
        String path = properties.getEndpoint().getTenderPullPath();
        String payload = "{\"publishedDate\":\"" + form.getPublishedDate() + "\"}";

        IntegrationTransaction txn = orchestrator.startTransaction(
                form.getPublishedDate(),
                IntegrationOperation.TENDER_PULL,
                form.getOfficeCode(),
                path,
                payload
        );

        try {
            ResponseEntity<String> response = requestService.executeEncryptedRequest(path, payload);
            GePNICJsonParser.ParsedResponse parsed = responseService.processResponse(response.getBody());

            List<TenderResponseForm> results = new ArrayList<>();
            if (parsed.rawJsonNode() != null && parsed.rawJsonNode().isArray()) {
                for (var node : parsed.rawJsonNode()) {
                    results.add(gepnicToWamisMapper.mapToTenderResponseForm(node, form.getTenderReference()));
                }
            } else if (parsed.rawJsonNode() != null) {
                results.add(gepnicToWamisMapper.mapToTenderResponseForm(parsed.rawJsonNode(), form.getTenderReference()));
            }

            orchestrator.completeTransaction(
                    txn,
                    IntegrationStatus.SUCCESS,
                    parsed.outerCode(),
                    "Retrieved " + results.size() + " tenders",
                    response.getBody(),
                    results.size()
            );

            return results;
        } catch (Exception e) {
            log.error("Failed to pull tenders for date {}: {}", form.getPublishedDate(), e.getMessage());
            orchestrator.completeTransaction(
                    txn,
                    IntegrationStatus.FAILED,
                    0,
                    e.getMessage(),
                    null,
                    0
            );
            return Collections.emptyList();
        }
    }

    /**
     * Pulls corrigendums published on the given date (format: yyyyMMdd).
     */
    public List<CorrigendumResponseForm> pullCorrigendumsByDate(CorrigendumPullForm form) {
        String path = properties.getEndpoint().getCorrigendumPullPath();
        String payload = "{\"publishedDate\":\"" + form.getPublishedDate() + "\"}";

        IntegrationTransaction txn = orchestrator.startTransaction(
                form.getPublishedDate(),
                IntegrationOperation.CORRIGENDUM_PULL,
                null,
                path,
                payload
        );

        try {
            ResponseEntity<String> response = requestService.executeEncryptedRequest(path, payload);
            GePNICJsonParser.ParsedResponse parsed = responseService.processResponse(response.getBody());

            List<CorrigendumResponseForm> results = new ArrayList<>();
            if (parsed.rawJsonNode() != null && parsed.rawJsonNode().isArray()) {
                for (var node : parsed.rawJsonNode()) {
                    results.add(gepnicToWamisMapper.mapToCorrigendumResponseForm(node));
                }
            }

            orchestrator.completeTransaction(
                    txn,
                    IntegrationStatus.SUCCESS,
                    parsed.outerCode(),
                    "Retrieved " + results.size() + " corrigendums",
                    response.getBody(),
                    results.size()
            );

            return results;
        } catch (Exception e) {
            log.error("Failed to pull corrigendums: {}", e.getMessage());
            orchestrator.completeTransaction(txn, IntegrationStatus.FAILED, 0, e.getMessage(), null, 0);
            return Collections.emptyList();
        }
    }
}
