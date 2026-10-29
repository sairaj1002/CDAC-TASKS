package com.cdac.egov.integration.gepnic.forms.tender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Normalized Tender Response returned to WAMIS consumers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenderResponseForm {

    private String sourceSystem;
    private String wamisTenderId;
    private String externalTenderReference;
    private String status;
    private String title;
    private BigDecimal estimatedCost;
    private LocalDateTime publishDate;
    private LocalDateTime bidSubmissionEndDate;
    private LocalDateTime lastSynchronizedAt;
    private int corrigendumCount;

    @Builder.Default
    private List<CorrigendumItemForm> corrigendums = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorrigendumItemForm {
        private String externalReference;
        private LocalDateTime publishedAt;
        private String summary;
    }
}
