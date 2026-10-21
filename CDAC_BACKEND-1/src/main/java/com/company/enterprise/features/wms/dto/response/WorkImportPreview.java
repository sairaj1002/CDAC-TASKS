package com.company.enterprise.features.wms.dto.response;

import java.util.List;

public record WorkImportPreview(
        List<WorkImportRow> validRows,
        List<WorkImportError> errors) {
}
