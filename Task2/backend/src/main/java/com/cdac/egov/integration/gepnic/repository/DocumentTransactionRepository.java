package com.cdac.egov.integration.gepnic.repository;

import com.cdac.egov.integration.gepnic.entity.DocumentTransaction;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTransactionRepository extends JpaRepository<DocumentTransaction, Long> {

    List<DocumentTransaction> findByTenderReference(String tenderReference);

    Optional<DocumentTransaction> findByUniqueFileNoAndDocUniqueId(String uniqueFileNo, String docUniqueId);

    Page<DocumentTransaction> findByUploadStatus(IntegrationStatus uploadStatus, Pageable pageable);
}
