package com.cdac.egov.integration.gepnic.service.integration;

import com.cdac.egov.integration.gepnic.config.GePNICProperties;
import com.cdac.egov.integration.gepnic.constants.GePNICConstants;
import com.cdac.egov.integration.gepnic.exception.GePNICApiException;
import com.cdac.egov.integration.gepnic.forms.encryption.EncryptedEnvelopeForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Manages outgoing HTTP/HTTPS transport to GePNIC endpoints.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GePNICHttpClient {

    private final RestTemplate gepnicRestTemplate;
    private final GePNICProperties properties;

    /**
     * Sends an encrypted envelope POST request to the specified path.
     */
    public ResponseEntity<String> postEnvelope(String path, EncryptedEnvelopeForm envelope) {
        String fullUrl = properties.getEndpoint().getBaseUrl() + path;
        String correlationId = UUID.randomUUID().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(GePNICConstants.HEADER_CORRELATION_ID, correlationId);
        headers.set(GePNICConstants.HEADER_REQUEST_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        HttpEntity<EncryptedEnvelopeForm> requestEntity = new HttpEntity<>(envelope, headers);
        log.info("Sending GePNIC envelope request to {} [correlationId={}]", fullUrl, correlationId);

        try {
            return gepnicRestTemplate.exchange(fullUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpStatusCodeException ex) {
            log.error("GePNIC HTTP error {} at {}: {}", ex.getStatusCode(), fullUrl, ex.getResponseBodyAsString());
            throw new GePNICApiException("GePNIC endpoint returned HTTP " + ex.getStatusCode(), ex.getStatusCode().value(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("Transport error connecting to GePNIC endpoint {}: {}", fullUrl, ex.getMessage());
            throw new GePNICApiException("Transport error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Uploads a tender file as a multipart request per NIC specification.
     */
    public ResponseEntity<String> uploadMultipartDocument(String uniqueUserId, String uniqueFileNo, String docUniqueId, String fileName, byte[] fileBytes) {
        String fullUrl = properties.getEndpoint().getBaseUrl() + properties.getEndpoint().getDocumentUploadPath();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("uniqueUserId", uniqueUserId);
        body.add("uniqueFileNo", uniqueFileNo);
        body.add("docuniqueid", docUniqueId);

        ByteArrayResource resource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        log.info("Posting multipart document upload to {} [file={}, docUniqueId={}]", fullUrl, fileName, docUniqueId);

        try {
            return gepnicRestTemplate.exchange(fullUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpStatusCodeException ex) {
            throw new GePNICApiException("Document upload HTTP error " + ex.getStatusCode(), ex.getStatusCode().value(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new GePNICApiException("Document upload transport error: " + ex.getMessage(), ex);
        }
    }
}
