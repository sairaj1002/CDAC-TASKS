package com.cdac.egov.integration.gepnic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Type-safe configuration properties for GePNIC integration.
 * Loaded from application.yml under prefix "gepnic".
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "gepnic")
public class GePNICProperties {

    /**
     * Target environment: dev, test, stage, prod
     */
    private String environment = "dev";

    /**
     * Whether development diagnostics endpoints are enabled.
     * MUST be false in production.
     */
    private boolean diagnosticsEnabled = true;

    /**
     * NIC endpoint and credentials configuration
     */
    private Endpoint endpoint = new Endpoint();

    /**
     * Security and Certificate references
     */
    private Security security = new Security();

    /**
     * HTTP client connection timeouts and retries
     */
    private Http http = new Http();

    @Getter
    @Setter
    public static class Endpoint {
        private String baseUrl = "https://demoeproc.nic.in";
        private String tenderPushPath = "/nicgep_external_webservice/push/postTenderJsonDetailsService";
        private String documentUploadPath = "/nicgep_external_webservice/push/postTenderFileUploadDetailsService";
        private String tenderPullPath = "/nicgep_external_webservice/pull/getTenderInfoFromPreTenderbyPublishDateService";
        private String corrigendumPullPath = "/nicgep_external_webservice/pull/getPreTenderCorrigendumInfosByPublishedDateService";
        private String aocPullPath = "/nicgep_external_webservice/pull/getAocInfoFromPreTenderbyCreatedDateService";
        private String aocDownloadPath = "/nicgep_external_webservice/pull/postAOCFileDownloadDetailsService";
        private String metadataCheckPath = "/nicgep_external_webservice/check/getMetadataStatusService";
    }

    @Getter
    @Setter
    public static class Security {
        private String uniqueUserId = "WAMIS-REGISTERED-USER-ID";
        private String keyId = "GEPNIC-CERTIFICATE-THUMBPRINT-001";
        private String keystorePath = "classpath:certs/wamis-keystore.p12";
        private String keystoreType = "PKCS12";
        private String keyAlias = "wamis-client";
        private String gepnicCertPath = "classpath:certs/gepnic-public.crt";
    }

    @Getter
    @Setter
    public static class Http {
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration readTimeout = Duration.ofSeconds(30);
        private int maxRetries = 3;
        private Duration retryBackoff = Duration.ofMillis(1000);
    }
}
