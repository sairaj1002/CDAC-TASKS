
package com.cdac.road.service;

import java.util.Arrays;
import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.bridge.entities.AuditRevisionType;
import com.cdac.road.entities.RoadComponentEntity;
import com.cdac.road.entities.CdWorkType;
import com.cdac.road.entities.NatureOfWork;
import com.cdac.road.entities.ProposedRoadType;
import com.cdac.road.entities.RepairMaintenanceType;
import com.cdac.road.entities.WorkPlanType;
import com.cdac.road.forms.RoadAuditRevisionView;
import com.cdac.road.forms.RoadComponentForm;
import com.cdac.road.forms.RoadComponentView;
import com.cdac.road.repository.RoadComponentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoadComponentService {

    private final RoadComponentRepository repository;
    private final EntityManager entityManager;

    public RoadComponentService(
            RoadComponentRepository repository,
            EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    // --------------------------------------------------
    // READ ALL ACTIVE ROAD COMPONENTS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<RoadComponentView> findAll() {
        return repository
                .findAllByDeletedFalseOrDeletedIsNullOrderByIdAsc()
                .stream()
                .map(RoadComponentService::toView)
                .toList();
    }

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    public RoadComponentView create(RoadComponentForm form) {

        RoadComponentEntity entity = new RoadComponentEntity();

        applyForm(entity, form);

        entity.setDeleted(false);
        entity.setRevisionType(com.cdac.road.entities.AuditRevisionType.CREATED);

        return toView(repository.save(entity));
    }

    // --------------------------------------------------
    // UPDATE
    // --------------------------------------------------

    public RoadComponentView update(long id, RoadComponentForm form) {

        RoadComponentEntity entity = findActive(id);

        applyForm(entity, form);

        entity.setRevisionType(com.cdac.road.entities.AuditRevisionType.UPDATED);

        return toView(repository.save(entity));
    }

    // --------------------------------------------------
    // SOFT DELETE
    // --------------------------------------------------

    public void delete(long id) {

        RoadComponentEntity entity = findActive(id);

        entity.setDeleted(true);
        entity.setRevisionType(com.cdac.road.entities.AuditRevisionType.DELETED);

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
                        RoadComponentEntity.class,
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
    // ENUM DROPDOWN OPTIONS
    // --------------------------------------------------

    public List<String> findNatureOfWorks() {
        return Arrays.stream(NatureOfWork.values())
                .map(NatureOfWork::getLabel)
                .toList();
    }

    public List<String> findProposedRoadTypes() {
        return Arrays.stream(ProposedRoadType.values())
                .map(ProposedRoadType::getLabel)
                .toList();
    }

    public List<String> findCdWorkTypes() {
        return Arrays.stream(CdWorkType.values())
                .map(CdWorkType::getLabel)
                .toList();
    }

    public List<String> findRepairMaintenanceTypes() {
        return Arrays.stream(RepairMaintenanceType.values())
                .map(RepairMaintenanceType::getLabel)
                .toList();
    }

    public List<String> findWorkPlanTypes() {
        return Arrays.stream(WorkPlanType.values())
                .map(WorkPlanType::getLabel)
                .toList();
    }

    // --------------------------------------------------
    // APPLY FORM TO ENTITY
    // --------------------------------------------------

    private static void applyForm(
            RoadComponentEntity entity,
            RoadComponentForm form) {

        entity.setNatureOfWork(form.getNatureOfWork());
        entity.setProposedRoadType(form.getProposedRoadType());
        entity.setLength(form.getLength());
        entity.setWidth(form.getWidth());
        entity.setWorkPlanType(form.getWorkPlanType());
        entity.setCdWorkType(form.getCdWorkType());

        entity.setRepairMaintenanceType(
                form.getWorkPlanType() == WorkPlanType.NON_PLAN
                        ? form.getRepairMaintenanceType()
                        : null);

        entity.setOfficeId(form.getOfficeId());
        entity.setWorkId(form.getWorkId());
    }

    // --------------------------------------------------
    // FIND ACTIVE RECORD
    // --------------------------------------------------

    private RoadComponentEntity findActive(long id) {

        return repository
                .findByIdAndDeletedFalseOrIdAndDeletedIsNull(id, id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Road component " + id + " was not found."));
    }

    // --------------------------------------------------
    // ENTITY TO VIEW
    // --------------------------------------------------

    private static RoadComponentView toView(
            RoadComponentEntity entity) {

        return new RoadComponentView(
                entity.getId(),
                entity.getNatureOfWork() != null
                        ? entity.getNatureOfWork().getLabel() : null,
                entity.getProposedRoadType() != null
                        ? entity.getProposedRoadType().getLabel() : null,
                entity.getLength(),
                entity.getWidth(),
                entity.getWorkPlanType() != null
                        ? entity.getWorkPlanType().getLabel() : null,
                entity.getCdWorkType() != null
                        ? entity.getCdWorkType().getLabel() : null,
                entity.getRepairMaintenanceType() != null
                        ? entity.getRepairMaintenanceType().getLabel() : null,
                entity.getOfficeId(),
                entity.getWorkId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getRevisionTimestamp());
    }

    // --------------------------------------------------
    // AUDIT REVISION MAPPING
    // --------------------------------------------------

    private RoadAuditRevisionView toAuditRevision(Object[] row) {

        RoadComponentEntity entity = (RoadComponentEntity) row[0];
        DefaultRevisionEntity revision =
                (DefaultRevisionEntity) row[1];
        RevisionType revisionType = (RevisionType) row[2];

        if (entity == null) {
            throw new IllegalStateException(
                    "Envers returned an audit revision without its entity snapshot.");
        }

        String type = entity.getRevisionType() == null
                ? null
                : entity.getRevisionType().name();

        if (type == null) {
            type = switch (revisionType) {
                case ADD -> "CREATED";
                case MOD -> "UPDATED";
                case DEL -> "DELETED";
            };
        }

        return new RoadAuditRevisionView(
                revision.getId(),
                revision.getRevisionDate().toInstant(),
                type,
                toView(entity));
    }
}
