package com.cdac.bridge.forms;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BridgeComponentView {

	private final Long id;
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
	private final Instant createdAt;
	private final Instant updatedAt;
	private final Instant revisionTimestamp;
}
