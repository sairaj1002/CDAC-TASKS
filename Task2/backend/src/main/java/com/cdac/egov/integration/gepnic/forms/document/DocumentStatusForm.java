package com.cdac.egov.integration.gepnic.forms.document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form for checking the upload status of a document submitted to GePNIC.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatusForm {

    @NotBlank(message = "uniqueFileNo must not be blank")
    private String uniqueFileNo;

    private String docUniqueId;
    private String tenderReference;
}
