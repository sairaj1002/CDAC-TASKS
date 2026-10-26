package com.cdac.scope.service;

import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.scope.constant.RoadConstant;
import com.cdac.scope.forms.DropdownOptionDto;
import com.cdac.scope.entities.AuditRevisionType;
import com.cdac.scope.entities.RoadEntity;
import com.cdac.scope.forms.RoadAuditRevisionView;
import com.cdac.scope.forms.RoadForm;
import com.cdac.scope.forms.RoadView;
import com.cdac.scope.repository.RoadRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoadService {

    private final RoadRepository repository;
    private final EntityManager entityManager;
    private final DropdownOptionService dropdownService;

    public RoadService(
            RoadRepository repository,
            EntityManager entityManager,
            DropdownOptionService dropdownService) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.dropdownService = dropdownService;
    }

    // --------------------------------------------------
    // READ ALL ACTIVE ROAD COMPONENTS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<RoadView> findAll() {
        return repository
                .findAllByDeletedFalseOrDeletedIsNullOrderByScopeIdAsc()
                .stream()
                .map(this::toView)
                .toList();
    }

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    public RoadView create(RoadForm form) {
        RoadEntity entity = new RoadEntity();
        applyForm(entity, form);
        entity.setDeleted(false);
        entity.setRevisionType(AuditRevisionType.CREATED);
        return toView(repository.save(entity));
    }

    // --------------------------------------------------
    // UPDATE
    // --------------------------------------------------

    public RoadView update(long id, RoadForm form) {
        RoadEntity entity = findActive(id);
        applyForm(entity, form);
        entity.setRevisionType(AuditRevisionType.UPDATED);
        return toView(repository.save(entity));
    }

    // --------------------------------------------------
    // SOFT DELETE
    // --------------------------------------------------

    public void delete(long id) {
        RoadEntity entity = findActive(id);
        entity.setDeleted(true);
        entity.setRevisionType(AuditRevisionType.DELETED);
        repository.save(entity);
    }

    // --------------------------------------------------
    // AUDIT HISTORY
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<RoadAuditRevisionView> findAuditHistory(long id) {
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<?> rows = reader.createQuery()
                .forRevisionsOfEntity(
                        RoadEntity.class,
                        false,
                        true)
                .add(AuditEntity.id().eq(id))
                .addOrder(AuditEntity.revisionNumber().asc())
                .getResultList();

        return rows.stream()
                .map(row -> toAuditRevision((Object[]) row))
                .toList();
    }

    // --------------------------------------------------
    // DYNAMIC DROPDOWN OPTIONS (BACKED BY DB)
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<String> findNatureOfWorks() {
        return dropdownService.findActiveByCategory(RoadConstant.Category.NATURE_OF_WORK).stream()
                .map(DropdownOptionDto::label)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findProposedRoadTypes() {
        return dropdownService.findActiveByCategory(RoadConstant.Category.ROAD_TYPE).stream()
                .map(DropdownOptionDto::label)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findCdWorkTypes() {
        return dropdownService.findActiveByCategory(RoadConstant.Category.CD_WORK_TYPE).stream()
                .map(DropdownOptionDto::label)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findRepairMaintenanceTypes() {
        return dropdownService.findActiveByCategory(RoadConstant.Category.REPAIR_MAINTENANCE_TYPE).stream()
                .map(DropdownOptionDto::label)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findWorkPlanTypes() {
        return dropdownService.findActiveByCategory(RoadConstant.Category.WORK_PLAN_TYPE).stream()
                .map(DropdownOptionDto::label)
                .toList();
    }

    // --------------------------------------------------
    // APPLY FORM TO ENTITY & VALIDATE CODES
    // --------------------------------------------------

    private void applyForm(
            RoadEntity entity,
            RoadForm form) {

        // Validate submitted codes against the database
        dropdownService.validateActiveCode(RoadConstant.Category.NATURE_OF_WORK, form.getNatureOfWork());
        dropdownService.validateActiveCode(RoadConstant.Category.ROAD_TYPE, form.getProposedRoadType());
        dropdownService.validateActiveCode(RoadConstant.Category.WORK_PLAN_TYPE, form.getWorkPlanType());
        dropdownService.validateActiveCode(RoadConstant.Category.CD_WORK_TYPE, form.getCdWorkType());

        boolean isNonPlan = RoadConstant.WorkPlan.isNonPlan(form.getWorkPlanType());

        if (isNonPlan && form.getRepairMaintenanceType() != null && !form.getRepairMaintenanceType().isBlank()) {
            dropdownService.validateActiveCode(RoadConstant.Category.REPAIR_MAINTENANCE_TYPE, form.getRepairMaintenanceType());
        }

        entity.setNatureOfWork(form.getNatureOfWork().trim());
        entity.setProposedRoadType(form.getProposedRoadType().trim());
        entity.setLength(form.getLength());
        entity.setWidth(form.getWidth());
        entity.setWorkPlanType(form.getWorkPlanType().trim());
        entity.setCdWorkType(form.getCdWorkType().trim());
        entity.setRepairMaintenanceType(isNonPlan && form.getRepairMaintenanceType() != null ? form.getRepairMaintenanceType().trim() : null);

        entity.setOfficeId(form.getOfficeId());
        entity.setWorkId(form.getWorkId());
        entity.setTenantId(form.getTenantId());
    }

    // --------------------------------------------------
    // FIND ACTIVE RECORD
    // --------------------------------------------------

    private RoadEntity findActive(long id) {
        return repository
                .findByScopeIdAndDeletedFalseOrScopeIdAndDeletedIsNull(id, id)
                .orElseThrow(() ->
                        new EntityNotFoundException(RoadConstant.Messages.notFound(id)));
    }

    // --------------------------------------------------
    // ENTITY TO VIEW (RESOLVES DISPLAY LABELS)
    // --------------------------------------------------

    private RoadView toView(RoadEntity entity) {
        return new RoadView(
                entity.getScopeId(),
                dropdownService.getLabel(RoadConstant.Category.NATURE_OF_WORK, entity.getNatureOfWork()),
                dropdownService.getLabel(RoadConstant.Category.ROAD_TYPE, entity.getProposedRoadType()),
                entity.getLength(),
                entity.getWidth(),
                dropdownService.getLabel(RoadConstant.Category.WORK_PLAN_TYPE, entity.getWorkPlanType()),
                dropdownService.getLabel(RoadConstant.Category.CD_WORK_TYPE, entity.getCdWorkType()),
                dropdownService.getLabel(RoadConstant.Category.REPAIR_MAINTENANCE_TYPE, entity.getRepairMaintenanceType()),
                entity.getOfficeId(),
                entity.getWorkId(),
                entity.getTenantId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getRevisionTimestamp());
    }

    // --------------------------------------------------
    // AUDIT REVISION MAPPING
    // --------------------------------------------------

    private RoadAuditRevisionView toAuditRevision(Object[] row) {
        RoadEntity entity = (RoadEntity) row[0];
        DefaultRevisionEntity revision = (DefaultRevisionEntity) row[1];
        RevisionType revisionType = (RevisionType) row[2];

        if (entity == null) {
            throw new IllegalStateException(RoadConstant.Messages.ENVERS_SNAPSHOT_MISSING);
        }

        String type = entity.getRevisionType() == null
                ? null
                : entity.getRevisionType().name();

        if (type == null) {
            type = switch (revisionType) {
                case ADD -> RoadConstant.Audit.CREATED;
                case MOD -> RoadConstant.Audit.UPDATED;
                case DEL -> RoadConstant.Audit.DELETED;
            };
        }

        return new RoadAuditRevisionView(
                revision.getId(),
                revision.getRevisionDate().toInstant(),
                type,
                toView(entity));
    }
}
