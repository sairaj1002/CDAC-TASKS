package com.cdac.scope.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateDropdownOptionRequest(
    @NotBlank(message = "Label is required.")
    @Size(max = 150, message = "Label must not exceed 150 characters.")
    String label,

    @NotNull(message = "Display order is required.")
    Integer displayOrder,

    @NotNull(message = "Active status is required.")
    Boolean active
) {}
