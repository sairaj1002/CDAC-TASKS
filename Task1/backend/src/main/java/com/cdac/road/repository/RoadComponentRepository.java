package com.cdac.road.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.road.entities.RoadComponentEntity;

public interface RoadComponentRepository
        extends JpaRepository<RoadComponentEntity, Long> {

    List<RoadComponentEntity> findAllByDeletedFalseOrDeletedIsNullOrderByIdAsc();

    Optional<RoadComponentEntity> findByIdAndDeletedFalseOrIdAndDeletedIsNull(
            Long activeId,
            Long legacyId
    );
}