package in.gov.wamis.procurement.pretender;

import in.gov.wamis.master.service.MasterDataService;
import in.gov.wamis.common.constants.CommonConstants;
import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.common.enums.PortalCode;
import in.gov.wamis.common.exception.BusinessValidationException;
import in.gov.wamis.common.exception.ResourceNotFoundException;
import in.gov.wamis.common.security.SecurityContextHelper;
import in.gov.wamis.integration.common.IntegrationResult;
import in.gov.wamis.integration.common.PortalRequest;
import in.gov.wamis.integration.common.PortalResponse;
import in.gov.wamis.integration.common.ProcurementPortal;
import in.gov.wamis.integration.registry.PortalAdapterRegistry;
import in.gov.wamis.integration.transaction.entity.IntegrationTransaction;
import in.gov.wamis.integration.transaction.service.IntegrationTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service coordinating WAMIS PreTender business workflows,
 * entity lifecycle, business validation, and portal submission.
 */
@Service
public class PreTenderService {

    private static final Logger log = LoggerFactory.getLogger(PreTenderService.class);

    private final PreTenderRepository preTenderRepository;
    private final PreTenderValidator preTenderValidator;
    private final PortalAdapterRegistry adapterRegistry;
    private final IntegrationTransactionService transactionService;
    private final SecurityContextHelper securityContextHelper;
    private final MasterDataService masterDataService;

    public PreTenderService(PreTenderRepository preTenderRepository,
                             PreTenderValidator preTenderValidator,
                             PortalAdapterRegistry adapterRegistry,
                             IntegrationTransactionService transactionService,
                             SecurityContextHelper securityContextHelper,
                             MasterDataService masterDataService) {
        this.preTenderRepository = preTenderRepository;
        this.preTenderValidator = preTenderValidator;
        this.adapterRegistry = adapterRegistry;
        this.transactionService = transactionService;
        this.securityContextHelper = securityContextHelper;
        this.masterDataService = masterDataService;
    }

    // runs once startup (including the master data scripts) has finished
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedSampleWorks() {
        if (preTenderRepository.count() > 0) {
            return;
        }
        log.info("Seeding initial WAMIS sample works matching Screenshot 1 & 2...");

        PreTender pt = new PreTender();
        pt.setUniqueFileNo("3/9434/1");
        pt.setTenderReferenceNo("Tender Online CC 05 of 2023");
        pt.setWorkId(9434L);
        pt.setOfficeId(10L);
        pt.setOrgId(2297L);
        pt.setTenderTitle("Improvement of Banapur main canal Antarkiyari-Kosarada road in the Dist. of Khordha under Special Central Assistance to States Capital Expenditure (CAPEX) for the year 2022-23");
        pt.setTenderDescription("Improvement of Banapur main canal Antarkiyari-Kosarada road in the Dist. of Khordha under Special Central Assistance to States Capital Expenditure (CAPEX) for the year 2022-23");
        pt.setTenderType(masterDataService.tenderType("Open Tender"));
        pt.setFormContract(masterDataService.formOfContract("Percentage"));
        pt.setCategory(masterDataService.tenderCategory("Works"));
        pt.setCoversCount(2);
        pt.setBidOpeners(masterDataService.bidOpeners("2 Off 2"));
        pt.setTenderValue(new BigDecimal("39748169.00"));
        pt.setFeeAmount(new BigDecimal("10000.00"));
        pt.setEmdAmount(new BigDecimal("397482.00"));
        pt.setBidValidityDays(90);
        pt.setDeliveryPeriodDays(365);
        pt.setLocation("Khordha");
        pt.setPincode(751013);
        pt.setPreBidMeeting(false);
        pt.setPreBidMeetingPlace("O/o CCE CRWC BBSR");
        pt.setPreBidMeetingAddress("O/o CCE CRWC BBSR");
        pt.setBidOpenPlace("---");
        pt.setTendererClass(masterDataService.tendererClass("Class A"));
        pt.setInvitingOfficer("---");
        pt.setInvitingOfficerAddress("---");

        LocalDateTime pubDate = LocalDateTime.of(2023, 2, 14, 18, 0, 0);
        pt.setPublishingDate(pubDate);
        pt.setDocDownloadStartDate(pubDate);
        pt.setSeekClarificationStartDate(pubDate);
        pt.setSeekClarificationEndDate(LocalDateTime.of(2023, 2, 27, 15, 0, 0));
        pt.setBidSubmissionStartDate(pubDate);
        pt.setBidSubmissionEndDate(LocalDateTime.of(2023, 2, 28, 17, 0, 0));
        pt.setBidOpenDate(LocalDateTime.of(2023, 3, 1, 11, 0, 0));
        pt.setStatus(IntegrationStatus.DRAFT);

        pt.addDocument(new PreTenderDocument(1L, "NITDOC", "NIT_Tender_Notice.pdf", "Notice Inviting Tender", ".pdf", 45000L, 1));
        pt.addDocument(new PreTenderDocument(2L, "BOQDOC", "BOQ_Banapur_Road.xls", "Financial Bill of Quantities", ".xls", 38000L, 2));

        preTenderRepository.save(pt);
    }

