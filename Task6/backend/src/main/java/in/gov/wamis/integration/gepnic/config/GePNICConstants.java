package in.gov.wamis.integration.gepnic.config;

/**
 * Stable constants and verified endpoint paths for GePNIC services.
 * Extracted from the NIC-GePNIC PreTender Integration Service Document.
 */
public final class GePNICConstants {

    private GePNICConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String PORTAL_CODE = "GEPNIC";
    public static final String OPERATION_PRE_TENDER = "PRE_TENDER";

    // Verified service relative paths
    public static final String TENDER_PUSH_PATH =
            "/nicgep_external_webservice/push/postTenderJsonDetailsService";
    public static final String DOCUMENT_UPLOAD_PATH =
            "/nicgep_external_webservice/push/postTenderFileUploadDetailsService";
    public static final String TENDER_PULL_BY_PUBLISH_DATE_PATH =
            "/nicgep_external_webservice/pull/getTenderInfoFromPreTenderbyPublishDateService";
    public static final String CORRIGENDUM_PULL_BY_PUBLISH_DATE_PATH =
            "/nicgep_external_webservice/pull/getPreTenderCorrigendumInfosByPublishedDateService";
    public static final String AOC_PULL_BY_CREATED_DATE_PATH =
            "/nicgep_external_webservice/pull/getAocInfoFromPreTenderbyCreatedDateService";
    public static final String AOC_FILE_DOWNLOAD_PATH =
            "/nicgep_external_webservice/pull/postAOCFileDownloadDetailsService";

    // Known NIC response codes
    public static final int RESP_CODE_SUCCESS = 1;
    public static final int INNER_CODE_INSERTED_SUCCESSFULLY = 101;
    public static final int INNER_CODE_DOC_UPLOADED_SUCCESSFULLY = 105;
    public static final int INNER_CODE_TENDERS_FETCHED = 104;
}

