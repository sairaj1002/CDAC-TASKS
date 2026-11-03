package in.gov.wamis.integration.gepnic.client;

import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.common.exception.IntegrationException;
import in.gov.wamis.integration.gepnic.config.GePNICProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;

/**
 * Low-level HTTP transport client for GePNIC external endpoints.
 * Handles HTTP requests, timeouts, and translates network interruptions to OUTCOME_UNKNOWN.
 * Contains no WAMIS procurement domain logic.
 */
@Component
public class GePNICClient {

    private static final Logger log = LoggerFactory.getLogger(GePNICClient.class);

    private final RestClient restClient;
    private final GePNICProperties properties;

    public GePNICClient(RestClient gepnicRestClient, GePNICProperties properties) {
        this.restClient = gepnicRestClient;
        this.properties = properties;
    }

    public String postJson(String relativePath, Object body, String correlationId) {
        log.info("Dispatching HTTP POST to GePNIC [path={}, correlationId={}]", relativePath, correlationId);
        try {
            return restClient.post()
                    .uri(relativePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        log.warn("GePNIC returned HTTP error status: {} for correlationId: {}", resp.getStatusCode(), correlationId);
                    })
                    .body(String.class);
        } catch (ResourceAccessException ex) {
            // Check if socket/read timeout occurred after request transmission
            if (isTimeout(ex)) {
                log.error("Timeout occurred while awaiting response from GePNIC [path={}, correlationId={}]: {}",
                        relativePath, correlationId, ex.getMessage());
                throw new IntegrationException(
                        "Timeout communicating with GePNIC portal. Request outcome is uncertain and must be reconciled before retrying.",
                        ex,
                        IntegrationStatus.OUTCOME_UNKNOWN,
                        "REMOTE_TIMEOUT"
                );
            }
            log.error("Network connection failure communicating with GePNIC: {}", ex.getMessage());
            throw new IntegrationException("Network error communicating with GePNIC: " + ex.getMessage(),
                    ex, IntegrationStatus.FAILED, "NETWORK_ERROR");
        } catch (RestClientResponseException ex) {
            log.error("GePNIC HTTP error response [status={}]: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return ex.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("Unexpected error during HTTP call to GePNIC: {}", ex.getMessage());
            throw new IntegrationException("Unexpected transport failure: " + ex.getMessage(),
                    ex, IntegrationStatus.FAILED, "TRANSPORT_ERROR");
        }
    }

    public String postMultipart(String relativePath, MultiValueMap<String, Object> multipartData, String correlationId) {
        log.info("Dispatching multipart upload to GePNIC [path={}, correlationId={}]", relativePath, correlationId);
        try {
            return restClient.post()
                    .uri(relativePath)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                    .body(multipartData)
                    .retrieve()
                    .body(String.class);
        } catch (ResourceAccessException ex) {
            if (isTimeout(ex)) {
                log.error("Timeout during multipart document upload to GePNIC [correlationId={}]: {}", correlationId, ex.getMessage());
                throw new IntegrationException(
                        "Timeout uploading document to GePNIC. Outcome uncertain; must reconcile.",
                        ex,
                        IntegrationStatus.OUTCOME_UNKNOWN,
                        "UPLOAD_TIMEOUT"
                );
            }
            throw new IntegrationException("Network error during document upload: " + ex.getMessage(),
                    ex, IntegrationStatus.FAILED, "NETWORK_ERROR");
        } catch (RestClientResponseException ex) {
            log.error("GePNIC upload error response [status={}]: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return ex.getResponseBodyAsString();
        } catch (Exception ex) {
            throw new IntegrationException("Unexpected upload transport failure: " + ex.getMessage(),
                    ex, IntegrationStatus.FAILED, "TRANSPORT_ERROR");
        }
    }

    private boolean isTimeout(ResourceAccessException ex) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof SocketTimeoutException || cause.getMessage() != null && cause.getMessage().toLowerCase().contains("timeout")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}

