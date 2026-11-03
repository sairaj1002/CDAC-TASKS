package com.cdac.egov.wms.work.subwork.road.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoadAuditRevisionView {
    private final int revisionNumber;
    private final Instant revisionDate;
    private final String revisionType;
    private final RoadView snapshot;
}

