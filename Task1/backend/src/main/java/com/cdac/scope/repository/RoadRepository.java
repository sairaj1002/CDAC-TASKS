package com.cdac.scope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cdac.scope.entities.RoadEntity;

@Repository
public interface RoadRepository extends JpaRepository<RoadEntity, Long> {

    List<RoadEntity> findAllByDeletedFalseOrDeletedIsNullOrderByScopeIdAsc();

    Optional<RoadEntity> findByScopeIdAndDeletedFalseOrScopeIdAndDeletedIsNull(
            Long activeScopeId,
            Long legacyScopeId
    );

    @Query("""
        SELECT COUNT(r) > 0 FROM RoadEntity r
        WHERE (r.deleted = false OR r.deleted IS NULL)
          AND (
            r.natureOfWork = :code OR
            r.proposedRoadType = :code OR
            r.workPlanType = :code OR
            r.cdWorkType = :code OR
            r.repairMaintenanceType = :code
          )
    """)
    boolean isOptionCodeInUse(@Param("code") String code);
}
