package com.cdac.bridge.service;

import java.util.Arrays;
import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.bridge.entities.BridgeActivity;
import com.cdac.bridge.entities.AuditRevisionType;
import com.cdac.bridge.entities.BridgeComponentEntity;
import com.cdac.bridge.forms.BridgeAuditRevisionView;
import com.cdac.bridge.forms.BridgeComponentForm;
import com.cdac.bridge.forms.BridgeComponentView;
import com.cdac.bridge.repository.BridgeComponentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.envers.DefaultRevisionEntity;

@Service
@Transactional
public class BridgeComponentService {

	private final BridgeComponentRepository repository;
	private final EntityManager entityManager;

	public BridgeComponentService(BridgeComponentRepository repository, EntityManager entityManager) {
		this.repository = repository;
		this.entityManager = entityManager;
	}

	@Transactional(readOnly = true)
	public List<BridgeComponentView> findAll() {
		return repository.findAllByDeletedFalseOrDeletedIsNullOrderByIdAsc().stream()
				.map(BridgeComponentService::toView)
				.toList();
	}

	public BridgeComponentView create(BridgeComponentForm form) {
		BridgeComponentEntity entity = new BridgeComponentEntity();
		applyForm(entity, form);
		entity.setDeleted(false);
		entity.setRevisionType(AuditRevisionType.CREATED);
		return toView(repository.save(entity));
	}

	public BridgeComponentView update(long id, BridgeComponentForm form) {
		BridgeComponentEntity entity = findActive(id);
		applyForm(entity, form);
		entity.setRevisionType(AuditRevisionType.UPDATED);
		return toView(repository.save(entity));
	}

	public void delete(long id) {
		BridgeComponentEntity entity = findActive(id);
		entity.setDeleted(true);
		entity.setRevisionType(AuditRevisionType.DELETED);
		repository.save(entity);
	}

	@Transactional(readOnly = true)
	public List<BridgeAuditRevisionView> findAuditHistory(long id) {
		AuditReader reader = AuditReaderFactory.get(entityManager);
		List<?> rows = reader.createQuery()
				.forRevisionsOfEntity(BridgeComponentEntity.class, false, true)
				.add(AuditEntity.id().eq(id))
				.addOrder(AuditEntity.revisionNumber().asc())
				.getResultList();

		return rows.stream()
				.map(row -> toAuditRevision((Object[]) row))
				.toList();
	}

	public List<String> findBridgeActivities() {
		return Arrays.stream(BridgeActivity.values())
				.map(BridgeActivity::getValue)
				.toList();
	}

	private static void applyForm(BridgeComponentEntity entity, BridgeComponentForm form) {
		entity.setRiverNallahBarrier(form.getRiverNallahBarrier().trim());
		entity.setWidth(form.getWidth());
		entity.setLength(form.getLength());
		entity.setApprovalRoad(form.getApprovalRoad().trim());
		entity.setNumberOfSpans(form.getNumberOfSpans());
		entity.setSpanLength(form.getSpanLength());
		entity.setBridgeActivity(form.getBridgeActivity().getValue());
		entity.setOtherActivityDescription(
				form.getBridgeActivity() == BridgeActivity.OTHER_ACTIVITY
						? form.getOtherActivityDescription().trim()
						: null);
		entity.setOfficeId(form.getOfficeId());
		entity.setWorkId(form.getWorkId());
	}

	private static BridgeAuditRevisionView toAuditRevision(Object[] row) {
		BridgeComponentEntity entity = (BridgeComponentEntity) row[0];
		DefaultRevisionEntity revision = (DefaultRevisionEntity) row[1];
		RevisionType revisionType = (RevisionType) row[2];

		if (entity == null) {
			throw new IllegalStateException("Envers returned an audit revision without its entity snapshot.");
		}

		String type = entity.getRevisionType() == null ? null : entity.getRevisionType().name();
		if (type == null) {
			type = switch (revisionType) {
				case ADD -> "CREATED";
				case MOD -> "UPDATED";
				case DEL -> "DELETED";
			};
		}
		return new BridgeAuditRevisionView(
				revision.getId(),
				revision.getRevisionDate().toInstant(),
				type,
				toView(entity));
	}

	private BridgeComponentEntity findActive(long id) {
		return repository.findByIdAndDeletedFalseOrIdAndDeletedIsNull(id, id)
				.orElseThrow(() -> new EntityNotFoundException("Bridge component " + id + " was not found."));
	}

	private static BridgeComponentView toView(BridgeComponentEntity entity) {
		return new BridgeComponentView(
				entity.getId(),
				entity.getRiverNallahBarrier(),
				entity.getWidth(),
				entity.getLength(),
				entity.getApprovalRoad(),
				entity.getNumberOfSpans(),
				entity.getSpanLength(),
				entity.getBridgeActivity(),
				entity.getOtherActivityDescription(),
				entity.getOfficeId(),
				entity.getWorkId(),
				entity.getCreatedAt(),
				entity.getUpdatedAt(),
				entity.getRevisionTimestamp());
	}
}
