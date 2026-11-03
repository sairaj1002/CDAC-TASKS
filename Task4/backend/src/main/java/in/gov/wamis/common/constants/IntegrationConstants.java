package in.gov.wamis.common.constants;

/**
 * Stable internal portal codes, default retry limits, and operation names.
 */
public final class IntegrationConstants {

    private IntegrationConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String PORTAL_CODE_GEPNIC = "GEPNIC";
    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final String OP_PRE_TENDER = "PRE_TENDER";
    public static final String OP_DOCUMENT_UPLOAD = "DOCUMENT_UPLOAD";
    public static final String OP_TENDER_PULL = "TENDER_PULL";
    public static final String OP_CORRIGENDUM_PULL = "CORRIGENDUM_PULL";
    public static final String OP_AOC_PULL = "AOC_PULL";
    public static final String OP_AOC_FILE_DOWNLOAD = "AOC_FILE_DOWNLOAD";
}

