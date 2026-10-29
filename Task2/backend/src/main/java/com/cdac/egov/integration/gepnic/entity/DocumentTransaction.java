package com.cdac.egov.integration.gepnic.entity;

import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tracks document file uploads and synchronizations against tender records.
 */
@Entity
@Table(name = "gepnic_document_transaction", indexes = {
        @Index(name = "idx_doc_unique_file_no", columnList = "uniqueFileNo"),
        @Index(name = "idx_doc_tender_reference", columnList = "tenderReference"),
        @Index(name = "idx_doc_upload_status", columnList = "uploadStatus")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_file_no", nullable = false, length = 128)
    private String uniqueFileNo;

    @Column(name = "doc_unique_id", nullable = false, length = 64)
    private String docUniqueId;

    @Column(name = "tender_reference", nullable = false, length = 128)
    private String tenderReference;

    @Column(name = "file_name", nullable = false, length = 256)
    private String fileName;

    @Column(name = "doc_code", length = 64)
    private String docCode;

    @Column(name = "doc_type", length = 32)
    private String docType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false, length = 32)
    private IntegrationStatus uploadStatus;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "response_message", length = 1024)
    private String responseMessage;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }
}
