package com.cdac.egov.wms.work.subwork.others.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.cdac.egov.wms.work.subwork.others.constants.OtherWorkConstants;

public class OthersRequest {

	@NotBlank
	@Size(max = OtherWorkConstants.Validation.DESCRIPTION_MAX_LENGTH)
	private String description;

	@NotNull
	private Long unitId;

	@NotNull
	@DecimalMin(OtherWorkConstants.Validation.QUANTITY_MIN)
	private BigDecimal quantity;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	private Long workId;
	private Long officeId;
	private Long tenantId;

	public Long getWorkId() {
		return workId;
	}


	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
}

