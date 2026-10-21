package com.company.enterprise.features.wms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.company.enterprise.features.wms.entity.WorkEntity;

public interface WorkRepository extends JpaRepository<WorkEntity, Long>, JpaSpecificationExecutor<WorkEntity> {
}
