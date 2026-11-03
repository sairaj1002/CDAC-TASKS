package com.cdac.egov.wms.work.subwork.dropdown.dto;

public record DropdownOptionDto(
    Long id,
    String category,
    String code,
    String label,
    Boolean active,
    Integer displayOrder
) {}

