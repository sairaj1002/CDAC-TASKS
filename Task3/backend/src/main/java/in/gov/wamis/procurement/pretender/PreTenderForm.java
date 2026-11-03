package in.gov.wamis.procurement.pretender;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.gov.wamis.common.constants.ValidationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * API request model for creating and updating a WAMIS Pre-Tender record.
 */
public class PreTenderForm {

    @NotBlank(message = "Unique file number is required")
    @Size(max = ValidationConstants.MAX_REF_NO_LENGTH)
    private String uniqueFileNo;

    @NotBlank(message = ValidationConstants.MSG_REF_NO_REQUIRED)
    @Size(max = ValidationConstants.MAX_REF_NO_LENGTH)
    private String tenderReferenceNo;

    @NotNull(message = ValidationConstants.MSG_WORK_ID_REQUIRED)
    @Positive(message = "Work ID must be positive")
    private Long workId;

    @NotNull(message = ValidationConstants.MSG_OFFICE_ID_REQUIRED)
    @Positive(message = "Office ID must be positive")
    private Long officeId;

    @NotNull(message = "Organization ID is required")
    @Positive(message = "Organization ID must be positive")
    private Long orgId;

    @NotBlank(message = ValidationConstants.MSG_TITLE_REQUIRED)
    @Size(max = ValidationConstants.MAX_TITLE_LENGTH)
    private String tenderTitle;

    @Size(max = ValidationConstants.MAX_DESC_LENGTH)
    private String tenderDescription;

    @NotBlank(message = "Tender type is required")
    private String tenderType = "Open Tender";

    @NotBlank(message = "Form of contract is required")
    private String formContract = "Supply";

    @NotBlank(message = "Tender category is required")
    private String category = "Works";

    @NotNull(message = "Number of covers is required")
    @Min(value = 1, message = "At least 1 cover is required")
    @Max(value = 4, message = "Covers cannot exceed 4")
    private Integer coversCount = 2;

    private String bidOpeners = "2 Off 2";
    private Boolean automaticEvaluation = false;
    private Boolean allowMultiCurrencyForBoq = false;
    private String creatorName;
    private String creatorReference = "WAMIS";

    @NotNull(message = "Tender value is required")
    @Positive(message = ValidationConstants.MSG_POSITIVE_VALUE)
    private BigDecimal tenderValue;

    private Integer bidValidityDays = 60;
    private Integer deliveryPeriodDays = 0;
    private String location;
    private Integer pincode;
    private Boolean preBidMeeting = false;
    private String preBidMeetingPlace = "NA";
    private String preBidMeetingAddress = "NA";
    private String bidOpenPlace;
    private String tendererClass = "Class A";

    @NotBlank(message = "Inviting officer is required")
    @Size(max = ValidationConstants.MAX_OFFICER_NAME_LENGTH)
    private String invitingOfficer;

    @NotBlank(message = "Inviting officer address is required")
    @Size(max = ValidationConstants.MAX_ADDRESS_LENGTH)
    private String invitingOfficerAddress;

    private Boolean allowItemwiseEvaluation = false;

    @NotNull(message = "Fee amount is required")
    @DecimalMin(value = "0.0", message = ValidationConstants.MSG_NON_NEGATIVE_FEE)
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @NotNull(message = "EMD amount is required")
    @DecimalMin(value = "0.0", message = ValidationConstants.MSG_NON_NEGATIVE_EMD)
    private BigDecimal emdAmount = BigDecimal.ZERO;

    private Boolean allowMultiCurrencyForFee = false;

    @NotNull(message = "Publishing date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishingDate;

    @NotNull(message = "Document download start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime docDownloadStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime seekClarificationStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime seekClarificationEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime preBidMeetingDate;

    @NotNull(message = "Bid submission start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidSubmissionStartDate;

    @NotNull(message = "Bid submission end date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidSubmissionEndDate;

    @NotNull(message = "Bid open date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidOpenDate;

    @Valid
    @NotEmpty(message = "At least one tender document is required")
    private List<PreTenderDocumentForm> documents = new ArrayList<>();

    public PreTenderForm() {
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

    public List<PreTenderDocumentForm> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PreTenderDocumentForm> documents) {
        this.documents = documents;
    }
}

