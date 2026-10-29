package com.cdac.egov.integration.gepnic.enums;

/**
 * Lifecycle states of an integration transaction between WAMIS and GePNIC.
 */
public enum IntegrationStatus {
    PENDING,
    IN_PROGRESS,
    VALIDATED,
    ENCRYPTED,
    TRANSMITTED,
    SUCCESS,
    FAILED,
    REJECTED,
    RETRIED,
    TIMEOUT
}
