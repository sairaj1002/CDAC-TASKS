package com.cdac.egov.integration.gepnic.forms.document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form carrying metadata for uploading a tender document to GePNIC.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadForm {

    @NotBlank(message = "uniqueFileNo must not be blank")
    private String uniqueFileNo;

    @NotBlank(message = "docUniqueId must not be blank")
    private String docUniqueId;

    @NotBlank(message = "tenderReference must not be blank")
    private String tenderReference;

    private String docCode;
    private String docType;
    private String fileName;
}
