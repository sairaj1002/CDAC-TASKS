package in.gov.wamis.integration.transaction.repository;

import in.gov.wamis.integration.transaction.entity.IntegrationTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntegrationTransactionRepository extends JpaRepository<IntegrationTransaction, Long> {

    Optional<IntegrationTransaction> findByCorrelationId(String correlationId);

    Optional<IntegrationTransaction> findByIdempotencyKey(String idempotencyKey);

    List<IntegrationTransaction> findByPreTenderIdOrderByCreatedAtDesc(Long preTenderId);

    List<IntegrationTransaction> findByPortalCodeOrderByCreatedAtDesc(String portalCode);
}

