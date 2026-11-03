package in.gov.wamis.procurement.pretender;

import in.gov.wamis.audit.entity.IntegrationAuditEvent;
import in.gov.wamis.audit.service.IntegrationAuditService;
import in.gov.wamis.common.enums.IntegrationOperation;
import in.gov.wamis.common.enums.IntegrationStatus;
import in.gov.wamis.common.enums.PortalCode;
import in.gov.wamis.integration.common.IntegrationResult;
import in.gov.wamis.integration.gepnic.client.GePNICClient;
import in.gov.wamis.integration.transaction.entity.IntegrationTransaction;
import in.gov.wamis.integration.transaction.service.IntegrationTransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class PreTenderIntegrationTest {

    @Autowired
    private PreTenderService preTenderService;

    @Autowired
    private PreTenderRepository preTenderRepository;

    @Autowired
    private IntegrationTransactionService transactionService;

    @Autowired
    private IntegrationAuditService auditService;

    @MockitoBean
    private GePNICClient gepnicClient;

    private PreTenderForm createValidForm(String fileNo, String refNo, Long workId) {
        PreTenderForm form = new PreTenderForm();
        form.setUniqueFileNo(fileNo);
        form.setTenderReferenceNo(refNo);
        form.setOfficeId(10L);
        form.setWorkId(workId);
        form.setOrgId(2297L);
        form.setTenderTitle("Construction of Rural Road");
        form.setTenderValue(new BigDecimal("1500000.00"));
        form.setFeeAmount(new BigDecimal("2000.00"));
        form.setEmdAmount(new BigDecimal("30000.00"));
        form.setInvitingOfficer("Chief Engineer");
        form.setInvitingOfficerAddress("PWD HQ");

        LocalDateTime now = LocalDateTime.now().plusDays(2);
        form.setPublishingDate(now);
        form.setDocDownloadStartDate(now.plusHours(2));
        form.setBidSubmissionStartDate(now.plusDays(1));
        form.setBidSubmissionEndDate(now.plusDays(15));
        form.setBidOpenDate(now.plusDays(16));

        form.getDocuments().add(new PreTenderDocumentForm(
                100L, "NITDOC", "notice.pdf", "NIT Notice", ".pdf", 50000L, 1
        ));
        return form;
    }

    @Test
    @DisplayName("End-to-End Vertical Slice: Create, Validate, Map, Push to GePNIC, Persist Transaction and Audit Trail")
    void testPreTenderPushEndToEnd() {
        // 1. Mock remote GePNIC response
        String sampleSuccessResponse = "{\"responseCode\":1,\"responseMessage\":\"{\\\"responseCode\\\":101,\\\"responseMessage\\\":\\\"Inserted Successfully\\\"}\"}";
        when(gepnicClient.postJson(anyString(), any(), anyString())).thenReturn(sampleSuccessResponse);

        // 2. Create PreTender
        PreTenderForm form = createValidForm("FILE-E2E-001", "REF-E2E-001", 501L);
        PreTenderResponseForm created = preTenderService.createPreTender(form);
        assertNotNull(created.getId());
        assertEquals(IntegrationStatus.DRAFT, created.getStatus());

        // 3. Submit PreTender to GePNIC
        String correlationId = "CORR-E2E-TEST-001";
        String idempotencyKey = "IDEMP-E2E-TEST-001";
        IntegrationResult result = preTenderService.submitPreTender(created.getId(), PortalCode.GEPNIC, correlationId, idempotencyKey);

        assertNotNull(result);
        assertEquals(IntegrationStatus.ACCEPTED, result.getStatus());
        assertTrue(result.isRemoteAccepted());
        assertEquals("REF-E2E-001", result.getExternalReference());

        // 4. Verify Transaction persistence
        IntegrationTransaction txn = transactionService.getTransaction(result.getTransactionId());
        assertEquals(IntegrationStatus.ACCEPTED, txn.getStatus());
        assertEquals(correlationId, txn.getCorrelationId());
        assertEquals("REF-E2E-001", txn.getExternalReference());

        // 5. Verify Immutable Audit Trail
        List<IntegrationAuditEvent> auditEvents = auditService.getAuditTrail(txn.getId());
        assertFalse(auditEvents.isEmpty());
        assertTrue(auditEvents.stream().anyMatch(e -> e.getToStatus() == IntegrationStatus.ACCEPTED));

        // 6. Test Idempotency: submitting same idempotency key does not re-submit
        IntegrationResult duplicateResult = preTenderService.submitPreTender(created.getId(), PortalCode.GEPNIC, "CORR-DUP-001", idempotencyKey);
        assertEquals(result.getTransactionId(), duplicateResult.getTransactionId());
        assertEquals(IntegrationStatus.ACCEPTED, duplicateResult.getStatus());
    }

    @Test
    @DisplayName("Batch submission processes each work independently")
    void testBatchSubmission() {
        String sampleSuccessResponse = "{\"responseCode\":1,\"responseMessage\":\"{\\\"responseCode\\\":101,\\\"responseMessage\\\":\\\"Inserted Successfully\\\"}\"}";
        when(gepnicClient.postJson(anyString(), any(), anyString())).thenReturn(sampleSuccessResponse);

        // Create two works
        preTenderService.createPreTender(createValidForm("FILE-B1", "REF-B1", 801L));
        preTenderService.createPreTender(createValidForm("FILE-B2", "REF-B2", 802L));

        PreTenderBatchForm batchForm = new PreTenderBatchForm(PortalCode.GEPNIC, IntegrationOperation.PRE_TENDER, List.of(801L, 802L, 999L));
        PreTenderBatchResponseForm batchResponse = preTenderService.submitBatch(batchForm, "BATCH-CORR-01");

        assertNotNull(batchResponse);
        assertEquals(3, batchResponse.getTotalSubmitted());
        assertEquals(2, batchResponse.getSuccessfulCount());
        assertEquals(1, batchResponse.getFailedCount()); // 999L has no tender
    }
}

