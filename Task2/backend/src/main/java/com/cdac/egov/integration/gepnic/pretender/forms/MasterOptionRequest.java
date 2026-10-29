package com.cdac.egov.integration.gepnic.pretender.forms;

import jakarta.validation.constraints.Size;

public record MasterOptionRequest(
    @Size(max = 255) String name,
    @Size(max = 255) String code,
    @Size(max = 2000) String description,
    Integer bovalue,
    Long tenderCategoryId
) {}
