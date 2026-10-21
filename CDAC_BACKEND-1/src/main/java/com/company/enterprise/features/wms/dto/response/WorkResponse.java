package com.company.enterprise.features.wms.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkResponse {

    private Long workRegNo;
    private String workDetails;
    private LocalDate actualStartDate;
    private String contractorName;
    private LocalDate stipulatedStartDate;
    private LocalDate stipulatedEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
