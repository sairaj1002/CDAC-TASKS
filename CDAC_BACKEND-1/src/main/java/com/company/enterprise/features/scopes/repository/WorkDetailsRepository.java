package com.company.enterprise.features.scopes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.enterprise.features.scopes.entity.WorkDetails;

public interface WorkDetailsRepository extends JpaRepository<WorkDetails, Long> {
    List<WorkDetails> findAllByOrderByWorkIdDesc();
}
