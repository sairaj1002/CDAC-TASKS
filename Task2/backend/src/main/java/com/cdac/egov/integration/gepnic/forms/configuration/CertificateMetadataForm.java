package com.cdac.egov.integration.gepnic.forms.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Public, sanitized certificate status and validity metadata. Secrets are never exposed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateMetadataForm {

    private String keyId;
    private String subjectDn;
    private String issuerDn;
    private String serialNumber;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean valid;
    private String algorithm;
}
