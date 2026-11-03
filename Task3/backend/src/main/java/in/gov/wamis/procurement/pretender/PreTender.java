package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.enums.IntegrationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PreTender entity representing internal WAMIS procurement and tender planning details.
 * Contains no portal-specific cryptography, HTTP transport, or envelope details.
 */
@Entity
@Table(name = "wamis_pretenders")
public class PreTender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_file_no", length = 100, nullable = false, unique = true)
    private String uniqueFileNo;

    @Column(name = "tender_ref_no", length = 100, nullable = false, unique = true)
    private String tenderReferenceNo;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @Column(name = "org_id", nullable = false)
    private Long orgId;

    @Column(name = "tender_title", length = 250, nullable = false)
    private String tenderTitle;

    @Column(name = "tender_description", length = 2000)
    private String tenderDescription;

    @Column(name = "tender_type", length = 50, nullable = false)
    private String tenderType = "Open Tender";

    @Column(name = "form_contract", length = 50, nullable = false)
    private String formContract = "Supply";

    @Column(name = "category", length = 50, nullable = false)
    private String category = "Works";

    @Column(name = "covers_count", nullable = false)
    private Integer coversCount = 2;

    @Column(name = "bid_openers", length = 50)
    private String bidOpeners = "2 Off 2";

    @Column(name = "automatic_evaluation")
    private Boolean automaticEvaluation = false;

    @Column(name = "allow_multi_currency_boq")
    private Boolean allowMultiCurrencyForBoq = false;

    @Column(name = "creator_name", length = 100)
    private String creatorName;

    @Column(name = "creator_reference", length = 100)
    private String creatorReference = "WAMIS";

    @Column(name = "tender_value", precision = 18, scale = 2, nullable = false)
    private BigDecimal tenderValue;

    @Column(name = "bid_validity_days")
    private Integer bidValidityDays = 60;

    @Column(name = "delivery_period_days")
    private Integer deliveryPeriodDays = 0;

    @Column(name = "location_details", length = 255)
    private String location;

    @Column(name = "pincode")
    private Integer pincode;

    @Column(name = "pre_bid_meeting")
    private Boolean preBidMeeting = false;

    @Column(name = "pre_bid_meeting_place", length = 255)
    private String preBidMeetingPlace = "NA";

    @Column(name = "pre_bid_meeting_address", length = 500)
    private String preBidMeetingAddress = "NA";

    @Column(name = "bid_open_place", length = 255)
    private String bidOpenPlace;

    @Column(name = "tenderer_class", length = 50)
    private String tendererClass = "Class A";

    @Column(name = "inviting_officer", length = 150, nullable = false)
    private String invitingOfficer;

    @Column(name = "inviting_officer_address", length = 500, nullable = false)
    private String invitingOfficerAddress;

    @Column(name = "allow_itemwise_evaluation")
    private Boolean allowItemwiseEvaluation = false;

    @Column(name = "fee_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Column(name = "emd_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal emdAmount = BigDecimal.ZERO;

    @Column(name = "allow_multi_currency_fee")
    private Boolean allowMultiCurrencyForFee = false;

    @Column(name = "publishing_date", nullable = false)
    private LocalDateTime publishingDate;

    @Column(name = "doc_download_start_date", nullable = false)
    private LocalDateTime docDownloadStartDate;

    @Column(name = "seek_clarification_start_date")
    private LocalDateTime seekClarificationStartDate;

    @Column(name = "seek_clarification_end_date")
    private LocalDateTime seekClarificationEndDate;

    @Column(name = "pre_bid_meeting_date")
    private LocalDateTime preBidMeetingDate;

    @Column(name = "bid_submission_start_date", nullable = false)
    private LocalDateTime bidSubmissionStartDate;

    @Column(name = "bid_submission_end_date", nullable = false)
    private LocalDateTime bidSubmissionEndDate;

    @Column(name = "bid_open_date", nullable = false)
    private LocalDateTime bidOpenDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private IntegrationStatus status = IntegrationStatus.DRAFT;

    @OneToMany(mappedBy = "preTender", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PreTenderDocument> documents = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PreTender() {
    }

    public void addDocument(PreTenderDocument document) {
        documents.add(document);
        document.setPreTender(this);
    }

    public void removeDocument(PreTenderDocument document) {
        documents.remove(document);
        document.setPreTender(null);
    }

    // Getters and Setters
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

    public String getBidOpeners() {
        return bidOpeners;
    }

    public void setBidOpeners(String bidOpeners) {
        this.bidOpeners = bidOpeners;
    }

    public Boolean getAutomaticEvaluation() {
        return automaticEvaluation;
    }

    public void setAutomaticEvaluation(Boolean automaticEvaluation) {
        this.automaticEvaluation = automaticEvaluation;
    }

    public Boolean getAllowMultiCurrencyForBoq() {
        return allowMultiCurrencyForBoq;
    }

    public void setAllowMultiCurrencyForBoq(Boolean allowMultiCurrencyForBoq) {
        this.allowMultiCurrencyForBoq = allowMultiCurrencyForBoq;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorReference() {
        return creatorReference;
    }

    public void setCreatorReference(String creatorReference) {
        this.creatorReference = creatorReference;
    }

    public BigDecimal getTenderValue() {
        return tenderValue;
    }

    public void setTenderValue(BigDecimal tenderValue) {
        this.tenderValue = tenderValue;
    }

    public Integer getBidValidityDays() {
        return bidValidityDays;
    }

    public void setBidValidityDays(Integer bidValidityDays) {
        this.bidValidityDays = bidValidityDays;
    }

    public Integer getDeliveryPeriodDays() {
        return deliveryPeriodDays;
    }

    public void setDeliveryPeriodDays(Integer deliveryPeriodDays) {
        this.deliveryPeriodDays = deliveryPeriodDays;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public Boolean getPreBidMeeting() {
        return preBidMeeting;
    }

    public void setPreBidMeeting(Boolean preBidMeeting) {
        this.preBidMeeting = preBidMeeting;
    }

    public String getPreBidMeetingPlace() {
        return preBidMeetingPlace;
    }

    public void setPreBidMeetingPlace(String preBidMeetingPlace) {
        this.preBidMeetingPlace = preBidMeetingPlace;
    }

    public String getPreBidMeetingAddress() {
        return preBidMeetingAddress;
    }

    public void setPreBidMeetingAddress(String preBidMeetingAddress) {
        this.preBidMeetingAddress = preBidMeetingAddress;
    }

    public String getBidOpenPlace() {
        return bidOpenPlace;
    }

    public void setBidOpenPlace(String bidOpenPlace) {
        this.bidOpenPlace = bidOpenPlace;
    }

    public String getTendererClass() {
        return tendererClass;
    }

    public void setTendererClass(String tendererClass) {
        this.tendererClass = tendererClass;
    }

    public String getInvitingOfficer() {
        return invitingOfficer;
    }

    public void setInvitingOfficer(String invitingOfficer) {
        this.invitingOfficer = invitingOfficer;
    }

    public String getInvitingOfficerAddress() {
        return invitingOfficerAddress;
    }

    public void setInvitingOfficerAddress(String invitingOfficerAddress) {
        this.invitingOfficerAddress = invitingOfficerAddress;
    }

    public Boolean getAllowItemwiseEvaluation() {
        return allowItemwiseEvaluation;
    }

    public void setAllowItemwiseEvaluation(Boolean allowItemwiseEvaluation) {
        this.allowItemwiseEvaluation = allowItemwiseEvaluation;
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

    public Boolean getAllowMultiCurrencyForFee() {
        return allowMultiCurrencyForFee;
    }

    public void setAllowMultiCurrencyForFee(Boolean allowMultiCurrencyForFee) {
        this.allowMultiCurrencyForFee = allowMultiCurrencyForFee;
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

    public LocalDateTime getSeekClarificationStartDate() {
        return seekClarificationStartDate;
    }

    public void setSeekClarificationStartDate(LocalDateTime seekClarificationStartDate) {
        this.seekClarificationStartDate = seekClarificationStartDate;
    }

    public LocalDateTime getSeekClarificationEndDate() {
        return seekClarificationEndDate;
    }

    public void setSeekClarificationEndDate(LocalDateTime seekClarificationEndDate) {
        this.seekClarificationEndDate = seekClarificationEndDate;
    }

    public LocalDateTime getPreBidMeetingDate() {
        return preBidMeetingDate;
    }

    public void setPreBidMeetingDate(LocalDateTime preBidMeetingDate) {
        this.preBidMeetingDate = preBidMeetingDate;
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

    public List<PreTenderDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PreTenderDocument> documents) {
        this.documents = documents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

