package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.exception.BusinessValidationException;
import in.gov.wamis.common.security.SecurityContextHelper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validates WAMIS Pre-Tender business invariants prior to submission.
 * Enforces business logic and rules independently from any external procurement portal.
 */
@Component
public class PreTenderValidator {

    private final SecurityContextHelper securityContextHelper;

    public PreTenderValidator(SecurityContextHelper securityContextHelper) {
        this.securityContextHelper = securityContextHelper;
    }

    public void validate(PreTender preTender) {
        Map<String, String> errors = new HashMap<>();

        if (preTender == null) {
            throw new BusinessValidationException("PreTender cannot be null");
        }

        // 1. Office and Work authorization & presence
        if (preTender.getOfficeId() == null || preTender.getOfficeId() <= 0) {
            errors.put("officeId", "Valid office ID is required");
        } else if (!securityContextHelper.isOfficeAuthorized(preTender.getOfficeId())) {
            errors.put("officeId", "User is not authorized for office: " + preTender.getOfficeId());
        }

        if (preTender.getWorkId() == null || preTender.getWorkId() <= 0) {
            errors.put("workId", "Valid work ID is required");
        } else if (!securityContextHelper.isWorkAuthorized(preTender.getWorkId())) {
            errors.put("workId", "User is not authorized for work: " + preTender.getWorkId());
        }

        if (preTender.getOrgId() == null || preTender.getOrgId() <= 0) {
            errors.put("orgId", "Valid organization ID is required");
        }

        // 2. Financials / Estimates
        if (preTender.getTenderValue() == null || preTender.getTenderValue().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("tenderValue", "Tender estimated value must be strictly positive");
        }

        if (preTender.getFeeAmount() != null && preTender.getFeeAmount().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("feeAmount", "Tender fee cannot be negative");
        }

        if (preTender.getEmdAmount() != null && preTender.getEmdAmount().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("emdAmount", "EMD amount cannot be negative");
        }

        // 3. Officer Details
        if (preTender.getInvitingOfficer() == null || preTender.getInvitingOfficer().isBlank()) {
            errors.put("invitingOfficer", "Inviting officer name is required");
        }
        if (preTender.getInvitingOfficerAddress() == null || preTender.getInvitingOfficerAddress().isBlank()) {
            errors.put("invitingOfficerAddress", "Inviting officer address is required");
        }

        // 4. Critical Dates Sequence Invariants
        validateDates(preTender, errors);

        // 5. Document Metadata Invariants
        validateDocuments(preTender.getDocuments(), errors);

        if (!errors.isEmpty()) {
            throw new BusinessValidationException("Pre-Tender business validation failed", errors);
        }
    }

    private void validateDates(PreTender pt, Map<String, String> errors) {
        LocalDateTime pubDate = pt.getPublishingDate();
        LocalDateTime docStart = pt.getDocDownloadStartDate();
        LocalDateTime bidStart = pt.getBidSubmissionStartDate();
        LocalDateTime bidEnd = pt.getBidSubmissionEndDate();
        LocalDateTime bidOpen = pt.getBidOpenDate();

        if (pubDate == null) {
            errors.put("publishingDate", "Publishing date is required");
            return;
        }
        if (docStart == null) {
            errors.put("docDownloadStartDate", "Document download start date is required");
            return;
        }
        if (bidStart == null) {
            errors.put("bidSubmissionStartDate", "Bid submission start date is required");
            return;
        }
        if (bidEnd == null) {
            errors.put("bidSubmissionEndDate", "Bid submission end date is required");
            return;
        }
        if (bidOpen == null) {
            errors.put("bidOpenDate", "Bid opening date is required");
            return;
        }

        if (docStart.isBefore(pubDate)) {
            errors.put("docDownloadStartDate", "Document download start date cannot precede publishing date");
        }
        if (bidStart.isBefore(docStart)) {
            errors.put("bidSubmissionStartDate", "Bid submission start date cannot precede document download start date");
        }
        if (!bidEnd.isAfter(bidStart)) {
            errors.put("bidSubmissionEndDate", "Bid submission end date must be strictly after bid submission start date");
        }
        if (bidOpen.isBefore(bidEnd)) {
            errors.put("bidOpenDate", "Bid open date cannot precede bid submission end date");
        }

        // Clarification dates check
        if (pt.getSeekClarificationStartDate() != null && pt.getSeekClarificationEndDate() != null) {
            if (pt.getSeekClarificationEndDate().isBefore(pt.getSeekClarificationStartDate())) {
                errors.put("seekClarificationEndDate", "Clarification end date cannot precede clarification start date");
            }
            if (pt.getSeekClarificationEndDate().isAfter(bidEnd)) {
                errors.put("seekClarificationEndDate", "Clarification end date cannot be after bid submission end date");
            }
        }

        // Pre-bid meeting check
        if (Boolean.TRUE.equals(pt.getPreBidMeeting())) {
            if (pt.getPreBidMeetingDate() == null) {
                errors.put("preBidMeetingDate", "Pre-bid meeting date is required when pre-bid meeting is enabled");
            } else {
                if (pt.getPreBidMeetingDate().isBefore(pubDate) || pt.getPreBidMeetingDate().isAfter(bidStart)) {
                    errors.put("preBidMeetingDate", "Pre-bid meeting date must be between publishing date and bid submission start date");
                }
            }
        }
    }

    private void validateDocuments(List<PreTenderDocument> documents, Map<String, String> errors) {
        if (documents == null || documents.isEmpty()) {
            errors.put("documents", "At least one tender document (e.g. NIT) is required");
            return;
        }

        boolean hasNitDoc = false;
        for (int i = 0; i < documents.size(); i++) {
            PreTenderDocument doc = documents.get(i);
            String prefix = "documents[" + i + "].";
            if (doc.getDocUniqueId() == null || doc.getDocUniqueId() <= 0) {
                errors.put(prefix + "docUniqueId", "Document unique ID must be positive");
            }
            if (doc.getDocCode() == null || doc.getDocCode().isBlank()) {
                errors.put(prefix + "docCode", "Document code is required");
            } else if (doc.getDocCode().toUpperCase().contains("NIT")) {
                hasNitDoc = true;
            }
            if (doc.getDocName() == null || doc.getDocName().isBlank()) {
                errors.put(prefix + "docName", "Document name is required");
            }
            if (doc.getDocSize() == null || doc.getDocSize() <= 0) {
                errors.put(prefix + "docSize", "Document size must be positive");
            }
            if (doc.getDocMimeType() == null || doc.getDocMimeType().isBlank()) {
                errors.put(prefix + "docMimeType", "Document extension / MIME type is required");
            }
        }

        if (!hasNitDoc) {
            errors.put("documents", "At least one NIT document (code containing 'NIT') is required");
        }
    }
}

