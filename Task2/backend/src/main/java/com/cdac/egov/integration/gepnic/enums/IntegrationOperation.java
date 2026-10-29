package com.cdac.egov.integration.gepnic.enums;

/**
 * Supported GePNIC integration operation types.
 */
public enum IntegrationOperation {
    TENDER_PUSH,
    TENDER_PULL,
    CORRIGENDUM_PULL,
    DOCUMENT_UPLOAD,
    DOCUMENT_STATUS_CHECK,
    METADATA_CHECK,
    AOC_PULL,
    AOC_DOCUMENT_DOWNLOAD,
    CONFIGURATION_UPDATE,
    DIAGNOSTICS_FLOW
}
