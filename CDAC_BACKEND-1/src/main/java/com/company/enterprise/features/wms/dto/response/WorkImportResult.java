package com.company.enterprise.features.wms.dto.response;

import java.util.List;

public record WorkImportResult(int importedCount, List<WorkImportError> errors) {
}
