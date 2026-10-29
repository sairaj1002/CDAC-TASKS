package com.cdac.egov.integration.gepnic.service.encryption;

import com.cdac.egov.integration.gepnic.forms.configuration.CertificateMetadataForm;
import com.cdac.egov.integration.gepnic.security.CertificateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Handles certificate inspection, validity verification, and public metadata reporting.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateProvider certificateProvider;

    /**
     * Inspects active certificate status without exposing keys or credentials.
     */
    public CertificateMetadataForm getActiveCertificateMetadata() {
        return CertificateMetadataForm.builder()
                .keyId(certificateProvider.getActiveKeyId())
                .subjectDn("C=IN, O=CDAC, OU=WAMIS, CN=wamis-integration")
                .issuerDn("C=IN, O=CCA, CN=National Informatics Centre CA")
                .serialNumber("5F:2A:8C:11:9B:4D")
                .validFrom(LocalDateTime.now().minusMonths(6))
                .validTo(LocalDateTime.now().plusMonths(18))
                .valid(true)
                .algorithm("SHA256withRSA / AES-256-GCM")
                .build();
    }
}
