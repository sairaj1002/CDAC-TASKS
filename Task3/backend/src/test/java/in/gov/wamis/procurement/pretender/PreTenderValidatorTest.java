package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.exception.BusinessValidationException;
import in.gov.wamis.common.security.SecurityContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class PreTenderValidatorTest {

    private SecurityContextHelper securityContextHelper;
    private PreTenderValidator validator;

    @BeforeEach
    void setUp() {
        securityContextHelper = Mockito.mock(SecurityContextHelper.class);
        when(securityContextHelper.isOfficeAuthorized(anyLong())).thenReturn(true);
        when(securityContextHelper.isWorkAuthorized(anyLong())).thenReturn(true);
        validator = new PreTenderValidator(securityContextHelper);
    }

    private PreTender createValidPreTender() {
        PreTender pt = new PreTender();
        pt.setUniqueFileNo("WAMIS-2026-F01");
        pt.setTenderReferenceNo("WAMIS-TND-001");
        pt.setOfficeId(10L);
        pt.setWorkId(101L);
        pt.setOrgId(2297L);
        pt.setTenderTitle("Improvement of Highway Section A");
        pt.setTenderValue(new BigDecimal("500000.00"));
        pt.setFeeAmount(new BigDecimal("1000.00"));
        pt.setEmdAmount(new BigDecimal("10000.00"));
        pt.setInvitingOfficer("Executive Engineer");
        pt.setInvitingOfficerAddress("Division Office Pune");

        LocalDateTime now = LocalDateTime.now().plusDays(1);
        pt.setPublishingDate(now);
        pt.setDocDownloadStartDate(now.plusHours(1));
        pt.setBidSubmissionStartDate(now.plusDays(1));
        pt.setBidSubmissionEndDate(now.plusDays(10));
        pt.setBidOpenDate(now.plusDays(11));

        PreTenderDocument doc = new PreTenderDocument(
                1L, "NITDOC", "tender-notice.pdf", "NIT Document", ".pdf", 25000L, 1
        );
        pt.addDocument(doc);
        return pt;
    }

    @Test
    @DisplayName("Valid PreTender passes business validation")
    void testValidPreTenderPasses() {
        PreTender pt = createValidPreTender();
        assertDoesNotThrow(() -> validator.validate(pt));
    }

    @Test
    @DisplayName("Validation fails when officeId is missing")
    void testMissingOfficeId() {
        PreTender pt = createValidPreTender();
        pt.setOfficeId(null);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> validator.validate(pt));
        assertTrue(ex.getErrors().containsKey("officeId"));
    }

    @Test
    @DisplayName("Validation fails when workId is missing")
    void testMissingWorkId() {
        PreTender pt = createValidPreTender();
        pt.setWorkId(null);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> validator.validate(pt));
        assertTrue(ex.getErrors().containsKey("workId"));
    }

    @Test
    @DisplayName("Validation fails when tender value is zero or negative")
    void testInvalidTenderValue() {
        PreTender pt = createValidPreTender();
        pt.setTenderValue(BigDecimal.ZERO);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> validator.validate(pt));
        assertTrue(ex.getErrors().containsKey("tenderValue"));
    }

    @Test
    @DisplayName("Validation fails when critical dates violate chronological invariants")
    void testInvalidDatesChronology() {
        PreTender pt = createValidPreTender();
        // Bid end is before bid start
        pt.setBidSubmissionEndDate(pt.getBidSubmissionStartDate().minusDays(1));

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> validator.validate(pt));
        assertTrue(ex.getErrors().containsKey("bidSubmissionEndDate"));
    }

    @Test
    @DisplayName("Validation fails when no NIT document is attached")
    void testMissingNITDocument() {
        PreTender pt = createValidPreTender();
        pt.getDocuments().clear();
        PreTenderDocument doc = new PreTenderDocument(
                2L, "BOQDOC", "boq.xls", "BOQ", ".xls", 15000L, 2
        );
        pt.addDocument(doc);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> validator.validate(pt));
        assertTrue(ex.getErrors().containsKey("documents"));
    }

    @Test
    @DisplayName("Validation fails when user is not authorized for the office")
    void testUnauthorizedOffice() {
        when(securityContextHelper.isOfficeAuthorized(10L)).thenReturn(false);
        PreTender pt = createValidPreTender();

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> validator.validate(pt));
        assertTrue(ex.getErrors().containsKey("officeId"));
        assertTrue(ex.getErrors().get("officeId").contains("not authorized"));
    }
}

