package com.cdac.egov.integration.gepnic.pretender.forms;

public record MasterOptionResponse(
    Long id,
    String name,
    String code,
    String description,
    Integer bovalue,
    Long tenderCategoryId,
    String tenderCategoryName,
    String sourceRowKey
) {}
