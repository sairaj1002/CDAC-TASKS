package in.gov.wamis.master.repository;

import in.gov.wamis.master.entity.DropdownMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DropdownMasterRepository extends JpaRepository<DropdownMaster, Long> {

    List<DropdownMaster> findByCategoryAndActiveTrueOrderByDisplayOrderAsc(String category);

    List<DropdownMaster> findByActiveTrueOrderByCategoryAscDisplayOrderAsc();

    List<DropdownMaster> findAllByOrderByCategoryAscDisplayOrderAsc();
}

