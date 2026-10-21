package com.company.enterprise.features.scopes.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.enterprise.features.scopes.dto.request.WorkDetailsRequest;
import com.company.enterprise.features.scopes.dto.request.WorkScopeRequest;
import com.company.enterprise.features.scopes.dto.response.WorkDetailsResponse;
import com.company.enterprise.features.scopes.dto.response.WorkScopeResponse;
import com.company.enterprise.features.scopes.entity.WorkDetails;
import com.company.enterprise.features.scopes.entity.WorkScopes;

@Component
public class WorkDetailsMapper {

    public WorkDetails toEntity(WorkDetailsRequest request) {
        WorkDetails workDetails = new WorkDetails();
        workDetails.setSubWorkDetails(request.subWorkDetails());
        workDetails.replaceWorkScopes(request.workScopes().stream().map(this::toEntity).toList());
        return workDetails;
    }

    public void updateEntity(WorkDetailsRequest request, WorkDetails workDetails) {
        workDetails.setSubWorkDetails(request.subWorkDetails());
        workDetails.replaceWorkScopes(request.workScopes().stream().map(this::toEntity).toList());
    }

    public WorkDetailsResponse toResponse(WorkDetails workDetails) {
        List<WorkScopeResponse> scopes = workDetails.getWorkScopes().stream()
                .map(scope -> new WorkScopeResponse(
                        scope.getScopeId(),
                        scope.getNameOfRiverNallahBarrier(),
                        scope.getWidth(),
                        scope.getLength(),
                        scope.getApproachRoad(),
                        scope.getNumberOfSpans(),
                        scope.getSpanLength(),
                        scope.getBridgeActivity()))
                .toList();

        return new WorkDetailsResponse(workDetails.getWorkId(), workDetails.getSubWorkDetails(), scopes);
    }

    private WorkScopes toEntity(WorkScopeRequest request) {
        WorkScopes scope = new WorkScopes();
        scope.setNameOfRiverNallahBarrier(request.nameOfRiverNallahBarrier());
        scope.setWidth(request.width());
        scope.setLength(request.length());
        scope.setApproachRoad(request.approachRoad());
        scope.setNumberOfSpans(request.numberOfSpans());
        scope.setSpanLength(request.spanLength());
        scope.setBridgeActivity(request.bridgeActivity());
        return scope;
    }
}
