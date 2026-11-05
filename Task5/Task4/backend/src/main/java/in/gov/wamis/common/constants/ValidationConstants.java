package in.gov.wamis.common.constants;

/**
 * Reused validation limits and error messages.
 */
public final class ValidationConstants {

    private ValidationConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final int MAX_TITLE_LENGTH = 250;
    public static final int MAX_DESC_LENGTH = 2000;
    public static final int MAX_REF_NO_LENGTH = 100;
    public static final int MAX_OFFICER_NAME_LENGTH = 150;
    public static final int MAX_ADDRESS_LENGTH = 500;
    public static final int MAX_DOC_NAME_LENGTH = 255;
    public static final long MAX_DOCUMENT_FILE_SIZE = 20L * 1024 * 1024; // 20 MB

    public static final String MSG_TITLE_REQUIRED = "Tender title is required";
    public static final String MSG_WORK_ID_REQUIRED = "Work ID is required";
    public static final String MSG_OFFICE_ID_REQUIRED = "Office ID is required";
    public static final String MSG_REF_NO_REQUIRED = "Tender reference number is required";
    public static final String MSG_POSITIVE_VALUE = "Tender estimated value must be strictly positive";
    public static final String MSG_NON_NEGATIVE_FEE = "Tender fee must not be negative";
    public static final String MSG_NON_NEGATIVE_EMD = "EMD amount must not be negative";
}

