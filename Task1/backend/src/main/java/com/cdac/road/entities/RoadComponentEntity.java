package com.cdac.road.entities;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.envers.Audited;

import com.cdac.road.entities.CdWorkType;
import com.cdac.road.entities.NatureOfWork;
import com.cdac.road.entities.ProposedRoadType;
import com.cdac.road.entities.RepairMaintenanceType;
import com.cdac.road.entities.WorkPlanType;

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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
public class RoadComponentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Office and work identifiers
    @Positive
    @Column(name = "office_id")
    private Integer officeId;

    @Positive
    @Column(name = "work_id")
    private Integer workId;

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

    // Nature of Work
    @NotNull(message = "Nature of work is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "nature_of_work", nullable = false, length = 30)
    private NatureOfWork natureOfWork;

    // Type of Proposed Road
    @NotNull(message = "Type of proposed road is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "proposed_road_type", nullable = false, length = 30)
    private ProposedRoadType proposedRoadType;

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

    // Plan / Non-Plan classification
    @NotNull(message = "Work plan type is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "work_plan_type", nullable = false, length = 10)
    private WorkPlanType workPlanType;

    // Type of CD Works
    @NotNull(message = "Type of CD works is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "cd_work_type", nullable = false, length = 40)
    private CdWorkType cdWorkType;

    // Repair/Maintenance Type - applicable only to Non-Plan work
    @Enumerated(EnumType.STRING)
    @Column(name = "repair_maintenance_type", length = 30)
    private RepairMaintenanceType repairMaintenanceType;

    @PrePersist
    private void setCreationTimestamps() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;
        revisionTimestamp = now;
    }

    @PreUpdate
    private void setUpdateTimestamps() {
        Instant now = Instant.now();

        updatedAt = now;
        revisionTimestamp = now;
    }

    /**
     * Repair/Maintenance Type is mandatory for Non-Plan work
     * and must be empty for Plan work.
     */
    @AssertTrue(message =
            "Repair/Maintenance Type must be selected for Non-Plan work "
            + "and must be empty for Plan work.")
    public boolean isRepairMaintenanceTypeValid() {

        if (workPlanType == null) {
            return true; // @NotNull validates this field separately
        }

        if (workPlanType == WorkPlanType.NON_PLAN) {
            return repairMaintenanceType != null;
        }

        return repairMaintenanceType == null;
    }

    public void setRevisionType(AuditRevisionType revisionType) {
        this.revisionType = revisionType;
    }
    
}