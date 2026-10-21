package com.cdac.bridge.forms;

import java.math.BigDecimal;

import com.cdac.bridge.entities.BridgeActivity;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
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
public class BridgeComponentForm {

	@NotBlank
	@Size(max = 150)
	private String riverNallahBarrier;

	@DecimalMin("0.0")
	@Digits(integer = 10, fraction = 3)
	private BigDecimal width;

	@DecimalMin("0.0")
	@Digits(integer = 10, fraction = 3)
	private BigDecimal length;

	@NotBlank
	@Size(max = 150)
	private String approvalRoad;

	@Min(0)
	private Integer numberOfSpans;

	@DecimalMin("0.0")
	@Digits(integer = 10, fraction = 3)
	private BigDecimal spanLength;

	@NotNull
	private BridgeActivity bridgeActivity;

	@Size(max = 500)
	private String otherActivityDescription;

	@Positive
	private Integer officeId;

	@Positive
	private Integer workId;

	@AssertTrue(message = "Other activity description is required for Other Activity.")
	public boolean isOtherActivityDescriptionValid() {
		return bridgeActivity != BridgeActivity.OTHER_ACTIVITY
				|| (otherActivityDescription != null && !otherActivityDescription.isBlank());
	}
}
