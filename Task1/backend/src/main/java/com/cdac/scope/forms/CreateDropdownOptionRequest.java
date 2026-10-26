package com.cdac.scope.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDropdownOptionRequest(
    @NotBlank(message = "Category is required.")
    @Size(max = 60, message = "Category must not exceed 60 characters.")
    String category,

    @NotBlank(message = "Code is required.")
    @Size(max = 60, message = "Code must not exceed 60 characters.")
    String code,

    @NotBlank(message = "Label is required.")
    @Size(max = 150, message = "Label must not exceed 150 characters.")
    String label,

    @NotNull(message = "Display order is required.")
    Integer displayOrder,

    Boolean active
) {}
