package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.constants.ApiConstants;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Documents of a saved Pre-Tender: upload a file under a document category, list them, download and remove.
 */
@RestController
@RequestMapping(ApiConstants.PRE_TENDERS + "/{preTenderId}/documents")
public class PreTenderDocumentController {

    private final PreTenderDocumentService documentService;

    public PreTenderDocumentController(PreTenderDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<List<PreTenderDocumentResponseForm>> getDocuments(@PathVariable Long preTenderId) {
        return ResponseEntity.ok(documentService.getDocuments(preTenderId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PreTenderDocumentResponseForm> upload(@PathVariable Long preTenderId,
                                                                @RequestParam String docCode,
                                                                @RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.upload(preTenderId, docCode, file));
    }

    @GetMapping("/{documentId}/file")
    public ResponseEntity<byte[]> download(@PathVariable Long preTenderId, @PathVariable Long documentId) {
        PreTenderDocumentService.DocumentDownload download = documentService.download(preTenderId, documentId);
        MediaType type = download.contentType() != null
                ? MediaType.parseMediaType(download.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(download.fileName(), StandardCharsets.UTF_8).build().toString())
                .body(download.content());
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long preTenderId, @PathVariable Long documentId) {
        documentService.delete(preTenderId, documentId);
        return ResponseEntity.ok(Collections.singletonMap("status", "DELETED"));
    }
}
