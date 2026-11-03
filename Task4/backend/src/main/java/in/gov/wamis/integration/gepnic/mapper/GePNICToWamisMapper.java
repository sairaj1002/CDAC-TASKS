package in.gov.wamis.integration.gepnic.mapper;

import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.integration.common.PortalResponse;
import in.gov.wamis.integration.gepnic.response.GePNICResponseParser.ParsedResponse;
import org.springframework.stereotype.Component;

/**
 * Maps GePNIC parsed responses to normalized WAMIS PortalResponse models.
 * Avoids silently mutating or overwriting authoritative records.
 */
@Component
public class GePNICToWamisMapper {

    public PortalResponse mapToPortalResponse(ParsedResponse parsed, String correlationId, String externalReference) {
        if (parsed == null) {
            return PortalResponse.failed("Null response parsed from GePNIC", correlationId);
        }

        if (parsed.isSuccess()) {
            return PortalResponse.accepted(
                    externalReference,
                    parsed.getInnerCode() != null ? parsed.getInnerCode() : parsed.getOuterCode(),
                    parsed.getInnerMessage() != null ? parsed.getInnerMessage() : parsed.getOuterMessage(),
                    correlationId
            );
        } else {
            return PortalResponse.rejected(
                    parsed.getInnerCode() != null ? parsed.getInnerCode() : parsed.getOuterCode(),
                    parsed.getInnerMessage() != null ? parsed.getInnerMessage() : parsed.getOuterMessage(),
                    correlationId
            );
        }
    }
}

