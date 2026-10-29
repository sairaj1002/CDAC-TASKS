package com.cdac.egov.integration.gepnic.pretender.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pre_tender")
public class Tender {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "procurement_type", nullable = false, length = 80)
    public String procurementType;
    @Column(name = "tender_reference_number", nullable = false, length = 180)
    public String tenderReferenceNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tender_type_id", nullable = false)
    public MasterTenderType tenderType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "number_of_covers_id", nullable = false)
    public MasterNumberOfCovers numberOfCovers;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tender_category_id", nullable = false)
    public MasterTenderCategory tenderCategory;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "work_item_product_category_id", nullable = false)
    public MasterWorkItemProductCategory workItemProductCategory;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "number_of_bid_openers_id", nullable = false)
    public MasterNumberOfBidOpeners numberOfBidOpeners;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "form_of_contract_id", nullable = false)
    public MasterFormOfContract formOfContract;
    @Column(name = "bid_validity_days", nullable = false)
    public Integer bidValidityDays;
    @Column(name = "cover1_description", nullable = false, columnDefinition = "text")
    public String cover1Description;
    @Column(name = "cover2_description", columnDefinition = "text")
    public String cover2Description;
    @Column(name = "delivery_period_days", nullable = false)
    public Integer deliveryPeriodDays;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tenderer_class_id", nullable = false)
    public MasterTendererClass tendererClass;
    @Column(name = "emd_exemption", nullable = false)
    public Boolean emdExemption;
    @Column(name = "paper_cost_exemption", nullable = false)
    public Boolean paperCostExemption;
    @Column(name = "tender_fee", precision = 14, scale = 2)
    public BigDecimal tenderFee;
    @Column(name = "tender_emd_fee", precision = 14, scale = 2)
    public BigDecimal tenderEmdFee;
    @Column(name = "pre_bid_meeting", nullable = false)
    public Boolean preBidMeeting;
    @Column(name = "pre_bid_meeting_address", columnDefinition = "text")
    public String preBidMeetingAddress;
    @Column(name = "tender_publishing_date_time", nullable = false)
    public LocalDateTime tenderPublishingDateTime;
    @Column(name = "document_download_start_date_time", nullable = false)
    public LocalDateTime documentDownloadStartDateTime;
    @Column(name = "seek_clarification_start_date_time", nullable = false)
    public LocalDateTime seekClarificationStartDateTime;
    @Column(name = "seek_clarification_end_date_time", nullable = false)
    public LocalDateTime seekClarificationEndDateTime;
    @Column(name = "bid_submission_start_date_time", nullable = false)
    public LocalDateTime bidSubmissionStartDateTime;
    @Column(name = "bid_submission_end_date_time", nullable = false)
    public LocalDateTime bidSubmissionEndDateTime;
    @Column(name = "bid_opening_date_time", nullable = false)
    public LocalDateTime bidOpeningDateTime;
    @Column(name = "parent_work_id", length = 160)
    public String parentWorkId;
    @Column(name = "parent_work_number", length = 120)
    public String parentWorkNumber;
    @Column(name = "parent_work_description", columnDefinition = "text")
    public String parentWorkDescription;
    @Column(name = "nit_file_name", length = 255)
    public String nitFileName;
    @Column(name = "dtcn_file_name", length = 255)
    public String dtcnFileName;
    @Column(name = "other_file_name", length = 255)
    public String otherFileName;
    public Tender() {}
}
