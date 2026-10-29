package com.cdac.egov.integration.gepnic.forms.metadata;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form to request tender/document synchronization metadata status check.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataCheckForm {

    @NotBlank(message = "tenderReference is required")
    private String tenderReference;

    private String uniqueFileNo;
}
