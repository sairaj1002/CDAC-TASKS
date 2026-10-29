package com.cdac.egov.integration.gepnic.service.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Handles two-tier parsing of GePNIC external responses where responseMessage can be a nested JSON string.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GePNICJsonParser {

    private final ObjectMapper objectMapper;

    /**
     * Parses the outer response and inspects if responseMessage is a nested JSON string.
     */
    public ParsedResponse parseResponse(String rawDecryptedJson) {
        try {
            JsonNode root = objectMapper.readTree(rawDecryptedJson);
            int outerCode = root.has("responseCode") ? root.get("responseCode").asInt() : 0;
            String message = "";
            int innerCode = outerCode;

            if (root.has("responseMessage")) {
                JsonNode messageNode = root.get("responseMessage");
                if (messageNode.isTextual()) {
                    String rawMessageText = messageNode.asText();
                    // Attempt nested JSON parsing
                    try {
                        JsonNode inner = objectMapper.readTree(rawMessageText);
                        if (inner.has("responseCode")) {
                            innerCode = inner.get("responseCode").asInt();
                        }
                        if (inner.has("responseMessage")) {
                            message = inner.get("responseMessage").asText();
                        } else {
                            message = rawMessageText;
                        }
                    } catch (Exception ex) {
                        message = rawMessageText;
                    }
                } else if (messageNode.isObject()) {
                    if (messageNode.has("responseCode")) {
                        innerCode = messageNode.get("responseCode").asInt();
                    }
                    if (messageNode.has("message")) {
                        message = messageNode.get("message").asText();
                    } else if (messageNode.has("responseMessage")) {
                        message = messageNode.get("responseMessage").asText();
                    } else {
                        message = messageNode.toString();
                    }
                }
            }

            return new ParsedResponse(outerCode, innerCode, message, root);
        } catch (Exception e) {
            log.error("Failed to parse GePNIC response JSON: {}", e.getMessage());
            return new ParsedResponse(0, 0, "Failed to parse response: " + e.getMessage(), null);
        }
    }

    public record ParsedResponse(int outerCode, int innerCode, String message, JsonNode rawJsonNode) {
        public boolean isSuccess() {
            return outerCode == 1 && (innerCode == 1 || innerCode == 101 || innerCode == 105);
        }
    }
}
