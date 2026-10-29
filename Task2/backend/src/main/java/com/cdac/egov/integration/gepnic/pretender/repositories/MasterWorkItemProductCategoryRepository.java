package com.cdac.egov.integration.gepnic.pretender.repositories;

import com.cdac.egov.integration.gepnic.pretender.entities.MasterWorkItemProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterWorkItemProductCategoryRepository extends JpaRepository<MasterWorkItemProductCategory, Long> {
    boolean existsByTenderCategory_Id(Long tenderCategoryId);
}
