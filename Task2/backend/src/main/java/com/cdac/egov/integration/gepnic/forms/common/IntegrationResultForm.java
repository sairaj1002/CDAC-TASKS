package com.cdac.egov.integration.gepnic.forms.common;

import com.cdac.egov.integration.gepnic.enums.IntegrationOperation;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard integration result data carrier returned across tender, document, and AOC operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationResultForm {

    private String transactionId;
    private IntegrationOperation operation;
    private IntegrationStatus status;
    private String sourceSystem;
    private String correlationId;
    private LocalDateTime timestamp;
    private Integer recordsProcessed;
    private Integer responseCode;
    private String message;
}