    @Transactional(readOnly = true)
    public List<PreTenderResponseForm> getAllPreTenders() {
        return preTenderRepository.findAll().stream()
                .map(PreTenderResponseForm::fromEntity)
                .toList();
    }

    @Transactional
    public PreTenderResponseForm updatePreTender(Long id, PreTenderForm form) {
        PreTender pt = preTenderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreTender", id));
        mapFormToEntity(form, pt);
        pt.setUpdatedAt(LocalDateTime.now());
        PreTender saved = preTenderRepository.save(pt);
        return PreTenderResponseForm.fromEntity(saved);
    }

    @Transactional
    public void deletePreTender(Long id) {
        PreTender pt = preTenderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreTender", id));
        preTenderRepository.delete(pt);
    }

    @Transactional
    public PreTenderResponseForm createPreTender(PreTenderForm form) {
        if (preTenderRepository.findByUniqueFileNo(form.getUniqueFileNo()).isPresent()) {
            throw new BusinessValidationException("Unique file number already exists: " + form.getUniqueFileNo());
        }
        if (preTenderRepository.findByTenderReferenceNo(form.getTenderReferenceNo()).isPresent()) {
            throw new BusinessValidationException("Tender reference number already exists: " + form.getTenderReferenceNo());
        }

        PreTender pt = new PreTender();
        mapFormToEntity(form, pt);
        pt.setStatus(IntegrationStatus.DRAFT);
        pt.setCreatedAt(LocalDateTime.now());
        pt.setUpdatedAt(LocalDateTime.now());

        PreTender saved = preTenderRepository.save(pt);
        return PreTenderResponseForm.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PreTenderResponseForm getPreTender(Long id) {
        PreTender pt = preTenderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreTender", id));
        return PreTenderResponseForm.fromEntity(pt);
    }

    @Transactional(readOnly = true)
    public void validatePreTender(Long id) {
        PreTender pt = preTenderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreTender", id));
        preTenderValidator.validate(pt);
    }

    @Transactional
    public IntegrationResult submitPreTender(Long preTenderId, PortalCode portalCode,
                                            String correlationId, String idempotencyKey) {
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        String actor = securityContextHelper.getCurrentUser();

        // 1. Authoritative DB load
        PreTender pt = preTenderRepository.findById(preTenderId)
                .orElseThrow(() -> new ResourceNotFoundException("PreTender", preTenderId));

        // 2. Validate WAMIS business rules
        try {
            preTenderValidator.validate(pt);
        } catch (BusinessValidationException bve) {
            pt.setStatus(IntegrationStatus.VALIDATION_FAILED);
            preTenderRepository.save(pt);
            throw bve;
        }

        // 3. Resolve portal adapter
        ProcurementPortal adapter = adapterRegistry.getAdapter(portalCode != null ? portalCode : PortalCode.GEPNIC);

        // 4. Create and persist integration transaction
        IntegrationTransaction txn = transactionService.createTransaction(
                IntegrationOperation.PRE_TENDER,
                adapter.getPortalCode().name(),
                pt.getId(),
                pt.getWorkId(),
                correlationId,
                idempotencyKey,
                actor
        );

        // Idempotency check: if transaction was already completed
        if (txn.getStatus() == IntegrationStatus.ACCEPTED) {
            return IntegrationResult.from(
                    txn.getId(),
                    correlationId,
                    portalCode,
                    IntegrationOperation.PRE_TENDER,
                    txn.getStatus(),
                    txn.getExternalReference(),
                    "Tender already accepted previously (Idempotent response)"
            );
        }

        // 5. Transition to IN_PROGRESS
        transactionService.transitionStatus(txn.getId(), IntegrationStatus.IN_PROGRESS, null, "Dispatching to portal adapter", null, actor);

        // 6. Build portal request
        PortalRequest portalRequest = new PortalRequest(
                pt.getId(),
                pt.getWorkId(),
                pt.getOfficeId(),
                adapter.getPortalCode(),
                IntegrationOperation.PRE_TENDER,
                correlationId,
                idempotencyKey
        );
        portalRequest.setPayload(pt);

        // 7. Dispatch to portal adapter
        PortalResponse portalResponse = adapter.pushTender(portalRequest);

        // 8. Transition transaction state based on response
        transactionService.transitionStatus(
                txn.getId(),
                portalResponse.getStatus(),
                portalResponse.getResponseCode() != null ? String.valueOf(portalResponse.getResponseCode()) : null,
                portalResponse.getMessage(),
                portalResponse.getExternalReference(),
                actor
        );

        // 9. Update PreTender status
        pt.setStatus(portalResponse.getStatus());
        pt.setUpdatedAt(LocalDateTime.now());
        preTenderRepository.save(pt);

        return IntegrationResult.from(
                txn.getId(),
                correlationId,
                portalCode,
                IntegrationOperation.PRE_TENDER,
                portalResponse.getStatus(),
                portalResponse.getExternalReference(),
                portalResponse.getMessage()
        );
    }

    @Transactional
    public PreTenderBatchResponseForm submitBatch(PreTenderBatchForm batchForm, String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        PreTenderBatchResponseForm batchResponse = new PreTenderBatchResponseForm();

        for (Long workId : batchForm.getWorkIds()) {
            String workCorrelationId = correlationId + "-W" + workId;
            List<PreTender> tenders = preTenderRepository.findByWorkId(workId);
            if (tenders.isEmpty()) {
                batchResponse.addItem(workId, null, null, workCorrelationId,
                        IntegrationStatus.FAILED, "No PreTender found for workId: " + workId);
                continue;
            }

            PreTender preTender = tenders.get(0);
            try {
                IntegrationResult result = submitPreTender(
                        preTender.getId(),
                        batchForm.getPortalCode(),
                        workCorrelationId,
                        "BATCH-" + workCorrelationId
                );
                batchResponse.addItem(
                        workId,
                        preTender.getId(),
                        result.getTransactionId(),
                        workCorrelationId,
                        result.getStatus(),
                        result.getMessage()
                );
            } catch (Exception e) {
                log.error("Batch submission failed for workId {}: {}", workId, e.getMessage());
                batchResponse.addItem(
                        workId,
                        preTender.getId(),
                        null,
                        workCorrelationId,
                        IntegrationStatus.FAILED,
                        e.getMessage()
                );
            }
        }
        return batchResponse;
    }

    private void mapFormToEntity(PreTenderForm form, PreTender pt) {
        pt.setUniqueFileNo(form.getUniqueFileNo());
        pt.setTenderReferenceNo(form.getTenderReferenceNo());
        pt.setWorkId(form.getWorkId());
        pt.setOfficeId(form.getOfficeId());
        pt.setOrgId(form.getOrgId());
        pt.setTenderTitle(form.getTenderTitle());
        pt.setTenderDescription(form.getTenderDescription());
        pt.setTenderType(masterDataService.tenderType(form.getTenderType()));
        pt.setFormContract(masterDataService.formOfContract(form.getFormContract()));
        pt.setCategory(masterDataService.tenderCategory(form.getCategory()));
        pt.setProductCategory(masterDataService.productCategory(form.getProductCategory(), pt.getCategoryMaster()));
        pt.setCoversCount(form.getCoversCount());
        pt.setBidOpeners(masterDataService.bidOpeners(form.getBidOpeners()));
        pt.setAutomaticEvaluation(form.getAutomaticEvaluation());
        pt.setAllowMultiCurrencyForBoq(form.getAllowMultiCurrencyForBoq());
        pt.setCreatorName(form.getCreatorName());
        pt.setCreatorReference(form.getCreatorReference());
        pt.setTenderValue(form.getTenderValue());
        pt.setBidValidityDays(form.getBidValidityDays());
        pt.setDeliveryPeriodDays(form.getDeliveryPeriodDays());
        pt.setLocation(form.getLocation());
        pt.setPincode(form.getPincode());
        pt.setPreBidMeeting(form.getPreBidMeeting());
        pt.setPreBidMeetingPlace(form.getPreBidMeetingPlace());
        pt.setPreBidMeetingAddress(form.getPreBidMeetingAddress());
        pt.setBidOpenPlace(form.getBidOpenPlace());
        pt.setTendererClass(masterDataService.tendererClass(form.getTendererClass()));
        pt.setInvitingOfficer(form.getInvitingOfficer());
        pt.setInvitingOfficerAddress(form.getInvitingOfficerAddress());
        pt.setAllowItemwiseEvaluation(form.getAllowItemwiseEvaluation());
        pt.setFeeAmount(form.getFeeAmount());
        pt.setEmdAmount(form.getEmdAmount());
        pt.setAllowMultiCurrencyForFee(form.getAllowMultiCurrencyForFee());
        pt.setPublishingDate(form.getPublishingDate());
        pt.setDocDownloadStartDate(form.getDocDownloadStartDate());
        pt.setSeekClarificationStartDate(form.getSeekClarificationStartDate());
        pt.setSeekClarificationEndDate(form.getSeekClarificationEndDate());
        pt.setPreBidMeetingDate(form.getPreBidMeetingDate());
        pt.setBidSubmissionStartDate(form.getBidSubmissionStartDate());
        pt.setBidSubmissionEndDate(form.getBidSubmissionEndDate());
        pt.setBidOpenDate(form.getBidOpenDate());

        if (form.getDocuments() != null) {
            for (PreTenderDocumentForm docForm : form.getDocuments()) {
                PreTenderDocument doc = new PreTenderDocument(
                        docForm.getDocUniqueId(),
                        docForm.getDocCode(),
                        docForm.getDocName(),
                        docForm.getDocDescription(),
                        docForm.getDocMimeType(),
                        docForm.getDocSize(),
                        docForm.getDocPriority()
                );
                pt.addDocument(doc);
            }
        }
    }
}

