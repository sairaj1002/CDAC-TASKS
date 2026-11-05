package in.gov.wamis.master.repository;

import in.gov.wamis.master.entity.MasterTenderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterTenderTypeRepository extends JpaRepository<MasterTenderType, Long> {
    Optional<MasterTenderType> findByTenderType(String tenderType);
}
