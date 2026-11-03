package com.cdac.egov.wms.work.subwork.road.dto;

import java.math.BigDecimal;
import com.cdac.egov.wms.work.subwork.road.constant.RoadConstant;


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

    @NotBlank(message = RoadConstant.Validation.CD_WORKS_REQUIRED)
    private String cdWorks;

    @NotNull
    @jakarta.validation.constraints.Min(0)
    private Integer numberOfStructures;

    @Size(max = RoadConstant.Validation.MAX_LENGTH_50, message = RoadConstant.Validation.REPAIR_MAINTENANCE_TYPE_MAX)
    @NotBlank
    private String repairMaintenanceType;

    @Positive(message = RoadConstant.Validation.OFFICE_ID_POSITIVE)
    private Integer officeId;

    @Positive(message = RoadConstant.Validation.WORK_ID_POSITIVE)
    private Integer workId;

    @Positive(message = RoadConstant.Validation.TENANT_ID_POSITIVE)
    private Integer tenantId;

}

