package com.company.enterprise.features.scopes.dto.response;

import java.math.BigDecimal;

import com.company.enterprise.features.scopes.entity.BridgeActivity;

public record WorkScopeResponse(
        Long scopeId,
        String nameOfRiverNallahBarrier,
        BigDecimal width,
        BigDecimal length,
        String approachRoad,
        Integer numberOfSpans,
        BigDecimal spanLength,
        BridgeActivity bridgeActivity) {
}
