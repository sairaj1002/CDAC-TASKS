package com.company.enterprise.features.wms.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkCreateRequest {

    @NotBlank(message = "Work details are required")
    @Size(max = 500, message = "Work details cannot exceed 500 characters")
    private String workDetails;

    private LocalDate actualStartDate;

    @NotBlank(message = "Contractor name is required")
    @Size(max = 150, message = "Contractor name cannot exceed 150 characters")
    private String contractorName;

    @NotNull(message = "Stipulated start date is required")
    private LocalDate stipulatedStartDate;

    @NotNull(message = "Stipulated end date is required")
    private LocalDate stipulatedEndDate;
}
