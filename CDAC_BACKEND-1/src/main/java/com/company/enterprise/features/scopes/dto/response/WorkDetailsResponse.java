package com.company.enterprise.features.scopes.dto.response;

import java.util.List;

public record WorkDetailsResponse(
        Long workId,
        String subWorkDetails,
        List<WorkScopeResponse> workScopes) {
}
