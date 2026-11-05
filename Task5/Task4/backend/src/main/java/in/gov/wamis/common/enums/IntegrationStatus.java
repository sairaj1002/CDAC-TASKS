package in.gov.wamis.common.enums;

/**
 * Standard integration status states for WAMIS external portal operations.
 * As defined in Section 9 of the development flow.
 */
public enum IntegrationStatus {
    DRAFT,
    VALIDATION_FAILED,
    READY,
    QUEUED,
    IN_PROGRESS,
    ACCEPTED,
    REJECTED,
    RETRY_PENDING,
    OUTCOME_UNKNOWN,
    FAILED
}

