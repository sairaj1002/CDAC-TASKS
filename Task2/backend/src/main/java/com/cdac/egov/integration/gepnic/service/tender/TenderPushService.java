package com.cdac.egov.integration.gepnic.service.tender;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import com.cdac.egov.integration.gepnic.entity.IntegrationTransaction;
import com.cdac.egov.integration.gepnic.enums.IntegrationOperation;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import com.cdac.egov.integration.gepnic.forms.common.IntegrationResultForm;
import com.cdac.egov.integration.gepnic.forms.tender.TenderPushForm;
import com.cdac.egov.integration.gepnic.service.integration.GePNICRequestService;
import com.cdac.egov.integration.gepnic.service.integration.GePNICResponseService;
import com.cdac.egov.integration.gepnic.service.integration.IntegrationOrchestrator;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Executes the complete tender push workflow against GePNIC.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenderPushService {

    private final TenderValidationService validationService;
    private final TenderPayloadMapper payloadMapper;
    private final GePNICRequestService requestService;
    private final GePNICResponseService responseService;
    private final IntegrationOrchestrator orchestrator;
    private final GePNICProperties properties;

    public IntegrationResultForm pushTender(TenderPushForm form) {
        log.info("Initiating tender push for WAMIS ID: {}", form.getWamisTenderId());

        // 1. Validation
        validationService.validate(form);

        // 2. Map WAMIS form to canonical GePNIC JSON
        String mappedJson = payloadMapper.mapToGePNICJson(form);

        // 3. Start Transaction audit
        String path = properties.getEndpoint().getTenderPushPath();
        IntegrationTransaction txn = orchestrator.startTransaction(
                form.getWamisTenderId(),
                IntegrationOperation.TENDER_PUSH,
                form.getOfficeId(),
                path,
                mappedJson
        );

        try {
            // 4. Encrypt, sign, and send via HTTP
            ResponseEntity<String> response = requestService.executeEncryptedRequest(path, mappedJson);
            String responseBody = response.getBody();

            // 5. Decrypt, verify signature, and parse two-tier response
            GePNICJsonParser.ParsedResponse parsed = responseService.processResponse(responseBody);

            IntegrationStatus status = parsed.isSuccess() ? IntegrationStatus.SUCCESS : IntegrationStatus.REJECTED;
            return orchestrator.completeTransaction(
                    txn,
                    status,
                    parsed.outerCode(),
                    parsed.message(),
                    responseBody,
                    1
            );

        } catch (Exception e) {
            log.error("Tender push failed for {}: {}", form.getWamisTenderId(), e.getMessage());
            return orchestrator.completeTransaction(
                    txn,
                    IntegrationStatus.FAILED,
                    0,
                    e.getMessage(),
                    null,
                    0
            );
        }
    }
}
