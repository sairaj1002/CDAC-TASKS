package com.cdac.egov.integration.gepnic.service.integration;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.entity.IntegrationTransaction;
import com.cdac.egov.integration.gepnic.enums.IntegrationOperation;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import com.cdac.egov.integration.gepnic.forms.common.IntegrationResultForm;
import com.cdac.egov.integration.gepnic.repository.IntegrationTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Orchestrates the end-to-end integration lifecycle and transaction auditing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationOrchestrator {

    private final IntegrationTransactionRepository transactionRepository;

    /**
     * Initializes a new integration transaction record in PENDING state.
     */
    @Transactional
    public IntegrationTransaction startTransaction(String correlationId, IntegrationOperation operation, String officeCode, String endpointUrl, String sanitizedPayload) {
        String transactionId = "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        IntegrationTransaction txn = IntegrationTransaction.builder()
                .transactionId(transactionId)
                .correlationId(correlationId)
                .operation(operation)
                .status(IntegrationStatus.IN_PROGRESS)
                .sourceSystem(GePNICConstants.SOURCE_SYSTEM_WAMIS)
                .targetSystem(GePNICConstants.TARGET_SYSTEM_GEPNIC)
                .officeCode(officeCode)
                .endpointUrl(endpointUrl)
                .requestPayloadSanitized(sanitizedPayload)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return transactionRepository.save(txn);
    }

    /**
     * Completes a transaction with success or failure details.
     */
    @Transactional
    public IntegrationResultForm completeTransaction(
            IntegrationTransaction txn,
            IntegrationStatus finalStatus,
            int responseCode,
            String responseMessage,
            String sanitizedResponsePayload,
            int recordsProcessed) {

        txn.setStatus(finalStatus);
        txn.setResponseCode(responseCode);
        txn.setResponseMessage(responseMessage);
        txn.setResponsePayloadSanitized(sanitizedResponsePayload);
        txn.setRecordsProcessed(recordsProcessed);
        txn.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(txn);

        return IntegrationResultForm.builder()
                .transactionId(txn.getTransactionId())
                .operation(txn.getOperation())
                .status(finalStatus)
                .sourceSystem(txn.getSourceSystem())
                .correlationId(txn.getCorrelationId())
                .timestamp(LocalDateTime.now())
                .recordsProcessed(recordsProcessed)
                .responseCode(responseCode)
                .message(responseMessage)
                .build();
    }
}
