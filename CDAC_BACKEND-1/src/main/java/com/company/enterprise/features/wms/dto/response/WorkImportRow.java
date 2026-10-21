package com.company.enterprise.features.wms.dto.response;

import java.time.LocalDate;

public record WorkImportRow(
        int rowNumber,
        String workDetails,
        LocalDate actualStartDate,
        String contractorName,
        LocalDate stipulatedStartDate,
        LocalDate stipulatedEndDate) {
}
