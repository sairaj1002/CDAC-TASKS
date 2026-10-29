package com.cdac.egov.integration.gepnic.pretender.services;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cdac.egov.integration.gepnic.pretender.entities.Tender;
import com.cdac.egov.integration.gepnic.pretender.forms.TenderRequest;
import com.cdac.egov.integration.gepnic.pretender.forms.TenderResponse;
import com.cdac.egov.integration.gepnic.pretender.repositories.TenderRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterTenderTypeRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterNumberOfCoversRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterTenderCategoryRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterWorkItemProductCategoryRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterNumberOfBidOpenersRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterFormOfContractRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterTendererClassRepository;

@Service
@Transactional
public class TenderService {
    private final TenderRepository tenders;
    private final MasterTenderTypeRepository tenderTypes;
    private final MasterNumberOfCoversRepository covers;
    private final MasterTenderCategoryRepository categories;
    private final MasterWorkItemProductCategoryRepository productCategories;
    private final MasterNumberOfBidOpenersRepository bidOpeners;
    private final MasterFormOfContractRepository contracts;
    private final MasterTendererClassRepository tendererClasses;

    public TenderService(TenderRepository tenders, MasterTenderTypeRepository tenderTypes,
        MasterNumberOfCoversRepository covers, MasterTenderCategoryRepository categories,
        MasterWorkItemProductCategoryRepository productCategories, MasterNumberOfBidOpenersRepository bidOpeners,
        MasterFormOfContractRepository contracts, MasterTendererClassRepository tendererClasses) {
        this.tenders = tenders; this.tenderTypes = tenderTypes; this.covers = covers; this.categories = categories;
        this.productCategories = productCategories; this.bidOpeners = bidOpeners; this.contracts = contracts;
        this.tendererClasses = tendererClasses;
    }

    public TenderResponse create(TenderRequest request) {
        validateDateRanges(request);
        return toResponse(tenders.save(apply(request, new Tender())));
    }

    public TenderResponse update(long id, TenderRequest request) {
        validateDateRanges(request);
        Tender existing = tenders.findById(id).orElseThrow(() -> new EntityNotFoundException("Pre-tender record " + id + " was not found"));
        return toResponse(tenders.save(apply(request, existing)));
    }

    @Transactional(readOnly = true)
    public TenderResponse get(long id) {
        return tenders.findById(id).map(this::toResponse)
            .orElseThrow(() -> new EntityNotFoundException("Pre-tender record " + id + " was not found"));
    }

    private Tender apply(TenderRequest request, Tender tender) {
        tender.procurementType = request.procurementType().trim();
        tender.tenderReferenceNumber = request.tenderReferenceNumber().trim();
        tender.tenderType = tenderTypes.findById(request.tenderTypeId()).orElseThrow(() -> missing("Tender Type"));
        tender.numberOfCovers = covers.findById(request.numberOfCoversId()).orElseThrow(() -> missing("Number of Covers"));
        tender.tenderCategory = categories.findById(request.tenderCategoryId()).orElseThrow(() -> missing("Tender Category"));
        tender.workItemProductCategory = productCategories.findById(request.workItemProductCategoryId())
            .orElseThrow(() -> missing("Work Item Product Category"));
        if (tender.workItemProductCategory.getTenderCategory() == null
            || !tender.workItemProductCategory.getTenderCategory().getId().equals(tender.tenderCategory.getId())) {
            throw new IllegalArgumentException("Work Item Product Category must belong to the selected Tender Category");
        }
        tender.numberOfBidOpeners = bidOpeners.findById(request.numberOfBidOpenersId()).orElseThrow(() -> missing("Number of Bid Openers"));
        tender.formOfContract = contracts.findById(request.formOfContractId()).orElseThrow(() -> missing("Form of Contract"));
        tender.tendererClass = tendererClasses.findById(request.tendererClassId()).orElseThrow(() -> missing("Tenderer Class"));
        tender.bidValidityDays = request.bidValidityDays(); tender.cover1Description = request.cover1Description().trim();
        tender.cover2Description = request.cover2Description(); tender.deliveryPeriodDays = request.deliveryPeriodDays();
        tender.emdExemption = request.emdExemption(); tender.paperCostExemption = request.paperCostExemption();
        tender.tenderFee = request.tenderFee(); tender.tenderEmdFee = request.tenderEmdFee();
        tender.preBidMeeting = request.preBidMeeting(); tender.preBidMeetingAddress = request.preBidMeetingAddress();
        tender.tenderPublishingDateTime = request.tenderPublishingDateTime(); tender.documentDownloadStartDateTime = request.documentDownloadStartDateTime();
        tender.seekClarificationStartDateTime = request.seekClarificationStartDateTime(); tender.seekClarificationEndDateTime = request.seekClarificationEndDateTime();
        tender.bidSubmissionStartDateTime = request.bidSubmissionStartDateTime(); tender.bidSubmissionEndDateTime = request.bidSubmissionEndDateTime();
        tender.bidOpeningDateTime = request.bidOpeningDateTime(); tender.nitFileName = request.nitFileName();
        tender.parentWorkId = request.parentWorkId(); tender.parentWorkNumber = request.parentWorkNumber();
        tender.parentWorkDescription = request.parentWorkDescription();
        tender.dtcnFileName = request.dtcnFileName(); tender.otherFileName = request.otherFileName();
        return tender;
    }

    private void validateDateRanges(TenderRequest request) {
        if (request.seekClarificationEndDateTime().isBefore(request.seekClarificationStartDateTime()))
            throw new IllegalArgumentException("Seek clarification end must be on or after its start");
        if (request.bidSubmissionEndDateTime().isBefore(request.bidSubmissionStartDateTime()))
            throw new IllegalArgumentException("Bid submission end must be on or after its start");
    }

    @Transactional(readOnly = true)
    public List<TenderResponse> list() {
        return tenders.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(this::toResponse).toList();
    }

    private EntityNotFoundException missing(String label) { return new EntityNotFoundException(label + " master value was not found"); }
    private TenderResponse toResponse(Tender tender) {
        return new TenderResponse(tender.id, tender.tenderReferenceNumber, tender.procurementType,
            tender.tenderType.getName(), tender.tenderCategory.getName(), tender.tenderPublishingDateTime,
            tender.nitFileName, tender.dtcnFileName, tender.otherFileName,
            tender.tenderType.getId(), tender.numberOfCovers.getId(), tender.tenderCategory.getId(),
            tender.workItemProductCategory.getId(), tender.numberOfBidOpeners.getId(), tender.formOfContract.getId(),
            tender.bidValidityDays, tender.cover1Description, tender.cover2Description, tender.deliveryPeriodDays,
            tender.tendererClass.getId(), tender.tendererClass.getName(), tender.emdExemption, tender.paperCostExemption,
            tender.tenderFee, tender.tenderEmdFee, tender.preBidMeeting, tender.preBidMeetingAddress,
            tender.documentDownloadStartDateTime, tender.seekClarificationStartDateTime, tender.seekClarificationEndDateTime,
            tender.bidSubmissionStartDateTime, tender.bidSubmissionEndDateTime, tender.bidOpeningDateTime,
            tender.numberOfCovers.getName(), tender.workItemProductCategory.getName(), tender.numberOfBidOpeners.getName(),
            tender.formOfContract.getName(), tender.parentWorkId, tender.parentWorkNumber, tender.parentWorkDescription);
    }
}
