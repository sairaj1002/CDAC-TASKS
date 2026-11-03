package com.cdac.egov.wms.work.subwork.road.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.cdac.egov.wms.work.subwork.road.constant.RoadConstant;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Audited
@Table(name = "road_component")
@Getter
@Setter
@NoArgsConstructor
public class RoadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scope_id")
    private Long scopeId;

    // Office, work and tenant identifiers
    @Positive
    @Column(name = "office_id")
    private Integer officeId;

    @Positive
    @Column(name = "work_id")
    private Integer workId;

    @Positive
    @Column(name = "tenant_id")
    private Integer tenantId;

    // Audit revision information
    @Enumerated(EnumType.STRING)
    @Column(name = "revision_type", length = 10)
    private AuditRevisionType revisionType;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "revision_timestamp")
    private Instant revisionTimestamp;

    // Nature of Work (stores stable option code)
    @NotBlank(message = "Nature of work is required.")
    @Column(name = "nature_of_work", nullable = false, length = 50)
    private String natureOfWork;

    // Type of Proposed Road (stores stable option code)
    @NotBlank(message = "Type of proposed road is required.")
    @Column(name = "proposed_road_type", nullable = false, length = 50)
    private String proposedRoadType;

    // Road length
    @NotNull(message = "Road length is required.")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Road length must be greater than zero.")
    @Digits(integer = 10, fraction = 3)
    @Column(name = "road_length", precision = 13, scale = 3)
    private BigDecimal length;

    // Road width
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Road width must be greater than zero.")
    @Digits(integer = 10, fraction = 3)
    @Column(name = "road_width", precision = 13, scale = 3)
    private BigDecimal width;

    @NotBlank
    @Column(name = "cd_works", nullable = false, length = 10)
    private String cdWorks;

    @NotNull
    @jakarta.validation.constraints.Min(0)
    @Column(name = "number_of_structures", nullable = false)
    private Integer numberOfStructures;

    @NotBlank
    @Column(name = "repair_maintenance_type", nullable = false, length = 50)
    private String repairMaintenanceType;

    @PrePersist
    private void setCreationTimestamps() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;
        revisionTimestamp = now;
        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    private void setUpdateTimestamps() {
        Instant now = Instant.now();

        updatedAt = now;
        revisionTimestamp = now;
    }

    public void setRevisionType(AuditRevisionType revisionType) {
        this.revisionType = revisionType;
    }
}

