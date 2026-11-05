package in.gov.wamis.common.security;

import java.util.regex.Pattern;

/**
 * Redaction utility to guarantee sensitive data (passwords, private keys, session keys, tokens)
 * is never logged or exposed in audit messages.
 */
public final class Sanitizer {

    private Sanitizer() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final Pattern PRIVATE_KEY_PATTERN =
            Pattern.compile("-----BEGIN [A-Z ]+PRIVATE KEY-----[\\s\\S]*?-----END [A-Z ]+PRIVATE KEY-----", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("(?i)(password|secret|keyStorePassword|trustStorePassword|encryptKey)\\s*[:=]\\s*[\"']?([^\"',\\s]+)[\"']?");
    private static final Pattern SENSITIVE_JSON_FIELD_PATTERN =
            Pattern.compile("(?i)\"(password|secret|encryptKey|privateKey)\"\\s*:\\s*\"[^\"]+\"");

    public static String sanitize(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        String sanitized = PRIVATE_KEY_PATTERN.matcher(input).replaceAll("[REDACTED_PRIVATE_KEY]");
        sanitized = PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED]");
        sanitized = SENSITIVE_JSON_FIELD_PATTERN.matcher(sanitized).replaceAll("\"$1\":\"[REDACTED]\"");
        return sanitized;
    }
}

