package com.cdac.egov.integration.gepnic.forms.aoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Normalized Award of Contract (AOC) Response Form.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AocResponseForm {

    private String tenderReference;
    private String contractNumber;
    private String awardedBidderName;
    private BigDecimal awardedValue;
    private LocalDateTime awardDate;
    private String status;

    @Builder.Default
    private List<AocDocumentItemForm> documents = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AocDocumentItemForm {
        private String bId;
        private String docType;
        private String docCode;
        private String fileName;
        private Long fileSize;
    }
}
