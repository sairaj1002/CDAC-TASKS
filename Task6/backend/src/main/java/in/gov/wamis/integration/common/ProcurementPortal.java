package in.gov.wamis.integration.common;

import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.PortalCode;
import in.gov.wamis.integration.gepnic.form.*;

/**
 * Common abstraction for e-procurement portal adapters (GePNIC, etc.).
 * Keeps procurement business logic independent from any e-procurement portal.
 */
public interface ProcurementPortal {

    PortalCode getPortalCode();

    boolean isEnabled();

    boolean supports(IntegrationOperation operation);

    PortalResponse pushTender(PortalRequest request);

    PortalResponse uploadDocument(PortalRequest request, DocumentUploadForm uploadForm);

    PortalResponse pullTender(PortalRequest request, TenderPullForm pullForm);

    PortalResponse pullCorrigendum(PortalRequest request, CorrigendumPullForm pullForm);

    PortalResponse pullAoc(PortalRequest request, AocPullForm pullForm);

    PortalResponse downloadAocDocument(PortalRequest request, AocFileDownloadForm downloadForm);
}

