package com.company.enterprise.features.wms.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.company.enterprise.features.wms.dto.request.WorkCreateRequest;
import com.company.enterprise.features.wms.dto.request.WorkUpdateRequest;
import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.entity.WorkEntity;

@Component
public class WorkMapper {

    private final ModelMapper modelMapper;

    public WorkMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public WorkEntity toEntity(WorkCreateRequest request) {
        return modelMapper.map(request, WorkEntity.class);
    }

    public void updateEntity(WorkUpdateRequest request, WorkEntity entity) {
        modelMapper.map(request, entity);
    }

    public WorkResponse toResponse(WorkEntity entity) {
        return modelMapper.map(entity, WorkResponse.class);
    }
}
