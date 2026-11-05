package in.gov.wamis.procurement.pretender;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * The uploaded file of a Pre-Tender document, kept apart from the document row so that listing
 * tenders and documents never loads file contents. One row per document (same id).
 */
@Entity
@Table(name = "wamis_pretender_document_files")
public class PreTenderDocumentFile {

    @Id
    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "content_type", length = 150)
    private String contentType;

    // bytea in PostgreSQL (schema.sql); files are limited by spring.servlet.multipart.max-file-size
    @Column(name = "content", nullable = false, columnDefinition = "bytea")
    private byte[] content;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public PreTenderDocumentFile() {
    }

    public PreTenderDocumentFile(Long documentId, String contentType, byte[] content) {
        this.documentId = documentId;
        this.contentType = contentType;
        this.content = content;
        this.uploadedAt = LocalDateTime.now();
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
