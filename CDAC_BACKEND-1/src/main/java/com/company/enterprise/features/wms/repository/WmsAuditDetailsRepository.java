package com.company.enterprise.features.wms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.enterprise.features.wms.entity.WmsAuditDetailsEntity;

public interface WmsAuditDetailsRepository extends JpaRepository<WmsAuditDetailsEntity, Long> {

    List<WmsAuditDetailsEntity> findAllByWorkRegNoOrderByRevisionAtDescAuditIdDesc(Long workRegNo);

    Optional<WmsAuditDetailsEntity> findFirstByWorkRegNoOrderByRevisionAtAscAuditIdAsc(Long workRegNo);
}
