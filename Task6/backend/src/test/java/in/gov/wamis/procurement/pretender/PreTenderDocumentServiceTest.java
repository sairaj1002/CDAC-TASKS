package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.exception.BusinessValidationException;
import in.gov.wamis.integration.gepnic.client.GePNICClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PreTenderDocumentServiceTest {

    @Autowired
    private PreTenderService preTenderService;

    @Autowired
    private PreTenderDocumentService documentService;

    @MockitoBean
    private GePNICClient gepnicClient;

    private PreTenderForm form(String fileNo) {
        PreTenderForm form = new PreTenderForm();
        form.setUniqueFileNo(fileNo);
        form.setTenderReferenceNo("REF-" + fileNo);
        form.setOfficeId(10L);
        form.setWorkId(901L);
        form.setOrgId(2297L);
        form.setTenderTitle("Boundary wall");
        form.setTenderValue(new BigDecimal("500000"));
        form.setInvitingOfficer("Executive Engineer");
        form.setInvitingOfficerAddress("PWD Division");
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        form.setPublishingDate(start);
        form.setDocDownloadStartDate(start.plusHours(1));
        form.setBidSubmissionStartDate(start.plusDays(1));
        form.setBidSubmissionEndDate(start.plusDays(10));
        form.setBidOpenDate(start.plusDays(11));
        form.getDocuments().add(new PreTenderDocumentForm(1L, "NITDOC", "notice.pdf", "NIT", ".pdf", 1000L, 1));
        return form;
    }

    @Test
    @DisplayName("A file uploaded to a tender is listed, can be downloaded and removed")
    void uploadListDownloadDelete() {
        PreTenderResponseForm tender = preTenderService.createPreTender(form("FILE-DOC-1"));
        byte[] content = "protected boq".getBytes(StandardCharsets.UTF_8);

        PreTenderDocumentResponseForm uploaded = documentService.upload(tender.getId(), "BOQDOC",
                new MockMultipartFile("file", "Protected_BOQ.xlsx", "application/vnd.ms-excel", content));

        assertEquals("BOQDOC", uploaded.getDocCode());
        assertEquals(".xlsx", uploaded.getDocMimeType());
        assertEquals(2L, uploaded.getDocUniqueId()); // after the NIT document of the form
        assertTrue(uploaded.getUploaded());

        List<PreTenderDocumentResponseForm> docs = documentService.getDocuments(tender.getId());
        assertEquals(2, docs.size());
        assertFalse(docs.get(0).getUploaded()); // the NIT row from the form has no file
        assertTrue(docs.get(1).getUploaded());

        PreTenderDocumentService.DocumentDownload download = documentService.download(tender.getId(), uploaded.getId());
        assertEquals("Protected_BOQ.xlsx", download.fileName());
        assertArrayEquals(content, download.content());

        documentService.delete(tender.getId(), uploaded.getId());
        assertEquals(1, documentService.getDocuments(tender.getId()).size());
    }

    @Test
    @DisplayName("Updating a tender keeps its documents and their files instead of adding them again")
    void updateDoesNotDuplicateDocuments() {
        PreTenderForm form = form("FILE-DOC-2");
        PreTenderResponseForm tender = preTenderService.createPreTender(form);
        PreTenderDocumentResponseForm uploaded = documentService.upload(tender.getId(), "BOQDOC",
                new MockMultipartFile("file", "boq.xls", "application/vnd.ms-excel", new byte[]{1, 2, 3}));

        // the edit form sends back the documents it shows: the NIT row and the uploaded BOQ
        form.getDocuments().add(new PreTenderDocumentForm(uploaded.getDocUniqueId(), "BOQDOC", "boq.xls", "BOQ", ".xls", 3L, 2));
        preTenderService.updatePreTender(tender.getId(), form);
        preTenderService.updatePreTender(tender.getId(), form);

        List<PreTenderDocumentResponseForm> docs = documentService.getDocuments(tender.getId());
        assertEquals(2, docs.size());
        assertTrue(docs.stream().anyMatch(d -> d.getId().equals(uploaded.getId()) && d.getUploaded()));
    }

    @Test
    @DisplayName("Unknown categories and file types are refused")
    void refusesUnknownCategoryAndType() {
        PreTenderResponseForm tender = preTenderService.createPreTender(form("FILE-DOC-3"));
        MockMultipartFile pdf = new MockMultipartFile("file", "a.pdf", "application/pdf", new byte[]{1});
        MockMultipartFile exe = new MockMultipartFile("file", "a.exe", "application/octet-stream", new byte[]{1});

        assertThrows(BusinessValidationException.class, () -> documentService.upload(tender.getId(), "NOPE", pdf));
        assertThrows(BusinessValidationException.class, () -> documentService.upload(tender.getId(), "NITDOC", exe));
    }
}
