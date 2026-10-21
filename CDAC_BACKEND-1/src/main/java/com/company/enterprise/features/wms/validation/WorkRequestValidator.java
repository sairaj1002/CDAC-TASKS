package com.company.enterprise.features.wms.validation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.enterprise.core.exception.BusinessException;
import com.company.enterprise.features.wms.dto.request.WorkCreateRequest;
import com.company.enterprise.features.wms.dto.request.WorkUpdateRequest;

@Component
public class WorkRequestValidator {

    public void validate(WorkCreateRequest request) {
        validateDateRange(request.getStipulatedStartDate(), request.getStipulatedEndDate());
    }

    public void validate(WorkUpdateRequest request) {
        validateDateRange(request.getStipulatedStartDate(), request.getStipulatedEndDate());
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException("Stipulated end date must be on or after the start date");
        }
    }
}
