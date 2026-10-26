package com.cdac.scope.forms;

public record DropdownOptionDto(
    Long id,
    String category,
    String code,
    String label,
    Boolean active,
    Integer displayOrder
) {}
