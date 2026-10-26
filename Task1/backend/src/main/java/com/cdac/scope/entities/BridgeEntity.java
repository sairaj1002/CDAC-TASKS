package com.cdac.scope.entities;

import java.math.BigDecimal;
import java.time.Instant;

import com.cdac.scope.constant.BridgeConstant;

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

@Entity
@Audited
@Table(name = "bridge_component")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
public class BridgeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "scope_id")
	private Long scopeId;

	@Positive
	@Column(name = "office_id")
	private Integer officeId;

	@Positive
	@Column(name = "work_id")
	private Integer workId;

	@Positive
	@Column(name = "tenant_id")
	private Integer tenantId;

	@Enumerated(EnumType.STRING)
	@Column(name = "revision_type", length = 10)
	private AuditRevisionType revisionType;

	@Column(name = "deleted")
	private Boolean deleted = false;

	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(name = "revision_timestamp")
	private Instant revisionTimestamp;

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String riverNallahBarrier;

	@DecimalMin("0.0")
	@Digits(integer = 10, fraction = 3)
	@Column(precision = 13, scale = 3)
	private BigDecimal width;

	@DecimalMin("0.0")
	@Digits(integer = 10, fraction = 3)
	@Column(precision = 13, scale = 3)
	private BigDecimal length;

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String approvalRoad;

	@Min(0)
	private Integer numberOfSpans;

	@DecimalMin("0.0")
	@Digits(integer = 10, fraction = 3)
	@Column(precision = 13, scale = 3)
	private BigDecimal spanLength;

	@NotBlank
	@Size(max = BridgeConstant.Validation.BRIDGE_ACTIVITY_MAX_LENGTH)
	@Column(name = "bridge_activity", nullable = false, length = BridgeConstant.Validation.BRIDGE_ACTIVITY_MAX_LENGTH)
	private String bridgeActivity;

	@Size(max = BridgeConstant.Validation.OTHER_ACTIVITY_DESC_MAX_LENGTH)
	@Column(length = BridgeConstant.Validation.OTHER_ACTIVITY_DESC_MAX_LENGTH)
	private String otherActivityDescription;

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

	@AssertTrue(message = BridgeConstant.Validation.OTHER_ACTIVITY_DESC_REQUIRED)
	public boolean isOtherActivityDescriptionValid() {
		return !BridgeConstant.Activity.isOtherActivity(bridgeActivity)
				|| (otherActivityDescription != null && !otherActivityDescription.isBlank());
	}
}
