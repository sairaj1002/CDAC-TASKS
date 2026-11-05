package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.PortalCode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Multi-work batch request model as specified in Section 8.
 * Internal batch request for triggering independent integration jobs.
 */
public class PreTenderBatchForm {

    @NotNull(message = "Portal code is required")
    private PortalCode portalCode = PortalCode.GEPNIC;

    @NotNull(message = "Operation is required")
    private IntegrationOperation operation = IntegrationOperation.PRE_TENDER;

    @NotEmpty(message = "At least one workId is required")
    private List<Long> workIds;

    public PreTenderBatchForm() {
    }

    public PreTenderBatchForm(PortalCode portalCode, IntegrationOperation operation, List<Long> workIds) {
        this.portalCode = portalCode;
        this.operation = operation;
        this.workIds = workIds;
    }

    public PortalCode getPortalCode() {
        return portalCode;
    }

    public void setPortalCode(PortalCode portalCode) {
        this.portalCode = portalCode;
    }

    public IntegrationOperation getOperation() {
        return operation;
    }

    public void setOperation(IntegrationOperation operation) {
        this.operation = operation;
    }

    public List<Long> getWorkIds() {
        return workIds;
    }

    public void setWorkIds(List<Long> workIds) {
        this.workIds = workIds;
    }
}

