package com.cdac.bridge.forms;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BridgeAuditRevisionView {

	private final int revisionNumber;
	private final Instant revisionDate;
	private final String revisionType;
	private final BridgeComponentView snapshot;
}
