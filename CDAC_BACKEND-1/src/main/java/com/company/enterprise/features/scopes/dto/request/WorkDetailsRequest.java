package com.company.enterprise.features.scopes.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record WorkDetailsRequest(
        @NotBlank @Size(max = 500) String subWorkDetails,
        @NotEmpty List<@Valid WorkScopeRequest> workScopes) {
}
