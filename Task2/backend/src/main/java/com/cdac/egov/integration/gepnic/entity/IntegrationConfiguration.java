package com.cdac.egov.integration.gepnic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persisted environment-level integration configuration and certificates metadata.
 */
@Entity
@Table(name = "gepnic_integration_configuration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "environment_name", nullable = false, length = 32)
    private String environmentName;

    @Column(name = "base_url", nullable = false, length = 512)
    private String baseUrl;

    @Column(name = "unique_user_id", nullable = false, length = 128)
    private String uniqueUserId;

    @Column(name = "key_id", nullable = false, length = 128)
    private String keyId;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "connect_timeout_ms", nullable = false)
    @Builder.Default
    private Integer connectTimeoutMs = 10000;

    @Column(name = "read_timeout_ms", nullable = false)
    @Builder.Default
    private Integer readTimeoutMs = 30000;

    @Column(name = "updated_by", length = 64)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
    }
}
