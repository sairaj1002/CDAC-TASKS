package in.gov.wamis.procurement.pretender;

import in.gov.wamis.common.exception.BusinessValidationException;
import in.gov.wamis.common.exception.ResourceNotFoundException;
import in.gov.wamis.master.form.DropdownMasterResponseForm;
import in.gov.wamis.master.service.DropdownMasterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Documents of a saved Pre-Tender with their files: upload, list, download and remove.
 * The document row carries the details sent to GePNIC; the file itself is kept in PreTenderDocumentFile.
 */
@Service
public class PreTenderDocumentService {

    private static final String DOC_CODE_CATEGORY = "DOC_CODE";
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(".pdf", ".doc", ".docx", ".xls", ".xlsx", ".dwg", ".dxf", ".zip", ".jpg", ".jpeg", ".png");

    private final PreTenderRepository preTenderRepository;
    private final PreTenderDocumentFileRepository fileRepository;
    private final DropdownMasterService dropdownMasterService;

    public PreTenderDocumentService(PreTenderRepository preTenderRepository,
                                    PreTenderDocumentFileRepository fileRepository,
                                    DropdownMasterService dropdownMasterService) {
        this.preTenderRepository = preTenderRepository;
        this.fileRepository = fileRepository;
        this.dropdownMasterService = dropdownMasterService;
    }

    @Transactional(readOnly = true)
    public List<PreTenderDocumentResponseForm> getDocuments(Long preTenderId) {
        return findPreTender(preTenderId).getDocuments().stream()
                .map(doc -> PreTenderDocumentResponseForm.fromEntity(doc, fileRepository.existsById(doc.getId())))
                .toList();
    }

    @Transactional
    public PreTenderDocumentResponseForm upload(Long preTenderId, String docCode, MultipartFile file) {
        PreTender pt = findPreTender(preTenderId);
        DropdownMasterResponseForm category = dropdownMasterService.getByCategory(DOC_CODE_CATEGORY).stream()
                .filter(c -> c.getCode().equals(docCode))
                .findFirst()
                .orElseThrow(() -> new BusinessValidationException("Unknown document category: " + docCode));
        if (file == null || file.isEmpty()) {
            throw new BusinessValidationException("Please choose a file to upload.");
        }
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        int dot = fileName.lastIndexOf('.');
        String extension = dot > 0 ? fileName.substring(dot).toLowerCase(Locale.ROOT) : "";
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessValidationException("File type not allowed: " + fileName + ". Allowed: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        byte[] content = readContent(file);
        long nextUniqueId = pt.getDocuments().stream().mapToLong(PreTenderDocument::getDocUniqueId).max().orElse(0) + 1;
        PreTenderDocument doc = new PreTenderDocument(nextUniqueId, category.getCode(), fileName, category.getDisplayName(),
                extension, (long) content.length, pt.getDocuments().size() + 1);
        doc.setUploaded(true);
        doc.setFileHash(sha256(content));
        pt.addDocument(doc);
        // the tender is managed here: flushing persists this document object and gives it its id
        // (save() would merge, and the id would go to a copy)
        preTenderRepository.flush();

        fileRepository.save(new PreTenderDocumentFile(doc.getId(), file.getContentType(), content));
        return PreTenderDocumentResponseForm.fromEntity(doc, true);
    }

    @Transactional(readOnly = true)
    public DocumentDownload download(Long preTenderId, Long documentId) {
        PreTenderDocument doc = findDocument(findPreTender(preTenderId), documentId);
        PreTenderDocumentFile file = fileRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("File of document", documentId));
        return new DocumentDownload(doc.getDocName(), file.getContentType(), file.getContent());
    }

    @Transactional
    public void delete(Long preTenderId, Long documentId) {
        PreTender pt = findPreTender(preTenderId);
        PreTenderDocument doc = findDocument(pt, documentId);
        fileRepository.deleteById(documentId);
        pt.getDocuments().remove(doc);
        // priorities follow the order of the remaining documents
        for (int i = 0; i < pt.getDocuments().size(); i++) {
            pt.getDocuments().get(i).setDocPriority(i + 1);
        }
    }

    private PreTender findPreTender(Long id) {
        return preTenderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PreTender", id));
    }

    private static PreTenderDocument findDocument(PreTender pt, Long documentId) {
        return pt.getDocuments().stream()
                .filter(d -> documentId.equals(d.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PreTender document", documentId));
    }

    private static byte[] readContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BusinessValidationException("The file could not be read: " + e.getMessage());
        }
    }

    private static String sha256(byte[] content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** A stored file ready to be sent to the browser. */
    public record DocumentDownload(String fileName, String contentType, byte[] content) {
    }
}
