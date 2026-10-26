package com.cdac.scope.forms;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoadView {

    private final Long scopeId;
    private final String natureOfWork;
    private final String proposedRoadType;
    private final BigDecimal length;
    private final BigDecimal width;
    private final String workPlanType;
    private final String cdWorkType;
    private final String repairMaintenanceType;
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
