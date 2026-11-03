package com.cdac.egov.wms.work.subwork.building.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import com.cdac.egov.wms.work.subwork.building.constants.BuildingWorkConstants;

public record BuildingRequest(
	    Long workId,
	    Long officeId,
	    Long tenantId,
	    @NotBlank @Size(max = BuildingWorkConstants.Validation.DESCRIPTION_MAX_LENGTH) String buildingDescription,
	    @NotNull @DecimalMin(BuildingWorkConstants.Validation.AREA_MIN) BigDecimal builtUpArea,
	    @NotNull @Min(BuildingWorkConstants.Validation.COUNT_MIN) Integer numberOfFloors,
	    @NotNull @Min(BuildingWorkConstants.Validation.COUNT_MIN) Integer numberOfBasements,
	    @NotBlank String maintenanceType,
	    @NotBlank String buildingActivity
	) {}

