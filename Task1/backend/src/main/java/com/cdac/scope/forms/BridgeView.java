package com.cdac.scope.forms;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BridgeView {

	private final Long scopeId;
	private final String riverNallahBarrier;
	private final BigDecimal width;
	private final BigDecimal length;
	private final String approvalRoad;
	private final Integer numberOfSpans;
	private final BigDecimal spanLength;
	private final String bridgeActivity;
	private final String otherActivityDescription;
	private final Integer officeId;
	private final Integer workId;
	private final Integer tenantId;
	private final Instant createdAt;
	private final Instant updatedAt;
	private final Instant revisionTimestamp;

	public Long getId() {
		return scopeId;
	}
}
