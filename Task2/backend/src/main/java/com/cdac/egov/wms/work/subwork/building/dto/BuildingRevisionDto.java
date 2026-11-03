package com.cdac.egov.wms.work.subwork.building.dto;

import java.time.Instant;
import java.util.Map;

public record BuildingRevisionDto(
        Long revisionId,
        Instant timestamp,
        String revisionType,
        Map<String, Object> building
) {}


