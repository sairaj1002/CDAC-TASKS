package in.gov.wamis.master.repository;

import in.gov.wamis.master.entity.MasterTenderCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterTenderCategoryRepository extends JpaRepository<MasterTenderCategory, Long> {
    Optional<MasterTenderCategory> findByCategory(String category);
}
