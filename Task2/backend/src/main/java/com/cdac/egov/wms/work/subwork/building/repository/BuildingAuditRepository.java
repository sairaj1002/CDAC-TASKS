package com.cdac.egov.wms.work.subwork.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.egov.wms.work.subwork.building.entity.BuildingAudit;

public interface BuildingAuditRepository
        extends JpaRepository<BuildingAudit, Long> {

    List<BuildingAudit> findByBuildingIdOrderByTimestampDesc(Long buildingId);
}

