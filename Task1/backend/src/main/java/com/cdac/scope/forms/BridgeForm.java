package com.cdac.scope.forms;

import java.math.BigDecimal;
import com.cdac.scope.constant.BridgeConstant;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class BridgeForm {

	public BridgeForm(
			String riverNallahBarrier,
			BigDecimal width,
			BigDecimal length,
			String approvalRoad,
			Integer numberOfSpans,
			BigDecimal spanLength,
			String bridgeActivity,
			String otherActivityDescription,
			Integer officeId,
			Integer workId) {
		this(riverNallahBarrier, width, length, approvalRoad, numberOfSpans, spanLength,
				bridgeActivity, otherActivityDescription, officeId, workId, null);
	}


	@NotBlank
	@Size(max = BridgeConstant.Validation.RIVER_NALLAH_BARRIER_MAX_LENGTH)
	private String riverNallahBarrier;

	@DecimalMin(BridgeConstant.Validation.DECIMAL_MIN_ZERO)
	@Digits(integer = BridgeConstant.Validation.DIGITS_INTEGER, fraction = BridgeConstant.Validation.DIGITS_FRACTION)
	private BigDecimal width;

	@DecimalMin(BridgeConstant.Validation.DECIMAL_MIN_ZERO)
	@Digits(integer = BridgeConstant.Validation.DIGITS_INTEGER, fraction = BridgeConstant.Validation.DIGITS_FRACTION)
	private BigDecimal length;

	@NotBlank
	@Size(max = BridgeConstant.Validation.APPROVAL_ROAD_MAX_LENGTH)
	private String approvalRoad;

	@Min(BridgeConstant.Validation.NUMBER_OF_SPANS_MIN)
	private Integer numberOfSpans;

	@DecimalMin(BridgeConstant.Validation.DECIMAL_MIN_ZERO)
	@Digits(integer = BridgeConstant.Validation.DIGITS_INTEGER, fraction = BridgeConstant.Validation.DIGITS_FRACTION)
	private BigDecimal spanLength;

	@NotBlank(message = BridgeConstant.Validation.BRIDGE_ACTIVITY_REQUIRED)
	@Size(max = BridgeConstant.Validation.BRIDGE_ACTIVITY_MAX_LENGTH)
	private String bridgeActivity;

	@Size(max = BridgeConstant.Validation.OTHER_ACTIVITY_DESC_MAX_LENGTH)
	private String otherActivityDescription;

	@Positive
	private Integer officeId;

	@Positive
	private Integer workId;

	@Positive
	private Integer tenantId;

	@AssertTrue(message = BridgeConstant.Validation.OTHER_ACTIVITY_DESC_REQUIRED)
	public boolean isOtherActivityDescriptionValid() {
		if (bridgeActivity == null || bridgeActivity.isBlank()) {
			return true;
		}
		boolean isOther = BridgeConstant.Activity.isOtherActivity(bridgeActivity);
		return !isOther || (otherActivityDescription != null && !otherActivityDescription.isBlank());
	}
}
