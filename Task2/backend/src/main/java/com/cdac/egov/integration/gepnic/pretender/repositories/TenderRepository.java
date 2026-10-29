package com.cdac.egov.integration.gepnic.pretender.repositories;

import com.cdac.egov.integration.gepnic.pretender.entities.Tender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderRepository extends JpaRepository<Tender, Long> {}
