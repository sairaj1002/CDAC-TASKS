package in.gov.wamis.master.repository;

import in.gov.wamis.master.entity.MasterProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterProductCategoryRepository extends JpaRepository<MasterProductCategory, Long> {
    Optional<MasterProductCategory> findByNameAndTenderCategory_Id(String name, Long tenderCategoryId);
}
