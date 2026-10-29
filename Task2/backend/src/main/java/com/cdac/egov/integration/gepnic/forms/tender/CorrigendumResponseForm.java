package com.cdac.egov.integration.gepnic.forms.tender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Normalized Corrigendum Response Form.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrigendumResponseForm {

    private String corrigendumReference;
    private String tenderReference;
    private LocalDateTime publishedDate;
    private String description;
    private String type;
    private String status;
}
