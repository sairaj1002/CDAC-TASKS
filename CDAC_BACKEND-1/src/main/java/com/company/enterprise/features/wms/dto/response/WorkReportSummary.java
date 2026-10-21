package com.company.enterprise.features.wms.dto.response;

import java.util.List;

public record WorkReportSummary(
        long totalRecords,
        List<String> contractorLabels,
        List<Long> contractorCounts) {
}
