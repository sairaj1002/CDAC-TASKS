package com.cdac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cdac.bridge.entities.BridgeActivity;
import com.cdac.bridge.entities.AuditRevisionType;
import com.cdac.bridge.entities.BridgeComponentEntity;
import com.cdac.bridge.forms.BridgeAuditRevisionView;
import com.cdac.bridge.forms.BridgeComponentForm;
import com.cdac.bridge.forms.BridgeComponentView;
import com.cdac.bridge.repository.BridgeComponentRepository;
import com.cdac.bridge.service.BridgeComponentService;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private BridgeComponentService bridgeComponentService;

	@Autowired
	private BridgeComponentRepository bridgeComponentRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void recordsCreateUpdateAndDeleteRevisions() {
		BridgeComponentForm initialForm = new BridgeComponentForm(
				"River A",
				new BigDecimal("3.500"),
				new BigDecimal("10.000"),
				"Road A",
				2,
				new BigDecimal("5.000"),
				BridgeActivity.REPAIR_TO_BRIDGE,
				null,
				12,
				34);
		BridgeComponentView created = bridgeComponentService.create(initialForm);

		BridgeComponentForm updatedForm = new BridgeComponentForm(
				"River B",
				new BigDecimal("4.000"),
				new BigDecimal("10.000"),
				"Road A",
				2,
				new BigDecimal("5.000"),
				BridgeActivity.REPAIR_TO_BRIDGE,
				null,
				12,
				34);
		bridgeComponentService.update(created.getId(), updatedForm);
		bridgeComponentService.delete(created.getId());

		List<BridgeAuditRevisionView> revisions =
				bridgeComponentService.findAuditHistory(created.getId());

		assertThat(revisions)
				.extracting(BridgeAuditRevisionView::getRevisionType)
				.containsExactly("CREATED", "UPDATED", "DELETED");
		assertThat(revisions.get(0).getSnapshot().getRiverNallahBarrier()).isEqualTo("River A");
		assertThat(revisions.get(1).getSnapshot().getRiverNallahBarrier()).isEqualTo("River B");
		assertThat(revisions.get(2).getSnapshot().getRiverNallahBarrier()).isEqualTo("River B");
		assertThat(revisions.get(0).getSnapshot().getOfficeId()).isEqualTo(12);
		assertThat(revisions.get(0).getSnapshot().getWorkId()).isEqualTo(34);
		assertThat(revisions.get(0).getRevisionDate()).isNotNull();
		assertThat(revisions).allSatisfy(revision -> {
			assertThat(revision.getSnapshot().getCreatedAt()).isNotNull();
			assertThat(revision.getSnapshot().getUpdatedAt()).isNotNull();
			assertThat(revision.getSnapshot().getRevisionTimestamp()).isNotNull();
		});
		assertThat(jdbcTemplate.queryForList(
				"select revision_type from bridge_component_aud where id = ? order by rev",
				String.class,
				created.getId()))
				.containsExactly("CREATED", "UPDATED", "DELETED");
		assertThat(jdbcTemplate.queryForObject(
				"select count(*) from revinfo",
				Integer.class))
				.isGreaterThanOrEqualTo(3);
		assertThat(jdbcTemplate.queryForObject(
				"select count(*) from bridge_component_aud where id = ? and created_at is not null and updated_at is not null and revision_timestamp is not null",
				Integer.class,
				created.getId()))
				.isEqualTo(3);
		assertThat(jdbcTemplate.queryForList(
				"select distinct cast(revtype as integer) from bridge_component_aud where id = ?",
				Integer.class,
				created.getId()))
				.containsOnly(0, 1);
		BridgeComponentEntity deletedEntity =
				bridgeComponentRepository.findById(created.getId()).orElseThrow();
		assertThat(deletedEntity.getDeleted()).isTrue();
		assertThat(deletedEntity.getRevisionType()).isEqualTo(AuditRevisionType.DELETED);
		assertThat(jdbcTemplate.queryForObject(
				"select revision_type from bridge_component where id = ?",
				String.class,
				created.getId()))
				.isEqualTo("DELETED");
	}

	@Test
	void softDeletedComponentsAreNotReturnedOrEditable() {
		BridgeComponentForm form = new BridgeComponentForm(
				"River C",
				null,
				null,
				"Road C",
				null,
				null,
				BridgeActivity.REPAIR_TO_BRIDGE,
				null,
				null,
				null);
		BridgeComponentView created = bridgeComponentService.create(form);

		bridgeComponentService.delete(created.getId());

		BridgeComponentEntity deletedEntity =
				bridgeComponentRepository.findById(created.getId()).orElseThrow();
		assertThat(deletedEntity.getDeleted()).isTrue();
		assertThat(deletedEntity.getRevisionType()).isEqualTo(AuditRevisionType.DELETED);
		assertThat(bridgeComponentService.findAll())
				.noneMatch(component -> component.getId().equals(created.getId()));
		assertThatThrownBy(() -> bridgeComponentService.delete(created.getId()))
				.isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
	}

	@Test
	void returnsEmptyAuditHistoryWhenNoRevisionsExist() {
		assertThat(bridgeComponentService.findAuditHistory(Long.MAX_VALUE)).isEmpty();
	}
}
