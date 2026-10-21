package com.cdac.bridge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.bridge.entities.BridgeComponentEntity;

public interface BridgeComponentRepository extends JpaRepository<BridgeComponentEntity, Long> {
	List<BridgeComponentEntity> findAllByDeletedFalseOrDeletedIsNullOrderByIdAsc();

	Optional<BridgeComponentEntity> findByIdAndDeletedFalseOrIdAndDeletedIsNull(Long activeId, Long legacyId);
}
