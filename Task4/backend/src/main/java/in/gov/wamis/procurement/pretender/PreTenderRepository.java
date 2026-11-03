package in.gov.wamis.procurement.pretender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreTenderRepository extends JpaRepository<PreTender, Long> {

    Optional<PreTender> findByUniqueFileNo(String uniqueFileNo);

    Optional<PreTender> findByTenderReferenceNo(String tenderReferenceNo);

    List<PreTender> findByWorkId(Long workId);

    List<PreTender> findByOfficeId(Long officeId);
}

