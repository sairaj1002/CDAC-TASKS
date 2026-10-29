package com.cdac.egov.integration.gepnic.pretender.forms;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record TenderResponse(Long id, String tenderReferenceNumber, String procurementType,
    String tenderType, String tenderCategory, LocalDateTime tenderPublishingDateTime,
    String nitFileName, String dtcnFileName, String otherFileName,
    Long tenderTypeId, Long numberOfCoversId, Long tenderCategoryId,
    Long workItemProductCategoryId, Long numberOfBidOpenersId, Long formOfContractId,
    Integer bidValidityDays, String cover1Description, String cover2Description,
    Integer deliveryPeriodDays, Long tendererClassId, String tendererClass,
    Boolean emdExemption, Boolean paperCostExemption, BigDecimal tenderFee,
    BigDecimal tenderEmdFee, Boolean preBidMeeting, String preBidMeetingAddress,
    LocalDateTime documentDownloadStartDateTime, LocalDateTime seekClarificationStartDateTime,
    LocalDateTime seekClarificationEndDateTime, LocalDateTime bidSubmissionStartDateTime,
    LocalDateTime bidSubmissionEndDateTime, LocalDateTime bidOpeningDateTime,
    String numberOfCovers, String workItemProductCategory, String numberOfBidOpeners,
    String formOfContract, String parentWorkId, String parentWorkNumber, String parentWorkDescription) {}
