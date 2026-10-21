package com.cdac.road.forms;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoadComponentView {

    private final Long id;
    private final String natureOfWork;
    private final String proposedRoadType;
    private final BigDecimal length;
    private final BigDecimal width;
    private final String workPlanType;
    private final String cdWorkType;
    private final String repairMaintenanceType;
    private final Integer officeId;
    private final Integer workId;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant revisionTimestamp;
}