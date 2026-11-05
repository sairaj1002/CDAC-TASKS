package in.gov.wamis.common.exception;

import java.util.Collections;
import java.util.Map;

public class BusinessValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public BusinessValidationException(String message) {
        super(message);
        this.errors = Collections.emptyMap();
    }

    public BusinessValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors != null ? errors : Collections.emptyMap();
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}

