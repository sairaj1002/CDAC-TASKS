package com.cdac.scope.forms;

import java.math.BigDecimal;
import com.cdac.scope.constant.RoadConstant;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoadForm {

    @NotBlank(message = RoadConstant.Validation.NATURE_OF_WORK_REQUIRED)
    @Size(max = RoadConstant.Validation.MAX_LENGTH_50, message = RoadConstant.Validation.NATURE_OF_WORK_MAX)
    private String natureOfWork;

    @NotBlank(message = RoadConstant.Validation.PROPOSED_ROAD_TYPE_REQUIRED)
    @Size(max = RoadConstant.Validation.MAX_LENGTH_50, message = RoadConstant.Validation.PROPOSED_ROAD_TYPE_MAX)
    private String proposedRoadType;

    @NotNull(message = RoadConstant.Validation.LENGTH_REQUIRED)
    @DecimalMin(value = RoadConstant.Validation.DECIMAL_MIN_ZERO, inclusive = RoadConstant.Validation.DECIMAL_MIN_INCLUSIVE,
            message = RoadConstant.Validation.LENGTH_MIN)
    @Digits(integer = RoadConstant.Validation.DIGITS_INTEGER, fraction = RoadConstant.Validation.DIGITS_FRACTION)
    private BigDecimal length;

    @DecimalMin(value = RoadConstant.Validation.DECIMAL_MIN_ZERO, inclusive = RoadConstant.Validation.DECIMAL_MIN_INCLUSIVE,
            message = RoadConstant.Validation.WIDTH_MIN)
    @Digits(integer = RoadConstant.Validation.DIGITS_INTEGER, fraction = RoadConstant.Validation.DIGITS_FRACTION)
    private BigDecimal width;

    @NotBlank(message = RoadConstant.Validation.WORK_PLAN_TYPE_REQUIRED)
    @Size(max = RoadConstant.Validation.MAX_LENGTH_50, message = RoadConstant.Validation.WORK_PLAN_TYPE_MAX)
    private String workPlanType;

    @NotBlank(message = RoadConstant.Validation.CD_WORK_TYPE_REQUIRED)
    @Size(max = RoadConstant.Validation.MAX_LENGTH_50, message = RoadConstant.Validation.CD_WORK_TYPE_MAX)
    private String cdWorkType;

    @Size(max = RoadConstant.Validation.MAX_LENGTH_50, message = RoadConstant.Validation.REPAIR_MAINTENANCE_TYPE_MAX)
    private String repairMaintenanceType;

    @Positive(message = RoadConstant.Validation.OFFICE_ID_POSITIVE)
    private Integer officeId;

    @Positive(message = RoadConstant.Validation.WORK_ID_POSITIVE)
    private Integer workId;

    @Positive(message = RoadConstant.Validation.TENANT_ID_POSITIVE)
    private Integer tenantId;

    @AssertTrue(message = RoadConstant.Validation.REPAIR_MAINTENANCE_TYPE_CONDITIONAL)
    public boolean isRepairMaintenanceTypeValid() {
        if (workPlanType == null || workPlanType.isBlank()) {
            return true;
        }

        if (RoadConstant.WorkPlan.isNonPlan(workPlanType)) {
            return repairMaintenanceType != null && !repairMaintenanceType.isBlank();
        }

        return repairMaintenanceType == null || repairMaintenanceType.isBlank();
    }
}
