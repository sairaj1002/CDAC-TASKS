package com.company.enterprise.features.wms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.company.enterprise.core.auditing.AuditAction;
import com.company.enterprise.core.auditing.AuditContext;
import com.company.enterprise.core.auditing.AuditUser;
import com.company.enterprise.features.wms.dto.request.WorkCreateRequest;
import com.company.enterprise.features.wms.dto.response.WorkAuditResponse;
import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.entity.WmsAuditDetailsEntity;
import com.company.enterprise.features.wms.entity.WorkEntity;
import com.company.enterprise.features.wms.mapper.WorkMapper;
import com.company.enterprise.features.wms.repository.WorkRepository;
import com.company.enterprise.features.wms.repository.WmsAuditDetailsRepository;
import com.company.enterprise.features.wms.validation.WorkRequestValidator;

class WorkServiceImplTest {

    private final WorkRepository workRepository = Mockito.mock(WorkRepository.class);
    private final WmsAuditDetailsRepository auditRepository = Mockito.mock(WmsAuditDetailsRepository.class);
    private final WorkMapper workMapper = Mockito.mock(WorkMapper.class);
    private final WorkRequestValidator validator = Mockito.mock(WorkRequestValidator.class);
    private final WorkServiceImpl service =
            new WorkServiceImpl(workRepository, auditRepository, workMapper, validator);

    @BeforeEach
    void setAuditUser() {
        AuditContext.setUser(new AuditUser(42L, "operator", "192.0.2.5", "00:11:22:33:44:55"));
    }

    @AfterEach
    void clearAuditContext() {
        AuditContext.clear();
    }

    @Test
    void createStoresTheSnapshotAndAuditMetadataInOneAuditRow() {
        WorkCreateRequest request = new WorkCreateRequest();
        WorkEntity entity = sampleWork();
        when(workMapper.toEntity(request)).thenReturn(entity);
        when(workRepository.saveAndFlush(entity)).thenReturn(entity);
        when(workMapper.toResponse(entity)).thenReturn(new WorkResponse());

        service.create(request);

        ArgumentCaptor<WmsAuditDetailsEntity> auditCaptor =
                ArgumentCaptor.forClass(WmsAuditDetailsEntity.class);
        verify(auditRepository).save(auditCaptor.capture());
        WmsAuditDetailsEntity audit = auditCaptor.getValue();
        assertEquals(AuditAction.CREATED, audit.getRevisionType());
        assertEquals(101L, audit.getWorkRegNo());
        assertEquals("Routine maintenance", audit.getWorkDetails());
        assertEquals("operator", audit.getCreatedBy());
        assertEquals("operator", audit.getModifiedBy());
        assertEquals(42L, audit.getUserId());
        assertEquals("192.0.2.5", audit.getIpAddress());
        assertEquals("00:11:22:33:44:55", audit.getMacAddress());
    }

    @Test
    void deleteStoresDeletedSnapshotBeforeRemovingTheWorkRow() {
        WorkEntity entity = sampleWork();
        when(workRepository.findById(101L)).thenReturn(Optional.of(entity));
        when(auditRepository.findFirstByWorkRegNoOrderByRevisionAtAscAuditIdAsc(any()))
                .thenReturn(Optional.of(auditCreatedBy("original-creator")));

        service.delete(101L);

        ArgumentCaptor<WmsAuditDetailsEntity> auditCaptor =
                ArgumentCaptor.forClass(WmsAuditDetailsEntity.class);
        verify(auditRepository).save(auditCaptor.capture());
        assertEquals(AuditAction.DELETED, auditCaptor.getValue().getRevisionType());
        assertEquals("original-creator", auditCaptor.getValue().getCreatedBy());
        assertEquals("operator", auditCaptor.getValue().getModifiedBy());
        verify(workRepository).delete(entity);
    }

    @Test
    void auditHistoryReturnsSnapshotAndAuditMetadata() {
        WmsAuditDetailsEntity audit = auditCreatedBy("original-creator");
        audit.setAuditId(7L);
        audit.setLegacyRevision(3L);
        audit.setRevisionAt(LocalDateTime.of(2026, 10, 1, 10, 15));
        audit.setRevisionType(AuditAction.UPDATED);
        audit.setWorkRegNo(101L);
        audit.setWorkDetails("Routine maintenance");
        audit.setContractorName("Example contractor");
        audit.setStipulatedStartDate(LocalDate.of(2026, 10, 1));
        audit.setStipulatedEndDate(LocalDate.of(2026, 10, 10));
        audit.setCreatedBy("original-creator");
        audit.setModifiedBy("operator");
        audit.setUserId(42L);
        audit.setUserName("operator");
        audit.setIpAddress("192.0.2.5");
        audit.setMacAddress("00:11:22:33:44:55");
        when(auditRepository.findAllByWorkRegNoOrderByRevisionAtDescAuditIdDesc(101L))
                .thenReturn(List.of(audit));

        List<WorkAuditResponse> history = service.getAuditHistory(101L);

        assertEquals(1, history.size());
        WorkAuditResponse revision = history.get(0);
        assertEquals(7L, revision.revisionNumber());
        assertEquals(3L, revision.legacyRevision());
        assertEquals(AuditAction.UPDATED, revision.revisionType());
        assertEquals("Routine maintenance", revision.work().getWorkDetails());
        assertEquals("original-creator", revision.createdBy());
        assertEquals("operator", revision.modifiedBy());
        assertEquals(42L, revision.userId());
        assertEquals("operator", revision.userName());
        assertEquals("192.0.2.5", revision.ipAddress());
        assertEquals("00:11:22:33:44:55", revision.macAddress());
    }

    private WorkEntity sampleWork() {
        WorkEntity entity = new WorkEntity();
        entity.setWorkRegNo(101L);
        entity.setWorkDetails("Routine maintenance");
        entity.setContractorName("Example contractor");
        entity.setStipulatedStartDate(LocalDate.of(2026, 10, 1));
        entity.setStipulatedEndDate(LocalDate.of(2026, 10, 10));
        entity.setCreatedAt(LocalDateTime.of(2026, 9, 30, 9, 0));
        entity.setUpdatedAt(LocalDateTime.of(2026, 9, 30, 9, 0));
        return entity;
    }

    private WmsAuditDetailsEntity auditCreatedBy(String createdBy) {
        WmsAuditDetailsEntity audit = new WmsAuditDetailsEntity();
        audit.setCreatedBy(createdBy);
        return audit;
    }
}
