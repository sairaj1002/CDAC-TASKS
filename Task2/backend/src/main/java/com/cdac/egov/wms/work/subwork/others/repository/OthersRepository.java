package com.cdac.egov.wms.work.subwork.others.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.egov.wms.work.subwork.others.entity.Others;

public interface OthersRepository extends JpaRepository<Others, Long> {

    Page<Others> findByDeletedFalse(Pageable pageable);
}
