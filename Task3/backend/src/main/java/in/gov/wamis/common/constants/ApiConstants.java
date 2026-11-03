package in.gov.wamis.common.constants;

/**
 * Internal WAMIS route paths and segments.
 * Strictly internal routes; never mix with external portal endpoint URLs.
 */
public final class ApiConstants {

    private ApiConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String API_PREFIX = "/api";

    public static final String PRE_TENDERS = API_PREFIX + "/pre-tenders";
    public static final String INTEGRATION_JOBS = API_PREFIX + "/integration-jobs";
    public static final String GEPNIC_BASE = API_PREFIX + "/gepnic";

    public static final String GEPNIC_TENDERS_PUSH = GEPNIC_BASE + "/tenders/push";
    public static final String GEPNIC_DOCUMENTS_UPLOAD = GEPNIC_BASE + "/documents/upload";
    public static final String GEPNIC_TENDERS_PULL = GEPNIC_BASE + "/tenders/pull";
    public static final String GEPNIC_CORRIGENDUMS_PULL = GEPNIC_BASE + "/corrigendums/pull";
    public static final String GEPNIC_AOC_PULL = GEPNIC_BASE + "/aoc/pull";
    public static final String GEPNIC_AOC_DOCUMENTS_DOWNLOAD = GEPNIC_BASE + "/aoc/documents/download";
    public static final String GEPNIC_TRANSACTIONS = GEPNIC_BASE + "/transactions";
    public static final String GEPNIC_CONFIG = GEPNIC_BASE + "/configuration";
}

