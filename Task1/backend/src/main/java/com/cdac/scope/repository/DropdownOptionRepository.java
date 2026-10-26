package com.cdac.scope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cdac.scope.entities.DropdownOptionEntity;

@Repository
public interface DropdownOptionRepository extends JpaRepository<DropdownOptionEntity, Long> {

    List<DropdownOptionEntity> findByCategoryAndActiveTrueOrderByDisplayOrderAscIdAsc(String category);

    List<DropdownOptionEntity> findByCategoryOrderByDisplayOrderAscIdAsc(String category);

    List<DropdownOptionEntity> findAllByOrderByCategoryAscDisplayOrderAscIdAsc();

    Optional<DropdownOptionEntity> findByCategoryAndCode(String category, String code);

    boolean existsByCategoryAndCode(String category, String code);

    @Query("SELECT DISTINCT d.category FROM DropdownOptionEntity d ORDER BY d.category ASC")
    List<String> findDistinctCategories();
}
