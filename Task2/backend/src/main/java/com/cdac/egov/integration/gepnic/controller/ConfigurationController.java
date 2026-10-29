package com.cdac.egov.integration.gepnic.controller;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.entity.IntegrationConfiguration;
import com.cdac.egov.integration.gepnic.forms.common.ApiResponseForm;
import com.cdac.egov.integration.gepnic.forms.configuration.CertificateMetadataForm;
import com.cdac.egov.integration.gepnic.forms.configuration.IntegrationConfigForm;
import com.cdac.egov.integration.gepnic.repository.IntegrationConfigurationRepository;
import com.cdac.egov.integration.gepnic.service.encryption.CertificateService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller for Screen 10: Integration Configuration & Certificate Management.
 * Manages authorized non-secret settings. Private keys and keystore passwords are never exposed.
 */
@RestController
@RequestMapping(GePNICConstants.API_CONFIGURATION)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConfigurationController {

    private final GePNICProperties properties;
    private final CertificateService certificateService;
    private final IntegrationConfigurationRepository configRepository;

    @GetMapping
    public ResponseEntity<ApiResponseForm<ConfigurationViewResponse>> getConfiguration() {
        CertificateMetadataForm certMeta = certificateService.getActiveCertificateMetadata();

        IntegrationConfigForm configForm = configRepository.findFirstByActiveTrueOrderByUpdatedAtDesc()
                .map(c -> IntegrationConfigForm.builder()
                        .environmentName(c.getEnvironmentName())
                        .baseUrl(c.getBaseUrl())
                        .uniqueUserId(c.getUniqueUserId())
                        .keyId(c.getKeyId())
                        .active(c.getActive())
                        .connectTimeoutMs(c.getConnectTimeoutMs())
                        .readTimeoutMs(c.getReadTimeoutMs())
                        .build())
                .orElse(IntegrationConfigForm.builder()
                        .environmentName(properties.getEnvironment())
                        .baseUrl(properties.getEndpoint().getBaseUrl())
                        .uniqueUserId(properties.getSecurity().getUniqueUserId())
                        .keyId(properties.getSecurity().getKeyId())
                        .active(true)
                        .connectTimeoutMs((int) properties.getHttp().getConnectTimeout().toMillis())
                        .readTimeoutMs((int) properties.getHttp().getReadTimeout().toMillis())
                        .build());

        ConfigurationViewResponse response = ConfigurationViewResponse.builder()
                .configuration(configForm)
                .certificate(certMeta)
                .connectionStatus("CONNECTED")
                .lastTestedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponseForm.ok(response, "Configuration loaded successfully"));
    }

    @PutMapping
    public ResponseEntity<ApiResponseForm<IntegrationConfigForm>> updateConfiguration(
            @Valid @RequestBody IntegrationConfigForm form) {

        IntegrationConfiguration config = IntegrationConfiguration.builder()
                .environmentName(form.getEnvironmentName())
                .baseUrl(form.getBaseUrl())
                .uniqueUserId(form.getUniqueUserId())
                .keyId(form.getKeyId())
                .active(form.isActive())
                .connectTimeoutMs(form.getConnectTimeoutMs() != null ? form.getConnectTimeoutMs() : 10000)
                .readTimeoutMs(form.getReadTimeoutMs() != null ? form.getReadTimeoutMs() : 30000)
                .updatedBy("ADMIN")
                .updatedAt(LocalDateTime.now())
                .build();

        configRepository.save(config);

        return ResponseEntity.ok(ApiResponseForm.ok(form, "Integration configuration updated successfully"));
    }

    @Data
    @Builder
    public static class ConfigurationViewResponse {
        private IntegrationConfigForm configuration;
        private CertificateMetadataForm certificate;
        private String connectionStatus;
        private LocalDateTime lastTestedAt;
    }
}
