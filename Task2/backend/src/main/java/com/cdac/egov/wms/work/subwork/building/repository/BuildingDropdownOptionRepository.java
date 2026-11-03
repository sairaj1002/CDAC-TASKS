package com.cdac.egov.wms.work.subwork.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cdac.egov.wms.work.subwork.building.entity.DropdownOption;

public interface BuildingDropdownOptionRepository
        extends JpaRepository<DropdownOption, Long> {

    @Query("SELECT d FROM DropdownOption d WHERE d.category = :category AND d.active = TRUE ORDER BY d.id asc")
    List<DropdownOption> findByCategoryAndActiveTrueOrderById(String category);

    boolean existsByCategoryAndLabel(String category, String label);
}
