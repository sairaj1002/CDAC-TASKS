package com.cdac.egov.integration.gepnic.entity;

import com.cdac.egov.integration.gepnic.enums.IntegrationOperation;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Audit and state-tracking entity for every WAMIS <-> GePNIC integration operation.
 */
@Entity
@Table(name = "gepnic_integration_transaction", indexes = {
        @Index(name = "idx_txn_transaction_id", columnList = "transactionId", unique = true),
        @Index(name = "idx_txn_correlation_id", columnList = "correlationId"),
        @Index(name = "idx_txn_status", columnList = "status"),
        @Index(name = "idx_txn_operation", columnList = "operation"),
        @Index(name = "idx_txn_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 64)
    private String transactionId;

    @Column(name = "correlation_id", nullable = false, length = 128)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false, length = 64)
    private IntegrationOperation operation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private IntegrationStatus status;

    @Column(name = "source_system", nullable = false, length = 32)
    private String sourceSystem;

    @Column(name = "target_system", nullable = false, length = 32)
    private String targetSystem;

    @Column(name = "office_code", length = 64)
    private String officeCode;

    @Column(name = "endpoint_url", length = 512)
    private String endpointUrl;

    @Column(name = "request_payload_sanitized", columnDefinition = "TEXT")
    private String requestPayloadSanitized;

    @Column(name = "response_payload_sanitized", columnDefinition = "TEXT")
    private String responsePayloadSanitized;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "response_message", length = 1024)
    private String responseMessage;

    @Column(name = "records_processed")
    private Integer recordsProcessed;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "error_message", length = 2048)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
