
package com.cdac.egov.wms.work.subwork.others.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.egov.wms.work.subwork.others.entity.OthersAudit;

public interface OthersAuditRepository
        extends JpaRepository<OthersAudit, Long> {

    List<OthersAudit> findByOthersIdOrderByTimestampDesc(Long othersId);
}

