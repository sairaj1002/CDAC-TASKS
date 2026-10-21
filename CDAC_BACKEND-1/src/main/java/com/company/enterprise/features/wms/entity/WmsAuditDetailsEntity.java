package com.company.enterprise.features.wms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.enterprise.core.auditing.AuditAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "wms_audit_details",
        indexes = @Index(name = "idx_wms_audit_work_revision", columnList = "work_reg_no, revision_at"),
        uniqueConstraints = @UniqueConstraint(
                name = "uk_wms_audit_legacy_revision",
                columnNames = {"legacy_revision", "work_reg_no"}))
@Getter
@Setter
@NoArgsConstructor
public class WmsAuditDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id", nullable = false, updatable = false)
    private Long auditId;

    @Column(name = "legacy_revision")
    private Long legacyRevision;

    @Column(name = "revision_at", nullable = false)
    private LocalDateTime revisionAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "revision_type", nullable = false, length = 16)
    private AuditAction revisionType;

    @Column(name = "work_reg_no", nullable = false)
    private Long workRegNo;

    @Column(name = "work_details", nullable = false, length = 500)
    private String workDetails;

    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    @Column(name = "contractor_name", nullable = false, length = 150)
    private String contractorName;

    @Column(name = "stipulated_start_date", nullable = false)
    private LocalDate stipulatedStartDate;

    @Column(name = "stipulated_end_date", nullable = false)
    private LocalDate stipulatedEndDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "modified_by", length = 255)
    private String modifiedBy;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", length = 255)
    private String userName;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "mac_address", length = 32)
    private String macAddress;
}
