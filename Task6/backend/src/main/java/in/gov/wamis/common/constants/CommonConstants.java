package in.gov.wamis.common.constants;

import java.time.format.DateTimeFormatter;

/**
 * Stable shared defaults, date formats, and headers.
 * Does not contain external URLs, credentials, or private keys.
 */
public final class CommonConstants {

    private CommonConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final DateTimeFormatter DATE_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMMDD);

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DEFAULT_CURRENCY = "INR";
    public static final String SYSTEM_ACTOR = "WAMIS_SYSTEM";

    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
    public static final String HEADER_IDEMPOTENCY_KEY = "X-Idempotency-Key";
}

