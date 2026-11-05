package in.gov.wamis.master.repository;

import in.gov.wamis.master.entity.MasterBidOpenersType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterBidOpenersTypeRepository extends JpaRepository<MasterBidOpenersType, Long> {
    Optional<MasterBidOpenersType> findByBoType(String boType);
}
