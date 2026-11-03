package in.gov.wamis.integration.gepnic.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.wamis.common.exception.IntegrationException;
import in.gov.wamis.integration.gepnic.config.GePNICConstants;
import in.gov.wamis.integration.gepnic.form.GePNICResponseForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Parses GePNIC outer response envelopes and safely decodes nested escaped JSON strings.
 * Handles malformed responses, unexpected HTTP shapes, and null checks safely.
 */
@Component
public class GePNICResponseParser {

    private static final Logger log = LoggerFactory.getLogger(GePNICResponseParser.class);

    private final ObjectMapper objectMapper;

    public GePNICResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ParsedResponse parse(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            throw new IntegrationException("Empty response received from GePNIC portal", null, null, "EMPTY_RESPONSE");
        }

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            Integer outerCode = rootNode.has("responseCode") && !rootNode.get("responseCode").isNull()
                    ? rootNode.get("responseCode").asInt() : null;
            String outerMessage = rootNode.has("responseMessage") && !rootNode.get("responseMessage").isNull()
                    ? rootNode.get("responseMessage").asText() : "";

            Integer innerCode = null;
            String innerMessage = outerMessage;
            JsonNode innerNode = null;

            // Attempt to parse nested JSON inside responseMessage if present
            if (outerMessage != null && outerMessage.trim().startsWith("{")) {
                try {
                    innerNode = objectMapper.readTree(outerMessage);
                    if (innerNode.has("responseCode") && !innerNode.get("responseCode").isNull()) {
                        innerCode = innerNode.get("responseCode").asInt();
                    }
                    if (innerNode.has("responseMessage") && !innerNode.get("responseMessage").isNull()) {
                        innerMessage = innerNode.get("responseMessage").asText();
                    }
                } catch (Exception e) {
                    log.warn("Nested responseMessage is not valid JSON string; using outer message string: {}", e.getMessage());
                }
            }

            boolean isSuccess = Integer.valueOf(GePNICConstants.RESP_CODE_SUCCESS).equals(outerCode)
                    && (innerCode == null || innerCode == GePNICConstants.INNER_CODE_INSERTED_SUCCESSFULLY
                    || innerCode == GePNICConstants.INNER_CODE_DOC_UPLOADED_SUCCESSFULLY
                    || innerCode == GePNICConstants.INNER_CODE_TENDERS_FETCHED);

            return new ParsedResponse(outerCode, outerMessage, innerCode, innerMessage, isSuccess, innerNode);
        } catch (Exception e) {
            log.error("Failed to parse GePNIC response body: {}", e.getMessage());
            throw new IntegrationException("Malformed response JSON received from GePNIC: " + e.getMessage(), e, null, "RESPONSE_PARSE_FAILED");
        }
    }

    public static class ParsedResponse {
        private final Integer outerCode;
        private final String outerMessage;
        private final Integer innerCode;
        private final String innerMessage;
        private final boolean success;
        private final JsonNode dataNode;

        public ParsedResponse(Integer outerCode, String outerMessage, Integer innerCode,
                              String innerMessage, boolean success, JsonNode dataNode) {
            this.outerCode = outerCode;
            this.outerMessage = outerMessage;
            this.innerCode = innerCode;
            this.innerMessage = innerMessage;
            this.success = success;
            this.dataNode = dataNode;
        }

        public Integer getOuterCode() {
            return outerCode;
        }

        public String getOuterMessage() {
            return outerMessage;
        }

        public Integer getInnerCode() {
            return innerCode;
        }

        public String getInnerMessage() {
            return innerMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public JsonNode getDataNode() {
            return dataNode;
        }
    }
}

