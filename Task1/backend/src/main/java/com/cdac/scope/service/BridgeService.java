package com.cdac.scope.service;

import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.scope.constant.BridgeConstant;
import com.cdac.scope.forms.DropdownOptionDto;
import com.cdac.scope.entities.AuditRevisionType;
import com.cdac.scope.entities.BridgeEntity;
import com.cdac.scope.forms.BridgeAuditRevisionView;
import com.cdac.scope.forms.BridgeForm;
import com.cdac.scope.forms.BridgeView;
import com.cdac.scope.repository.BridgeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class BridgeService {

	private final BridgeRepository repository;
	private final EntityManager entityManager;
	private final DropdownOptionService dropdownService;

	public BridgeService(
			BridgeRepository repository,
			EntityManager entityManager,
			DropdownOptionService dropdownService) {
		this.repository = repository;
		this.entityManager = entityManager;
		this.dropdownService = dropdownService;
	}

	@Transactional(readOnly = true)
	public List<BridgeView> findAll() {
		return repository.findAllByDeletedFalseOrDeletedIsNullOrderByScopeIdAsc().stream()
				.map(this::toView)
				.toList();
	}

	public BridgeView create(BridgeForm form) {
		BridgeEntity entity = new BridgeEntity();
		applyForm(entity, form);
		entity.setDeleted(false);
		entity.setRevisionType(AuditRevisionType.CREATED);
		return toView(repository.save(entity));
	}

	public BridgeView update(long id, BridgeForm form) {
		BridgeEntity entity = findActive(id);
		applyForm(entity, form);
		entity.setRevisionType(AuditRevisionType.UPDATED);
		return toView(repository.save(entity));
	}

	public void delete(long id) {
		BridgeEntity entity = findActive(id);
		entity.setDeleted(true);
		entity.setRevisionType(AuditRevisionType.DELETED);
		repository.save(entity);
	}

	@Transactional(readOnly = true)
	public List<BridgeAuditRevisionView> findAuditHistory(long id) {
		AuditReader reader = AuditReaderFactory.get(entityManager);
		List<?> rows = reader.createQuery()
				.forRevisionsOfEntity(BridgeEntity.class, false, true)
				.add(AuditEntity.id().eq(id))
				.addOrder(AuditEntity.revisionNumber().asc())
				.getResultList();

		return rows.stream()
				.map(row -> toAuditRevision((Object[]) row))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<String> findBridgeActivities() {
		return dropdownService.findActiveByCategory(BridgeConstant.Activity.CATEGORY_BRIDGE_ACTIVITY).stream()
				.map(DropdownOptionDto::label)
				.toList();
	}

	private void applyForm(BridgeEntity entity, BridgeForm form) {
		dropdownService.validateActiveCode(BridgeConstant.Activity.CATEGORY_BRIDGE_ACTIVITY, form.getBridgeActivity());

		entity.setRiverNallahBarrier(form.getRiverNallahBarrier().trim());
		entity.setWidth(form.getWidth());
		entity.setLength(form.getLength());
		entity.setApprovalRoad(form.getApprovalRoad().trim());
		entity.setNumberOfSpans(form.getNumberOfSpans());
		entity.setSpanLength(form.getSpanLength());
		entity.setBridgeActivity(form.getBridgeActivity().trim());

		boolean isOther = BridgeConstant.Activity.isOtherActivity(form.getBridgeActivity());

		entity.setOtherActivityDescription(
				isOther && form.getOtherActivityDescription() != null
						? form.getOtherActivityDescription().trim()
						: null);
		entity.setOfficeId(form.getOfficeId());
		entity.setWorkId(form.getWorkId());
		entity.setTenantId(form.getTenantId());
	}

	private BridgeAuditRevisionView toAuditRevision(Object[] row) {
		BridgeEntity entity = (BridgeEntity) row[0];
		DefaultRevisionEntity revision = (DefaultRevisionEntity) row[1];
		RevisionType revisionType = (RevisionType) row[2];

		if (entity == null) {
			throw new IllegalStateException(BridgeConstant.Messages.ENVERS_SNAPSHOT_MISSING);
		}

		String type = entity.getRevisionType() == null ? null : entity.getRevisionType().name();
		if (type == null) {
			type = switch (revisionType) {
				case ADD -> BridgeConstant.Audit.CREATED;
				case MOD -> BridgeConstant.Audit.UPDATED;
				case DEL -> BridgeConstant.Audit.DELETED;
			};
		}

		return new BridgeAuditRevisionView(
				revision.getId(),
				revision.getRevisionDate().toInstant(),
				type,
				toView(entity));
	}

	private BridgeView toView(BridgeEntity entity) {
		return new BridgeView(
				entity.getScopeId(),
				entity.getRiverNallahBarrier(),
				entity.getWidth(),
				entity.getLength(),
				entity.getApprovalRoad(),
				entity.getNumberOfSpans(),
				entity.getSpanLength(),
				dropdownService.getLabel(BridgeConstant.Activity.CATEGORY_BRIDGE_ACTIVITY, entity.getBridgeActivity()),
				entity.getOtherActivityDescription(),
				entity.getOfficeId(),
				entity.getWorkId(),
				entity.getTenantId(),
				entity.getCreatedAt(),
				entity.getUpdatedAt(),
				entity.getRevisionTimestamp());
	}

	private BridgeEntity findActive(long id) {
		return repository.findByScopeIdAndDeletedFalseOrScopeIdAndDeletedIsNull(id, id)
				.orElseThrow(() -> new EntityNotFoundException(BridgeConstant.Messages.notFound(id)));
	}
}
