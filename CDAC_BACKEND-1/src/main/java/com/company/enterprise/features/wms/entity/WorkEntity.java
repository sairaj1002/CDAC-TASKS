package com.company.enterprise.features.wms.entity;

import java.time.LocalDate;

import com.company.enterprise.core.auditing.AuditableBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "works_details")
@Getter
@Setter
@NoArgsConstructor
public class WorkEntity extends AuditableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_reg_no", nullable = false, updatable = false)
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
}
