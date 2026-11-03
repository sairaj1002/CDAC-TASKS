package com.cdac.egov.wms.work.subwork.building.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cdac.egov.wms.work.subwork.building.entity.Building;

public interface BuildingRepository extends
        JpaRepository<Building, Long>,
        JpaSpecificationExecutor<Building> {
}
        
