package com.cdac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cdac.scope.constant.BridgeConstant;
import com.cdac.scope.entities.AuditRevisionType;
import com.cdac.scope.entities.BridgeEntity;
import com.cdac.scope.forms.BridgeAuditRevisionView;
import com.cdac.scope.forms.BridgeForm;
import com.cdac.scope.forms.BridgeView;
import com.cdac.scope.repository.BridgeRepository;
import com.cdac.scope.service.BridgeService;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private BridgeService bridgeService;

	@Autowired
	private BridgeRepository bridgeRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void recordsCreateUpdateAndDeleteRevisions() {
		BridgeForm initialForm = new BridgeForm(
				"River A",
				new BigDecimal("3.500"),
				new BigDecimal("10.000"),
				"Road A",
				2,
				new BigDecimal("5.000"),
				BridgeConstant.Activity.REPAIR_TO_BRIDGE_CODE,
				null,
				12,
				34,
				99);
		BridgeView created = bridgeService.create(initialForm);

		BridgeForm updatedForm = new BridgeForm(
				"River B",
				new BigDecimal("4.000"),
				new BigDecimal("10.000"),
				"Road A",
				2,
				new BigDecimal("5.000"),
				BridgeConstant.Activity.REPAIR_TO_BRIDGE_CODE,
				null,
				12,
				34,
				99);
		bridgeService.update(created.getScopeId(), updatedForm);
		bridgeService.delete(created.getScopeId());

		List<BridgeAuditRevisionView> revisions =
				bridgeService.findAuditHistory(created.getScopeId());

		assertThat(revisions)
				.extracting(BridgeAuditRevisionView::getRevisionType)
				.containsExactly(BridgeConstant.Audit.CREATED, BridgeConstant.Audit.UPDATED, BridgeConstant.Audit.DELETED);
		assertThat(revisions.get(0).getSnapshot().getRiverNallahBarrier()).isEqualTo("River A");
		assertThat(revisions.get(1).getSnapshot().getRiverNallahBarrier()).isEqualTo("River B");
		assertThat(revisions.get(2).getSnapshot().getRiverNallahBarrier()).isEqualTo("River B");
		assertThat(revisions.get(0).getSnapshot().getOfficeId()).isEqualTo(12);
		assertThat(revisions.get(0).getSnapshot().getWorkId()).isEqualTo(34);
		assertThat(revisions.get(0).getSnapshot().getTenantId()).isEqualTo(99);
		assertThat(revisions.get(0).getSnapshot().getScopeId()).isEqualTo(created.getScopeId());
		assertThat(revisions.get(0).getRevisionDate()).isNotNull();
		assertThat(revisions).allSatisfy(revision -> {
			assertThat(revision.getSnapshot().getCreatedAt()).isNotNull();
			assertThat(revision.getSnapshot().getUpdatedAt()).isNotNull();
			assertThat(revision.getSnapshot().getRevisionTimestamp()).isNotNull();
		});
		assertThat(jdbcTemplate.queryForList(
				"select revision_type from bridge_component_aud where scope_id = ? order by rev",
				String.class,
				created.getScopeId()))
				.containsExactly("CREATED", "UPDATED", "DELETED");
		assertThat(jdbcTemplate.queryForObject(
				"select count(*) from revinfo",
				Integer.class))
				.isGreaterThanOrEqualTo(3);
		assertThat(jdbcTemplate.queryForObject(
				"select count(*) from bridge_component_aud where scope_id = ? and created_at is not null and updated_at is not null and revision_timestamp is not null",
				Integer.class,
				created.getScopeId()))
				.isEqualTo(3);
		assertThat(jdbcTemplate.queryForList(
				"select distinct cast(revtype as integer) from bridge_component_aud where scope_id = ?",
				Integer.class,
				created.getScopeId()))
				.containsOnly(0, 1);
		BridgeEntity deletedEntity =
				bridgeRepository.findById(created.getScopeId()).orElseThrow();
		assertThat(deletedEntity.getDeleted()).isTrue();
		assertThat(deletedEntity.getRevisionType()).isEqualTo(AuditRevisionType.DELETED);
		assertThat(deletedEntity.getTenantId()).isEqualTo(99);
		assertThat(jdbcTemplate.queryForObject(
				"select revision_type from bridge_component where scope_id = ?",
				String.class,
				created.getScopeId()))
				.isEqualTo(BridgeConstant.Audit.DELETED);
	}

	@Test
	void softDeletedComponentsAreNotReturnedOrEditable() {
		BridgeForm form = new BridgeForm(
				"River C",
				null,
				null,
				"Road C",
				null,
				null,
				BridgeConstant.Activity.REPAIR_TO_BRIDGE_CODE,
				null,
				null,
				null,
				null);
		BridgeView created = bridgeService.create(form);

		bridgeService.delete(created.getScopeId());

		BridgeEntity deletedEntity =
				bridgeRepository.findById(created.getScopeId()).orElseThrow();
		assertThat(deletedEntity.getDeleted()).isTrue();
		assertThat(deletedEntity.getRevisionType()).isEqualTo(AuditRevisionType.DELETED);
		assertThat(bridgeService.findAll())
				.noneMatch(component -> component.getScopeId().equals(created.getScopeId()));
		assertThatThrownBy(() -> bridgeService.delete(created.getScopeId()))
				.isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
	}

	@Test
	void returnsEmptyAuditHistoryWhenNoRevisionsExist() {
		assertThat(bridgeService.findAuditHistory(Long.MAX_VALUE)).isEmpty();
	}
}
