package com.cdac.road.forms;

import java.math.BigDecimal;

import com.cdac.road.entities.CdWorkType;
import com.cdac.road.entities.NatureOfWork;
import com.cdac.road.entities.ProposedRoadType;
import com.cdac.road.entities.RepairMaintenanceType;
import com.cdac.road.entities.WorkPlanType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoadComponentForm {

    @NotNull(message = "Nature of work is required.")
    private NatureOfWork natureOfWork;

    @NotNull(message = "Type of proposed road is required.")
    private ProposedRoadType proposedRoadType;

    @NotNull(message = "Road length is required.")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Road length must be greater than zero.")
    @Digits(integer = 10, fraction = 3)
    private BigDecimal length;

    @DecimalMin(value = "0.0", inclusive = false,
            message = "Road width must be greater than zero.")
    @Digits(integer = 10, fraction = 3)
    private BigDecimal width;

    @NotNull(message = "Work plan type is required.")
    private WorkPlanType workPlanType;

    @NotNull(message = "Type of CD works is required.")
    private CdWorkType cdWorkType;

    private RepairMaintenanceType repairMaintenanceType;

    @Positive(message = "Office ID must be positive.")
    private Integer officeId;

    @Positive(message = "Work ID must be positive.")
    private Integer workId;

    @AssertTrue(message =
            "Repair/Maintenance Type is required for Non-Plan work "
            + "and must be empty for Plan work.")
    public boolean isRepairMaintenanceTypeValid() {

        if (workPlanType == null) {
            return true;
        }

        if (workPlanType == WorkPlanType.NON_PLAN) {
            return repairMaintenanceType != null;
        }

        return repairMaintenanceType == null;
    }
}