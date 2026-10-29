package com.cdac.egov.integration.gepnic.forms.tender;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request form to query/pull corrigendums from GePNIC by publish date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrigendumPullForm {

    @NotBlank(message = "publishedDate must not be blank (format: yyyyMMdd)")
    private String publishedDate;

    private String tenderReference;
}
