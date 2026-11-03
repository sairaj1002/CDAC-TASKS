package in.gov.wamis.integration.gepnic;

import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.common.enums.PortalCode;
import in.gov.wamis.common.exception.IntegrationException;
import in.gov.wamis.integration.common.PortalRequest;
import in.gov.wamis.integration.common.PortalResponse;
import in.gov.wamis.integration.common.ProcurementPortal;
import in.gov.wamis.integration.gepnic.client.GePNICClient;
import in.gov.wamis.integration.gepnic.config.GePNICConstants;
import in.gov.wamis.integration.gepnic.config.GePNICProperties;
import in.gov.wamis.integration.gepnic.form.*;
import in.gov.wamis.integration.gepnic.mapper.GePNICToWamisMapper;
import in.gov.wamis.integration.gepnic.mapper.WamisToGePNICMapper;
import in.gov.wamis.integration.gepnic.response.GePNICResponseParser;
import in.gov.wamis.integration.gepnic.response.GePNICResponseParser.ParsedResponse;
import in.gov.wamis.integration.gepnic.security.GePNICEncryptionService;
import in.gov.wamis.procurement.pretender.PreTender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

/**
 * GePNIC Portal Adapter implementation.
 * Encapsulates all GePNIC-specific field names, security envelopes, URL paths, and response parsing.
 */
@Component
public class GePNICAdapter implements ProcurementPortal {

    private static final Logger log = LoggerFactory.getLogger(GePNICAdapter.class);

    private final GePNICProperties properties;
    private final GePNICClient client;
    private final WamisToGePNICMapper toGePNICMapper;
    private final GePNICToWamisMapper toWamisMapper;
    private final GePNICEncryptionService encryptionService;
    private final GePNICResponseParser responseParser;

    public GePNICAdapter(GePNICProperties properties,
                         GePNICClient client,
                         WamisToGePNICMapper toGePNICMapper,
                         GePNICToWamisMapper toWamisMapper,
                         GePNICEncryptionService encryptionService,
                         GePNICResponseParser responseParser) {
        this.properties = properties;
        this.client = client;
        this.toGePNICMapper = toGePNICMapper;
        this.toWamisMapper = toWamisMapper;
        this.encryptionService = encryptionService;
        this.responseParser = responseParser;
    }

    @Override
    public PortalCode getPortalCode() {
        return PortalCode.GEPNIC;
    }

    @Override
    public boolean isEnabled() {
        return properties.isEnabled();
    }

    @Override
    public boolean supports(IntegrationOperation operation) {
        return true;
    }

    @Override
    public PortalResponse pushTender(PortalRequest request) {
        String correlationId = request.getCorrelationId();
        log.info("Executing GePNIC tender push [correlationId={}, preTenderId={}]", correlationId, request.getPreTenderId());

        if (!(request.getPayload() instanceof PreTender preTender)) {
            throw new IllegalArgumentException("Payload for GePNIC pushTender must be an authorized PreTender instance");
        }

        try {
            // 1. Build canonical GePNIC JSON
            String plaintextJson = toGePNICMapper.mapToJson(preTender);

            // 2. Encrypt and sign into envelope
            GePNICEnvelopeForm envelope = encryptionService.buildEnvelope(plaintextJson);

            // 3. Dispatch to GePNIC endpoint
            String responseStr = client.postJson(GePNICConstants.TENDER_PUSH_PATH, envelope, correlationId);

            // 4. Parse outer & nested response
            ParsedResponse parsed = responseParser.parse(responseStr);

            // 5. Map to normalized portal response
            return toWamisMapper.mapToPortalResponse(parsed, correlationId, preTender.getTenderReferenceNo());
        } catch (IntegrationException ie) {
            log.error("Integration exception during GePNIC tender push: {}", ie.getMessage());
            return new PortalResponse() {{
                setStatus(ie.getStatus() != null ? ie.getStatus() : IntegrationStatus.FAILED);
                setMessage(ie.getMessage());
                setCorrelationId(correlationId);
            }};
        } catch (Exception e) {
            log.error("Unexpected failure pushing tender to GePNIC: {}", e.getMessage(), e);
            return PortalResponse.failed("Tender push failed: " + e.getMessage(), correlationId);
        }
    }

