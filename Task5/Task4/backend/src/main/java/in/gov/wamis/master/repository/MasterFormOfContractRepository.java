package in.gov.wamis.master.repository;

import in.gov.wamis.master.entity.MasterFormOfContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterFormOfContractRepository extends JpaRepository<MasterFormOfContract, Long> {
    Optional<MasterFormOfContract> findByFormOfContract(String formOfContract);
}
