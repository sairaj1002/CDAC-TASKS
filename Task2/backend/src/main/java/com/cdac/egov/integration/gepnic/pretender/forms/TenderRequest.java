package com.cdac.egov.integration.gepnic.pretender.forms;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;

public record TenderRequest(
    @NotBlank @Size(max = 80) String procurementType,
    @NotBlank @Size(max = 180) String tenderReferenceNumber,
    @NotNull Long tenderTypeId,
    @NotNull Long numberOfCoversId,
    @NotNull Long tenderCategoryId,
    @NotNull Long workItemProductCategoryId,
    @NotNull Long numberOfBidOpenersId,
    @NotNull Long formOfContractId,
    @NotNull @Positive Integer bidValidityDays,
    @NotBlank @Size(max = 2000) String cover1Description,
    @Size(max = 2000) String cover2Description,
    @NotNull @Positive Integer deliveryPeriodDays,
    @NotNull Long tendererClassId,
    @NotNull Boolean emdExemption,
    @NotNull Boolean paperCostExemption,
    @PositiveOrZero @Digits(integer = 12, fraction = 2) BigDecimal tenderFee,
    @PositiveOrZero @Digits(integer = 12, fraction = 2) BigDecimal tenderEmdFee,
    @NotNull Boolean preBidMeeting,
    @Size(max = 2000) String preBidMeetingAddress,
    @NotNull LocalDateTime tenderPublishingDateTime,
    @NotNull LocalDateTime documentDownloadStartDateTime,
    @NotNull LocalDateTime seekClarificationStartDateTime,
    @NotNull LocalDateTime seekClarificationEndDateTime,
    @NotNull LocalDateTime bidSubmissionStartDateTime,
    @NotNull LocalDateTime bidSubmissionEndDateTime,
    @NotNull LocalDateTime bidOpeningDateTime,
    @Size(max = 160) String parentWorkId,
    @Size(max = 120) String parentWorkNumber,
    @Size(max = 2000) String parentWorkDescription,
    String nitFileName,
    String dtcnFileName,
    String otherFileName
) {
    public TenderRequest withParentWork(String id, String number, String description) {
        return new TenderRequest(procurementType, tenderReferenceNumber, tenderTypeId, numberOfCoversId,
            tenderCategoryId, workItemProductCategoryId, numberOfBidOpenersId, formOfContractId, bidValidityDays,
            cover1Description, cover2Description, deliveryPeriodDays, tendererClassId, emdExemption,
            paperCostExemption, tenderFee, tenderEmdFee, preBidMeeting, preBidMeetingAddress,
            tenderPublishingDateTime, documentDownloadStartDateTime, seekClarificationStartDateTime,
            seekClarificationEndDateTime, bidSubmissionStartDateTime, bidSubmissionEndDateTime,
            bidOpeningDateTime, id, number, description, nitFileName, dtcnFileName, otherFileName);
    }

    public TenderRequest withFileNames(String nit, String dtcn, String other) {
        return new TenderRequest(procurementType, tenderReferenceNumber, tenderTypeId, numberOfCoversId,
            tenderCategoryId, workItemProductCategoryId, numberOfBidOpenersId, formOfContractId, bidValidityDays,
            cover1Description, cover2Description, deliveryPeriodDays, tendererClassId, emdExemption,
            paperCostExemption, tenderFee, tenderEmdFee, preBidMeeting, preBidMeetingAddress,
            tenderPublishingDateTime, documentDownloadStartDateTime, seekClarificationStartDateTime,
            seekClarificationEndDateTime, bidSubmissionStartDateTime, bidSubmissionEndDateTime,
            bidOpeningDateTime, parentWorkId, parentWorkNumber, parentWorkDescription,
            nit == null ? nitFileName : nit, dtcn == null ? dtcnFileName : dtcn,
            other == null ? otherFileName : other);
    }
}
