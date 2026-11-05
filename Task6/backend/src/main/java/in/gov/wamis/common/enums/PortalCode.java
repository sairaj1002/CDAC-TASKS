package in.gov.wamis.common.enums;

/**
 * Supported e-procurement portal identifiers.
 */
public enum PortalCode {
    GEPNIC;

    public static PortalCode fromString(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Portal code must not be null or blank");
        }
        for (PortalCode portal : values()) {
            if (portal.name().equalsIgnoreCase(code.trim())) {
                return portal;
            }
        }
        throw new IllegalArgumentException("Unsupported portal code: " + code);
    }
}

