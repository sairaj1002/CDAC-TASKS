package in.gov.wamis.audit.repository;

import in.gov.wamis.audit.entity.IntegrationAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationAuditRepository extends JpaRepository<IntegrationAuditEvent, Long> {

    List<IntegrationAuditEvent> findByTransactionIdOrderByEventTimestampAsc(Long transactionId);

    List<IntegrationAuditEvent> findByCorrelationIdOrderByEventTimestampAsc(String correlationId);
}

