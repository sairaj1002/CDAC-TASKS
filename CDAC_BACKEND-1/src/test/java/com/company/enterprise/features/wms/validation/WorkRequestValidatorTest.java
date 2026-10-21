package com.company.enterprise.features.wms.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.company.enterprise.core.exception.BusinessException;
import com.company.enterprise.features.wms.dto.request.WorkCreateRequest;

class WorkRequestValidatorTest {

    private final WorkRequestValidator validator = new WorkRequestValidator();

    @Test
    void rejectsEndDateBeforeStartDate() {
        WorkCreateRequest request = new WorkCreateRequest();
        request.setStipulatedStartDate(LocalDate.of(2026, 10, 10));
        request.setStipulatedEndDate(LocalDate.of(2026, 10, 9));

        assertThrows(BusinessException.class, () -> validator.validate(request));
    }

    @Test
    void acceptsEndDateOnOrAfterStartDate() {
        WorkCreateRequest request = new WorkCreateRequest();
        request.setStipulatedStartDate(LocalDate.of(2026, 10, 10));
        request.setStipulatedEndDate(LocalDate.of(2026, 10, 10));

        assertDoesNotThrow(() -> validator.validate(request));
    }
}
