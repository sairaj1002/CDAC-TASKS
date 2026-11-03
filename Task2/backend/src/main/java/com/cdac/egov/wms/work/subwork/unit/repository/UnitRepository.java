package com.cdac.egov.wms.work.subwork.unit.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.egov.wms.work.subwork.unit.entity.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long> {

    List<Unit> findByActiveTrueOrderByShortNameAsc();

    java.util.Optional<Unit> findByShortName(String shortName);

    boolean existsByShortName(String shortName);
}
