package com.cdac.egov.integration.gepnic.service.document;

import com.cdac.egov.integration.gepnic.entity.DocumentTransaction;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import com.cdac.egov.integration.gepnic.forms.document.DocumentResponseForm;
import com.cdac.egov.integration.gepnic.forms.document.DocumentStatusForm;
import com.cdac.egov.integration.gepnic.forms.document.DocumentUploadForm;
import com.cdac.egov.integration.gepnic.repository.DocumentTransactionRepository;
import com.cdac.egov.integration.gepnic.security.SecretProvider;
import com.cdac.egov.integration.gepnic.service.integration.GePNICHttpClient;
import com.cdac.egov.integration.gepnic.service.integration.GePNICResponseService;
import com.cdac.egov.integration.gepnic.service.json.GePNICJsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles tender document upload and status verification according to the GePNIC file upload protocol.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIntegrationService {

    private final GePNICHttpClient httpClient;
    private final GePNICResponseService responseService;
    private final DocumentTransactionRepository documentRepo;
    private final SecretProvider secretProvider;

    /**
     * Uploads a tender document file to GePNIC.
     */
    @Transactional
    public DocumentResponseForm uploadDocument(DocumentUploadForm form, MultipartFile file) {
        log.info("Uploading document {} for fileNo: {}, docUniqueId: {}", form.getFileName(), form.getUniqueFileNo(), form.getDocUniqueId());

        try {
            byte[] fileBytes = file != null ? file.getBytes() : new byte[1024];
            String fileName = file != null ? file.getOriginalFilename() : form.getFileName();
            long fileSize = file != null ? file.getSize() : (long) fileBytes.length;

            String userId = secretProvider.getUniqueUserId();
            ResponseEntity<String> response = httpClient.uploadMultipartDocument(
                    userId,
                    form.getUniqueFileNo(),
                    form.getDocUniqueId(),
                    fileName,
                    fileBytes
            );

            GePNICJsonParser.ParsedResponse parsed = responseService.processResponse(response.getBody());
            IntegrationStatus status = parsed.isSuccess() ? IntegrationStatus.SUCCESS : IntegrationStatus.FAILED;

            DocumentTransaction docTxn = DocumentTransaction.builder()
                    .uniqueFileNo(form.getUniqueFileNo())
                    .docUniqueId(form.getDocUniqueId())
                    .tenderReference(form.getTenderReference())
                    .fileName(fileName)
                    .docCode(form.getDocCode())
                    .docType(form.getDocType())
                    .fileSizeBytes(fileSize)
                    .uploadStatus(status)
                    .responseCode(parsed.outerCode())
                    .responseMessage(parsed.message())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            documentRepo.save(docTxn);

            return DocumentResponseForm.builder()
                    .uniqueFileNo(form.getUniqueFileNo())
                    .docUniqueId(form.getDocUniqueId())
                    .tenderReference(form.getTenderReference())
                    .fileName(fileName)
                    .fileSizeBytes(fileSize)
                    .status(status)
                    .responseCode(parsed.outerCode())
                    .responseMessage(parsed.message())
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Document upload failed: {}", e.getMessage());
            return DocumentResponseForm.builder()
                    .uniqueFileNo(form.getUniqueFileNo())
                    .docUniqueId(form.getDocUniqueId())
                    .tenderReference(form.getTenderReference())
                    .fileName(form.getFileName())
                    .status(IntegrationStatus.FAILED)
                    .responseCode(0)
                    .responseMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Checks upload status for a tender's documents.
     */
    public List<DocumentTransaction> getDocumentTransactions(String tenderReference) {
        return documentRepo.findByTenderReference(tenderReference);
    }

    /**
     * Checks document status.
     */
    public DocumentResponseForm checkDocumentStatus(DocumentStatusForm form) {
        return documentRepo.findByUniqueFileNoAndDocUniqueId(form.getUniqueFileNo(), form.getDocUniqueId())
                .map(txn -> DocumentResponseForm.builder()
                        .uniqueFileNo(txn.getUniqueFileNo())
                        .docUniqueId(txn.getDocUniqueId())
                        .tenderReference(txn.getTenderReference())
                        .fileName(txn.getFileName())
                        .fileSizeBytes(txn.getFileSizeBytes())
                        .status(txn.getUploadStatus())
                        .responseCode(txn.getResponseCode())
                        .responseMessage(txn.getResponseMessage())
                        .timestamp(txn.getUploadedAt())
                        .build())
                .orElse(DocumentResponseForm.builder()
                        .uniqueFileNo(form.getUniqueFileNo())
                        .docUniqueId(form.getDocUniqueId())
                        .tenderReference(form.getTenderReference())
                        .status(IntegrationStatus.PENDING)
                        .responseMessage("No document record found for given file and doc ID")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
