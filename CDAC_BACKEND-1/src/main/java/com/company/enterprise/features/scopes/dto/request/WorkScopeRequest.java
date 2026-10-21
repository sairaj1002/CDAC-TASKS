package com.company.enterprise.features.scopes.dto.request;

import java.math.BigDecimal;

import com.company.enterprise.features.scopes.entity.BridgeActivity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record WorkScopeRequest(
        @NotBlank @Size(max = 200) String nameOfRiverNallahBarrier,
        @NotNull @Positive BigDecimal width,
        @NotNull @Positive BigDecimal length,
        @NotBlank @Size(max = 500) String approachRoad,
        @NotNull @Positive Integer numberOfSpans,
        @NotNull @Positive BigDecimal spanLength,
        @NotNull BridgeActivity bridgeActivity) {
}