    @Override
    public PortalResponse uploadDocument(PortalRequest request, DocumentUploadForm uploadForm) {
        String correlationId = request.getCorrelationId();
        log.info("Executing GePNIC document upload [correlationId={}, fileNo={}, docId={}]",
                correlationId, uploadForm.getUniqueFileNo(), uploadForm.getDocuniqueid());

        try {
            MultiValueMap<String, Object> multipart = new LinkedMultiValueMap<>();
            multipart.add("uniqueUserId", properties.getUniqueUserId());
            multipart.add("uniqueFileNo", uploadForm.getUniqueFileNo());
            multipart.add("docuniqueid", String.valueOf(uploadForm.getDocuniqueid()));

            if (uploadForm.getFile() != null && !uploadForm.getFile().isEmpty()) {
                ByteArrayResource fileResource = new ByteArrayResource(uploadForm.getFile().getBytes()) {
                    @Override
                    public String getFilename() {
                        return uploadForm.getFile().getOriginalFilename();
                    }
                };
                multipart.add("file", fileResource);
            }

            String responseStr = client.postMultipart(GePNICConstants.DOCUMENT_UPLOAD_PATH, multipart, correlationId);
            ParsedResponse parsed = responseParser.parse(responseStr);
            return toWamisMapper.mapToPortalResponse(parsed, correlationId, uploadForm.getUniqueFileNo());
        } catch (IOException e) {
            return PortalResponse.failed("Failed to read file bytes: " + e.getMessage(), correlationId);
        } catch (IntegrationException ie) {
            return new PortalResponse() {{
                setStatus(ie.getStatus() != null ? ie.getStatus() : IntegrationStatus.FAILED);
                setMessage(ie.getMessage());
                setCorrelationId(correlationId);
            }};
        }
    }

    @Override
    public PortalResponse pullTender(PortalRequest request, TenderPullForm pullForm) {
        String correlationId = request.getCorrelationId();
        try {
            String responseStr = client.postJson(GePNICConstants.TENDER_PULL_BY_PUBLISH_DATE_PATH, pullForm, correlationId);
            ParsedResponse parsed = responseParser.parse(responseStr);
            return toWamisMapper.mapToPortalResponse(parsed, correlationId, null);
        } catch (IntegrationException ie) {
            return new PortalResponse() {{
                setStatus(ie.getStatus() != null ? ie.getStatus() : IntegrationStatus.FAILED);
                setMessage(ie.getMessage());
                setCorrelationId(correlationId);
            }};
        }
    }

    @Override
    public PortalResponse pullCorrigendum(PortalRequest request, CorrigendumPullForm pullForm) {
        String correlationId = request.getCorrelationId();
        try {
            String responseStr = client.postJson(GePNICConstants.CORRIGENDUM_PULL_BY_PUBLISH_DATE_PATH, pullForm, correlationId);
            ParsedResponse parsed = responseParser.parse(responseStr);
            return toWamisMapper.mapToPortalResponse(parsed, correlationId, null);
        } catch (IntegrationException ie) {
            return new PortalResponse() {{
                setStatus(ie.getStatus() != null ? ie.getStatus() : IntegrationStatus.FAILED);
                setMessage(ie.getMessage());
                setCorrelationId(correlationId);
            }};
        }
    }

    @Override
    public PortalResponse pullAoc(PortalRequest request, AocPullForm pullForm) {
        String correlationId = request.getCorrelationId();
        try {
            String responseStr = client.postJson(GePNICConstants.AOC_PULL_BY_CREATED_DATE_PATH, pullForm, correlationId);
            ParsedResponse parsed = responseParser.parse(responseStr);
            return toWamisMapper.mapToPortalResponse(parsed, correlationId, null);
        } catch (IntegrationException ie) {
            return new PortalResponse() {{
                setStatus(ie.getStatus() != null ? ie.getStatus() : IntegrationStatus.FAILED);
                setMessage(ie.getMessage());
                setCorrelationId(correlationId);
            }};
        }
    }

    @Override
    public PortalResponse downloadAocDocument(PortalRequest request, AocFileDownloadForm downloadForm) {
        String correlationId = request.getCorrelationId();
        try {
            String responseStr = client.postJson(GePNICConstants.AOC_FILE_DOWNLOAD_PATH, downloadForm, correlationId);
            ParsedResponse parsed = responseParser.parse(responseStr);
            return toWamisMapper.mapToPortalResponse(parsed, correlationId, downloadForm.getDocCode());
        } catch (IntegrationException ie) {
            return new PortalResponse() {{
                setStatus(ie.getStatus() != null ? ie.getStatus() : IntegrationStatus.FAILED);
                setMessage(ie.getMessage());
                setCorrelationId(correlationId);
            }};
        }
    }
}

