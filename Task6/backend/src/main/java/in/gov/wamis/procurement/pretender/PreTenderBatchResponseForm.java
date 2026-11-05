package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.enums.IntegrationStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch submission response tracking each independent work's outcome.
 */
public class PreTenderBatchResponseForm {

    private int totalSubmitted;
    private int successfulCount;
    private int failedCount;
    private List<WorkJobSummaryForm> items = new ArrayList<>();

    public PreTenderBatchResponseForm() {
    }

    public void addItem(Long workId, Long preTenderId, Long transactionId, String correlationId,
                        IntegrationStatus status, String message) {
        items.add(new WorkJobSummaryForm(workId, preTenderId, transactionId, correlationId, status, message));
        totalSubmitted++;
        if (status == IntegrationStatus.ACCEPTED || status == IntegrationStatus.QUEUED || status == IntegrationStatus.IN_PROGRESS) {
            successfulCount++;
        } else {
            failedCount++;
        }
    }

    public int getTotalSubmitted() {
        return totalSubmitted;
    }

    public void setTotalSubmitted(int totalSubmitted) {
        this.totalSubmitted = totalSubmitted;
    }

    public int getSuccessfulCount() {
        return successfulCount;
    }

    public void setSuccessfulCount(int successfulCount) {
        this.successfulCount = successfulCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public List<WorkJobSummaryForm> getItems() {
        return items;
    }

    public void setItems(List<WorkJobSummaryForm> items) {
        this.items = items;
    }

    public static class WorkJobSummaryForm {
        private Long workId;
        private Long preTenderId;
        private Long transactionId;
        private String correlationId;
        private IntegrationStatus status;
        private String message;

        public WorkJobSummaryForm() {
        }

        public WorkJobSummaryForm(Long workId, Long preTenderId, Long transactionId, String correlationId,
                                 IntegrationStatus status, String message) {
            this.workId = workId;
            this.preTenderId = preTenderId;
            this.transactionId = transactionId;
            this.correlationId = correlationId;
            this.status = status;
            this.message = message;
        }

        public Long getWorkId() {
            return workId;
        }

        public void setWorkId(Long workId) {
            this.workId = workId;
        }

        public Long getPreTenderId() {
            return preTenderId;
        }

        public void setPreTenderId(Long preTenderId) {
            this.preTenderId = preTenderId;
        }

        public Long getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(Long transactionId) {
            this.transactionId = transactionId;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        public IntegrationStatus getStatus() {
            return status;
        }

        public void setStatus(IntegrationStatus status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

