package com.company.enterprise.features.wms.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.company.enterprise.core.auditing.AuditAction;
import com.company.enterprise.core.auditing.AuditContext;
import com.company.enterprise.core.auditing.AuditUser;
import com.company.enterprise.core.exception.ResourceNotFoundException;
import com.company.enterprise.features.wms.dto.request.WorkCreateRequest;
import com.company.enterprise.features.wms.dto.request.WorkUpdateRequest;
import com.company.enterprise.features.wms.dto.response.WorkAuditResponse;
import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.entity.WmsAuditDetailsEntity;
import com.company.enterprise.features.wms.entity.WorkEntity;
import com.company.enterprise.features.wms.mapper.WorkMapper;
import com.company.enterprise.features.wms.repository.WorkRepository;
import com.company.enterprise.features.wms.repository.WmsAuditDetailsRepository;
import com.company.enterprise.features.wms.service.WorkService;
import com.company.enterprise.features.wms.specification.WorkSpecification;
import com.company.enterprise.features.wms.validation.WorkRequestValidator;

@Service
@Transactional
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepository;
    private final WmsAuditDetailsRepository auditRepository;
    private final WorkMapper workMapper;
    private final WorkRequestValidator validator;

    public WorkServiceImpl(
            WorkRepository workRepository,
            WmsAuditDetailsRepository auditRepository,
            WorkMapper workMapper,
            WorkRequestValidator validator) {
        this.workRepository = workRepository;
        this.auditRepository = auditRepository;
        this.workMapper = workMapper;
        this.validator = validator;
    }

    @Override
    public WorkResponse create(WorkCreateRequest request) {
        validator.validate(request);
        setAuditAction(AuditAction.CREATED);
        WorkEntity saved = workRepository.saveAndFlush(workMapper.toEntity(request));
        recordAudit(saved, AuditAction.CREATED);
        return workMapper.toResponse(saved);
    }

    @Override
    public List<WorkResponse> createMany(List<WorkCreateRequest> requests) {
        requests.forEach(validator::validate);
        if (requests.isEmpty()) {
            return List.of();
        }
        setAuditAction(AuditAction.CREATED);
        List<WorkEntity> savedWorks = workRepository.saveAllAndFlush(
                requests.stream().map(workMapper::toEntity).toList());
        savedWorks.forEach(work -> recordAudit(work, AuditAction.CREATED));
        return savedWorks
                .stream()
                .map(workMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkResponse getById(Long workRegNo) {
        return workMapper.toResponse(findWork(workRegNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkResponse> getAll(String search) {
        return workRepository.findAll(WorkSpecification.matchingSearch(search))
                .stream()
                .map(workMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkAuditResponse> getAuditHistory(Long workRegNo) {
        return auditRepository.findAllByWorkRegNoOrderByRevisionAtDescAuditIdDesc(workRegNo)
                .stream()
                .map(audit -> {
                    WorkResponse snapshot = new WorkResponse();
                    snapshot.setWorkRegNo(audit.getWorkRegNo());
                    snapshot.setWorkDetails(audit.getWorkDetails());
                    snapshot.setActualStartDate(audit.getActualStartDate());
                    snapshot.setContractorName(audit.getContractorName());
                    snapshot.setStipulatedStartDate(audit.getStipulatedStartDate());
                    snapshot.setStipulatedEndDate(audit.getStipulatedEndDate());
                    snapshot.setCreatedAt(audit.getCreatedAt());
                    snapshot.setUpdatedAt(audit.getUpdatedAt());
                    return new WorkAuditResponse(
                            audit.getAuditId(),
                            audit.getRevisionAt(),
                            audit.getRevisionType(),
                            audit.getLegacyRevision(),
                            snapshot,
                            audit.getCreatedBy(),
                            audit.getModifiedBy(),
                            audit.getUserId(),
                            audit.getUserName(),
                            audit.getIpAddress(),
                            audit.getMacAddress());
                }).toList();
    }

    @Override
    public WorkResponse update(Long workRegNo, WorkUpdateRequest request) {
        validator.validate(request);
        WorkEntity existing = findWork(workRegNo);
        setAuditAction(AuditAction.UPDATED);
        workMapper.updateEntity(request, existing);
        WorkEntity saved = workRepository.saveAndFlush(existing);
        recordAudit(saved, AuditAction.UPDATED);
        return workMapper.toResponse(saved);
    }

    @Override
    public void delete(Long workRegNo) {
        WorkEntity existing = findWork(workRegNo);
        setAuditAction(AuditAction.DELETED);
        recordAudit(existing, AuditAction.DELETED);
        workRepository.delete(existing);
    }

    private void recordAudit(WorkEntity work, AuditAction action) {
        AuditUser user = AuditContext.getUser();
        String userName = user == null ? null : user.userName();
        String createdBy;
        if (action == AuditAction.CREATED) {
            createdBy = userName;
        } else {
            var firstAudit = auditRepository.findFirstByWorkRegNoOrderByRevisionAtAscAuditIdAsc(work.getWorkRegNo());
            createdBy = firstAudit.isPresent() ? firstAudit.get().getCreatedBy() : userName;
        }

        WmsAuditDetailsEntity audit = new WmsAuditDetailsEntity();
        audit.setRevisionAt(LocalDateTime.now());
        audit.setRevisionType(action);
        audit.setWorkRegNo(work.getWorkRegNo());
        audit.setWorkDetails(work.getWorkDetails());
        audit.setActualStartDate(work.getActualStartDate());
        audit.setContractorName(work.getContractorName());
        audit.setStipulatedStartDate(work.getStipulatedStartDate());
        audit.setStipulatedEndDate(work.getStipulatedEndDate());
        audit.setCreatedAt(work.getCreatedAt());
        audit.setUpdatedAt(work.getUpdatedAt());
        audit.setCreatedBy(createdBy);
        audit.setModifiedBy(userName);
        if (user != null) {
            audit.setUserId(user.userId());
            audit.setUserName(user.userName());
            audit.setIpAddress(user.ipAddress());
            audit.setMacAddress(user.macAddress());
        }
        auditRepository.save(audit);
    }

    private void setAuditAction(AuditAction action) {
        AuditContext.setAction(action);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    AuditContext.clearAction();
                }
            });
        }
    }

    private WorkEntity findWork(Long workRegNo) {
        return workRepository.findById(workRegNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Work record " + workRegNo + " was not found"));
    }
}
