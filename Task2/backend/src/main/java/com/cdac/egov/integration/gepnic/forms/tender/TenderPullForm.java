package com.cdac.egov.integration.gepnic.forms.tender;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request form to query/pull tenders from GePNIC by publish date or tender reference.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenderPullForm {

    /**
     * Date formatted as yyyyMMdd per NIC GePNIC specification, e.g. 20261101
     */
    @NotBlank(message = "publishedDate must not be blank (format: yyyyMMdd)")
    private String publishedDate;

    private String tenderReference;
    private String officeCode;
}
