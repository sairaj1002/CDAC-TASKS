package com.company.enterprise.features.scopes.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.enterprise.core.exception.ResourceNotFoundException;
import com.company.enterprise.features.scopes.dto.request.WorkDetailsRequest;
import com.company.enterprise.features.scopes.dto.response.WorkDetailsResponse;
import com.company.enterprise.features.scopes.entity.WorkDetails;
import com.company.enterprise.features.scopes.mapper.WorkDetailsMapper;
import com.company.enterprise.features.scopes.repository.WorkDetailsRepository;
import com.company.enterprise.features.scopes.service.WorkDetailsService;

@Service
@Transactional
public class WorkDetailsServiceImpl implements WorkDetailsService {

    private final WorkDetailsRepository repository;
    private final WorkDetailsMapper mapper;

    public WorkDetailsServiceImpl(WorkDetailsRepository repository, WorkDetailsMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WorkDetailsResponse create(WorkDetailsRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkDetailsResponse> getAll() {
        return repository.findAllByOrderByWorkIdDesc().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkDetailsResponse getById(Long workId) {
        return mapper.toResponse(findWorkDetails(workId));
    }

    @Override
    public WorkDetailsResponse update(Long workId, WorkDetailsRequest request) {
        WorkDetails workDetails = findWorkDetails(workId);
        mapper.updateEntity(request, workDetails);
        return mapper.toResponse(repository.save(workDetails));
    }

    @Override
    public void delete(Long workId) {
        repository.delete(findWorkDetails(workId));
    }

    private WorkDetails findWorkDetails(Long workId) {
        return repository.findById(workId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Work details " + workId + " were not found"));
    }
}
