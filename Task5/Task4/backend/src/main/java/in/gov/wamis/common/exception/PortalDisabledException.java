package in.gov.wamis.common.exception;

public class PortalDisabledException extends RuntimeException {

    private final String portalCode;

    public PortalDisabledException(String portalCode) {
        super("Procurement portal '" + portalCode + "' is disabled or not configured in this environment");
        this.portalCode = portalCode;
    }

    public String getPortalCode() {
        return portalCode;
    }
}

