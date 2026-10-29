package com.cdac.egov.integration.gepnic.constants;

/**
 * Centralized constants for the GePNIC Integration Module.
 * Environment-dependent endpoints and credentials are externalized into application.yml.
 */
public final class GePNICConstants {

    private GePNICConstants() {
        // Prevent instantiation
    }

    // System Identifiers
    public static final String SOURCE_SYSTEM_WAMIS = "WAMIS";
    public static final String TARGET_SYSTEM_GEPNIC = "GePNIC";

    // Header & Correlation Constants
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String HEADER_REQUEST_TIMESTAMP = "X-Request-Timestamp";

    // NIC Demo Endpoints (Default fallbacks, overridden via properties)
    public static final String DEFAULT_PUSH_TENDER_PATH = "/nicgep_external_webservice/push/postTenderJsonDetailsService";
    public static final String DEFAULT_UPLOAD_DOC_PATH = "/nicgep_external_webservice/push/postTenderFileUploadDetailsService";
    public static final String DEFAULT_PULL_TENDER_PATH = "/nicgep_external_webservice/pull/getTenderInfoFromPreTenderbyPublishDateService";
    public static final String DEFAULT_PULL_CORRIGENDUM_PATH = "/nicgep_external_webservice/pull/getPreTenderCorrigendumInfosByPublishedDateService";
    public static final String DEFAULT_PULL_AOC_PATH = "/nicgep_external_webservice/pull/getAocInfoFromPreTenderbyCreatedDateService";
    public static final String DEFAULT_DOWNLOAD_AOC_DOC_PATH = "/nicgep_external_webservice/pull/postAOCFileDownloadDetailsService";

    // Internal API Route Prefixes
    public static final String API_BASE_PATH = "/api/gepnic";
    public static final String API_DASHBOARD = API_BASE_PATH + "/dashboard";
    public static final String API_OFFICES = API_BASE_PATH + "/offices";
    public static final String API_TENDERS = API_BASE_PATH + "/tenders";
    public static final String API_CORRIGENDUMS = API_BASE_PATH + "/corrigendums";
    public static final String API_DOCUMENTS = API_BASE_PATH + "/documents";
    public static final String API_METADATA = API_BASE_PATH + "/metadata";
    public static final String API_AOC = API_BASE_PATH + "/aoc";
    public static final String API_TRANSACTIONS = API_BASE_PATH + "/transactions";
    public static final String API_CONFIGURATION = API_BASE_PATH + "/configuration";
    public static final String API_DIAGNOSTICS = API_BASE_PATH + "/diagnostics";

    // Cryptographic Constants
    public static final String ALGORITHM_AES = "AES";
    public static final String CIPHER_AES_GCM = "AES/GCM/NoPadding";
    public static final String CIPHER_RSA = "RSA/ECB/PKCS1Padding";
    public static final String SIGNATURE_SHA256_RSA = "SHA256withRSA";
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH_BYTES = 12;
    public static final int GCM_TAG_LENGTH_BITS = 128;

    // Response Status Codes
    public static final int RESPONSE_CODE_SUCCESS = 1;
    public static final int RESPONSE_CODE_FAILURE = 0;
    public static final int NIC_SUB_CODE_INSERT_SUCCESS = 101;
    public static final int NIC_SUB_CODE_DOC_UPLOAD_SUCCESS = 105;

    // Supported Document Mime Types / Extensions
    public static final String EXT_PDF = ".pdf";
    public static final String EXT_XLS = ".xls";
    public static final String EXT_XLSX = ".xlsx";
    public static final String EXT_RAR = ".rar";
    public static final String EXT_JPG = ".jpg";
}
