package com.cdac.egov.integration.gepnic.forms.aoc;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request form to download an AOC document per NIC specification mapping.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AocDownloadForm {

    @NotBlank(message = "docType is required")
    private String docType;

    @NotBlank(message = "bId is required")
    private String bId;

    @NotBlank(message = "docCode is required")
    private String docCode;

    @NotBlank(message = "fileName is required")
    private String fileName;
}
