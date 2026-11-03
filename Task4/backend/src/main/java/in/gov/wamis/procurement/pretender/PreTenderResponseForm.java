package in.gov.wamis.procurement.pretender;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.gov.wamis.common.enums.IntegrationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard API response model for PreTender data.
 * Adheres strictly to the architectural rule: Never return JPA entities directly from controllers.
 */
public class PreTenderResponseForm {

    private Long id;
    private String uniqueFileNo;
    private String tenderReferenceNo;
    private Long workId;
    private Long officeId;
    private Long orgId;
    private String tenderTitle;
    private String tenderDescription;
    private String tenderType;
    private String formContract;
    private String category;
    private String productCategory;
    private String bidOpeners;
    private String tendererClass;
    private Integer coversCount;
    private BigDecimal tenderValue;
    private BigDecimal feeAmount;
    private BigDecimal emdAmount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishingDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime docDownloadStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidSubmissionStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidSubmissionEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidOpenDate;

    private IntegrationStatus status;
    private List<PreTenderDocumentForm> documents = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public PreTenderResponseForm() {
    }

    public static PreTenderResponseForm fromEntity(PreTender entity) {
        if (entity == null) {
            return null;
        }
        PreTenderResponseForm form = new PreTenderResponseForm();
        form.setId(entity.getId());
        form.setUniqueFileNo(entity.getUniqueFileNo());
        form.setTenderReferenceNo(entity.getTenderReferenceNo());
        form.setWorkId(entity.getWorkId());
        form.setOfficeId(entity.getOfficeId());
        form.setOrgId(entity.getOrgId());
        form.setTenderTitle(entity.getTenderTitle());
        form.setTenderDescription(entity.getTenderDescription());
        form.setTenderType(entity.getTenderType());
        form.setFormContract(entity.getFormContract());
        form.setCategory(entity.getCategory());
        form.setProductCategory(entity.getProductCategory());
        form.setBidOpeners(entity.getBidOpeners());
        form.setTendererClass(entity.getTendererClass());
        form.setCoversCount(entity.getCoversCount());
        form.setTenderValue(entity.getTenderValue());
        form.setFeeAmount(entity.getFeeAmount());
        form.setEmdAmount(entity.getEmdAmount());
        form.setPublishingDate(entity.getPublishingDate());
        form.setDocDownloadStartDate(entity.getDocDownloadStartDate());
        form.setBidSubmissionStartDate(entity.getBidSubmissionStartDate());
        form.setBidSubmissionEndDate(entity.getBidSubmissionEndDate());
        form.setBidOpenDate(entity.getBidOpenDate());
        form.setStatus(entity.getStatus());
        form.setCreatedAt(entity.getCreatedAt());

        if (entity.getDocuments() != null) {
            for (PreTenderDocument doc : entity.getDocuments()) {
                form.getDocuments().add(new PreTenderDocumentForm(
                        doc.getDocUniqueId(),
                        doc.getDocCode(),
                        doc.getDocName(),
                        doc.getDocDescription(),
                        doc.getDocMimeType(),
                        doc.getDocSize(),
                        doc.getDocPriority()
                ));
            }
        }
        return form;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueFileNo() {
        return uniqueFileNo;
    }

    public void setUniqueFileNo(String uniqueFileNo) {
        this.uniqueFileNo = uniqueFileNo;
    }

    public String getTenderReferenceNo() {
        return tenderReferenceNo;
    }

    public void setTenderReferenceNo(String tenderReferenceNo) {
        this.tenderReferenceNo = tenderReferenceNo;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getTenderTitle() {
        return tenderTitle;
    }

    public void setTenderTitle(String tenderTitle) {
        this.tenderTitle = tenderTitle;
    }

    public String getTenderDescription() {
        return tenderDescription;
    }

    public void setTenderDescription(String tenderDescription) {
        this.tenderDescription = tenderDescription;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public String getFormContract() {
        return formContract;
    }

    public void setFormContract(String formContract) {
        this.formContract = formContract;
    }

    public String getBidOpeners() {
        return bidOpeners;
    }

    public void setBidOpeners(String bidOpeners) {
        this.bidOpeners = bidOpeners;
    }

    public String getTendererClass() {
        return tendererClass;
    }

    public void setTendererClass(String tendererClass) {
        this.tendererClass = tendererClass;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCoversCount() {
        return coversCount;
    }

    public void setCoversCount(Integer coversCount) {
        this.coversCount = coversCount;
    }

    public BigDecimal getTenderValue() {
        return tenderValue;
    }

    public void setTenderValue(BigDecimal tenderValue) {
        this.tenderValue = tenderValue;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getEmdAmount() {
        return emdAmount;
    }

    public void setEmdAmount(BigDecimal emdAmount) {
        this.emdAmount = emdAmount;
    }

    public LocalDateTime getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(LocalDateTime publishingDate) {
        this.publishingDate = publishingDate;
    }

    public LocalDateTime getDocDownloadStartDate() {
        return docDownloadStartDate;
    }

    public void setDocDownloadStartDate(LocalDateTime docDownloadStartDate) {
        this.docDownloadStartDate = docDownloadStartDate;
    }

    public LocalDateTime getBidSubmissionStartDate() {
        return bidSubmissionStartDate;
    }

    public void setBidSubmissionStartDate(LocalDateTime bidSubmissionStartDate) {
        this.bidSubmissionStartDate = bidSubmissionStartDate;
    }

    public LocalDateTime getBidSubmissionEndDate() {
        return bidSubmissionEndDate;
    }

    public void setBidSubmissionEndDate(LocalDateTime bidSubmissionEndDate) {
        this.bidSubmissionEndDate = bidSubmissionEndDate;
    }

    public LocalDateTime getBidOpenDate() {
        return bidOpenDate;
    }

    public void setBidOpenDate(LocalDateTime bidOpenDate) {
        this.bidOpenDate = bidOpenDate;
    }

    public IntegrationStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }

    public List<PreTenderDocumentForm> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PreTenderDocumentForm> documents) {
        this.documents = documents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

