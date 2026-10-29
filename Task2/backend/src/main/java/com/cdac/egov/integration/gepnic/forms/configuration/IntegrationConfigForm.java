package com.cdac.egov.integration.gepnic.forms.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form for viewing and updating authorized environment configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationConfigForm {

    @NotBlank(message = "environmentName is required")
    private String environmentName;

    @NotBlank(message = "baseUrl is required")
    private String baseUrl;

    @NotBlank(message = "uniqueUserId is required")
    private String uniqueUserId;

    @NotBlank(message = "keyId is required")
    private String keyId;

    @Builder.Default
    private boolean active = true;

    private Integer connectTimeoutMs;
    private Integer readTimeoutMs;
}
