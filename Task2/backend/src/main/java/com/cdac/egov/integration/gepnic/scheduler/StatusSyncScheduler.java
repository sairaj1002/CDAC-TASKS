package com.cdac.egov.integration.gepnic.scheduler;

import com.cdac.egov.integration.gepnic.entity.IntegrationTransaction;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import com.cdac.egov.integration.gepnic.repository.IntegrationTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Periodically polls and reconciles transactions stuck in IN_PROGRESS or TIMEOUT status.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatusSyncScheduler {

    private final IntegrationTransactionRepository transactionRepository;

    /**
     * Executes reconciliation every 5 minutes (300,000 ms).
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 60000)
    public void reconcilePendingTransactions() {
        log.debug("Running GePNIC transaction status synchronization check...");
        Page<IntegrationTransaction> pendingTxns = transactionRepository.findByStatus(IntegrationStatus.IN_PROGRESS, PageRequest.of(0, 50));

        for (IntegrationTransaction txn : pendingTxns) {
            if (txn.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(10))) {
                log.warn("Marking stalled transaction {} as TIMEOUT", txn.getTransactionId());
                txn.setStatus(IntegrationStatus.TIMEOUT);
                txn.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(txn);
            }
        }
    }
}
