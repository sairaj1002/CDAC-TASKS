package com.cdac.egov.integration.gepnic.forms.tender;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Request form received from WAMIS to push a tender to GePNIC.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenderPushForm {

    @NotBlank(message = "wamisTenderId is required")
    private String wamisTenderId;

    @NotBlank(message = "officeId is required")
    private String officeId;

    @NotBlank(message = "tenderTitle is required")
    private String tenderTitle;

    @NotNull(message = "estimatedCost is required")
    @DecimalMin(value = "0.0", message = "estimatedCost must be non-negative")
    private BigDecimal estimatedCost;

    private BigDecimal tenderFee;
    private BigDecimal emdAmount;

    @NotNull(message = "publishDate is required")
    private LocalDateTime publishDate;

    @NotNull(message = "submissionEndDate is required")
    private LocalDateTime submissionEndDate;

    private String workDescription;

    @Builder.Default
    private List<TenderDocumentItemForm> documents = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenderDocumentItemForm {
        @NotBlank(message = "documentId is required")
        private String documentId;

        @NotBlank(message = "fileName is required")
        private String fileName;

        private String contentType;
        private Long fileSize;
        private String docCode;
    }
}
