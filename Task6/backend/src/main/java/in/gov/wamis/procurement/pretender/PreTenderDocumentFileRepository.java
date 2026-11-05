package in.gov.wamis.procurement.pretender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreTenderDocumentFileRepository extends JpaRepository<PreTenderDocumentFile, Long> {
}
