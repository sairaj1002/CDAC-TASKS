package com.cdac.egov.integration.gepnic.controller;

import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.enums.IntegrationOperation;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import com.cdac.egov.integration.gepnic.repository.IntegrationTransactionRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller providing integration summary, metrics, and KPI cards for Screen 1 (Dashboard).
 */
@RestController
@RequestMapping(GePNICConstants.API_DASHBOARD)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final IntegrationTransactionRepository transactionRepository;

    @GetMapping
    public ResponseEntity<ApiResponseForm<DashboardSummaryResponse>> getDashboardSummary() {
        long totalCount = transactionRepository.count();
        long successCount = transactionRepository.countByStatus(IntegrationStatus.SUCCESS);
        long failedCount = transactionRepository.countByStatus(IntegrationStatus.FAILED)
                + transactionRepository.countByStatus(IntegrationStatus.REJECTED);
        long pendingCount = transactionRepository.countByStatus(IntegrationStatus.PENDING)
                + transactionRepository.countByStatus(IntegrationStatus.IN_PROGRESS);

        var recentTransactions = transactionRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        DashboardSummaryResponse response = DashboardSummaryResponse.builder()
                .totalTransactions(totalCount)
                .successCount(successCount)
                .failedCount(failedCount)
                .pendingCount(pendingCount)
                .successRatePercent(totalCount > 0 ? (double) Math.round((double) successCount * 1000.0 / totalCount) / 10.0 : 100.0)
                .recentTransactions(recentTransactions)
                .operationBreakdown(Map.of(
                        "Tender Push", transactionRepository.countByOperation(IntegrationOperation.TENDER_PUSH),
                        "Tender Pull", transactionRepository.countByOperation(IntegrationOperation.TENDER_PULL),
                        "Document Upload", transactionRepository.countByOperation(IntegrationOperation.DOCUMENT_UPLOAD),
                        "AOC Management", transactionRepository.countByOperation(IntegrationOperation.AOC_PULL)
                ))
                .build();

        return ResponseEntity.ok(ApiResponseForm.ok(response, "Dashboard summary loaded successfully"));
    }

    @Data
    @Builder
    public static class DashboardSummaryResponse {
        private long totalTransactions;
        private long successCount;
        private long failedCount;
        private long pendingCount;
        private double successRatePercent;
        private Object recentTransactions;
        private Map<String, Long> operationBreakdown;
    }
}
