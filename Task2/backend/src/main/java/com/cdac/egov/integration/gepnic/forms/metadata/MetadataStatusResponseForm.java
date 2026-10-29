package com.cdac.egov.integration.gepnic.forms.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Metadata synchronization check status response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataStatusResponseForm {

    private String tenderReference;
    private String uniqueFileNo;
    private boolean metadataSynced;
    private int documentCountUploaded;
    private int documentCountExpected;
    private String syncStatus;
    private LocalDateTime lastVerifiedAt;
    private String message;
}
