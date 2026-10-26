package com.cdac.scope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cdac.scope.entities.BridgeEntity;

@Repository
public interface BridgeRepository extends JpaRepository<BridgeEntity, Long> {

    List<BridgeEntity> findAllByDeletedFalseOrDeletedIsNullOrderByScopeIdAsc();

    Optional<BridgeEntity> findByScopeIdAndDeletedFalseOrScopeIdAndDeletedIsNull(Long activeScopeId, Long legacyScopeId);

    @Query("""
        SELECT COUNT(b) > 0 FROM BridgeEntity b
        WHERE (b.deleted = false OR b.deleted IS NULL)
          AND b.bridgeActivity = :code
    """)
    boolean isOptionCodeInUse(@Param("code") String code);
}
