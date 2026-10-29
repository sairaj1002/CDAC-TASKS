package com.cdac.egov.integration.gepnic.forms.document;

import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Result returned after document upload or status query.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseForm {

    private String uniqueFileNo;
    private String docUniqueId;
    private String tenderReference;
    private String fileName;
    private Long fileSizeBytes;
    private IntegrationStatus status;
    private Integer responseCode;
    private String responseMessage;
    private LocalDateTime timestamp;
}
