package in.gov.wamis.procurement.pretender;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Pre-Tender Document entity representing uploaded/attached tender notices, BOQ, and drawings.
 */
@Entity
@Table(name = "wamis_pretender_documents")
public class PreTenderDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pretender_id", nullable = false)
    private PreTender preTender;

    @Column(name = "doc_unique_id", nullable = false)
    private Long docUniqueId;

    @Column(name = "doc_code", length = 50, nullable = false)
    private String docCode; // e.g. NITDOC, BOQDOC, ADDLDOC

    @Column(name = "doc_name", length = 255, nullable = false)
    private String docName; // e.g. tender-notice.pdf

    @Column(name = "doc_description", length = 1000)
    private String docDescription;

    @Column(name = "doc_mime_type", length = 50, nullable = false)
    private String docMimeType; // e.g. .pdf, .xls

    @Column(name = "doc_size", nullable = false)
    private Long docSize;

    @Column(name = "doc_priority", nullable = false)
    private Integer docPriority;

    @Column(name = "uploaded", nullable = false)
    private Boolean uploaded = false;

    @Column(name = "file_hash", length = 128)
    private String fileHash;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public PreTenderDocument() {
    }

    public PreTenderDocument(Long docUniqueId, String docCode, String docName, String docDescription,
                             String docMimeType, Long docSize, Integer docPriority) {
        this.docUniqueId = docUniqueId;
        this.docCode = docCode;
        this.docName = docName;
        this.docDescription = docDescription;
        this.docMimeType = docMimeType;
        this.docSize = docSize;
        this.docPriority = docPriority;
        this.uploaded = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PreTender getPreTender() {
        return preTender;
    }

    public void setPreTender(PreTender preTender) {
        this.preTender = preTender;
    }

    public Long getDocUniqueId() {
        return docUniqueId;
    }

    public void setDocUniqueId(Long docUniqueId) {
        this.docUniqueId = docUniqueId;
    }

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocDescription() {
        return docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }

    public String getDocMimeType() {
        return docMimeType;
    }

    public void setDocMimeType(String docMimeType) {
        this.docMimeType = docMimeType;
    }

    public Long getDocSize() {
        return docSize;
    }

    public void setDocSize(Long docSize) {
        this.docSize = docSize;
    }

    public Integer getDocPriority() {
        return docPriority;
    }

    public void setDocPriority(Integer docPriority) {
        this.docPriority = docPriority;
    }

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

