package com.cdac.egov.integration.gepnic.repository;

import com.cdac.egov.integration.gepnic.entity.IntegrationTransaction;
import com.cdac.egov.integration.gepnic.enums.IntegrationOperation;
import com.cdac.egov.integration.gepnic.enums.IntegrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IntegrationTransactionRepository extends JpaRepository<IntegrationTransaction, Long> {

    Optional<IntegrationTransaction> findByTransactionId(String transactionId);

    Optional<IntegrationTransaction> findByCorrelationIdAndOperation(String correlationId, IntegrationOperation operation);

    Page<IntegrationTransaction> findByOperation(IntegrationOperation operation, Pageable pageable);

    Page<IntegrationTransaction> findByStatus(IntegrationStatus status, Pageable pageable);

    @Query("SELECT t FROM IntegrationTransaction t WHERE " +
           "(:operation IS NULL OR t.operation = :operation) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:correlationId IS NULL OR LOWER(t.correlationId) LIKE LOWER(CONCAT('%', :correlationId, '%'))) AND " +
           "(:fromDate IS NULL OR t.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR t.createdAt <= :toDate)")
    Page<IntegrationTransaction> searchTransactions(
            @Param("operation") IntegrationOperation operation,
            @Param("status") IntegrationStatus status,
            @Param("correlationId") String correlationId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    long countByStatus(IntegrationStatus status);

    long countByOperation(IntegrationOperation operation);
}
