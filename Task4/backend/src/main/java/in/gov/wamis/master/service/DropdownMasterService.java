package in.gov.wamis.master.service;

import in.gov.wamis.common.exception.ResourceNotFoundException;
import in.gov.wamis.master.entity.DropdownMaster;
import in.gov.wamis.master.form.DropdownMasterForm;
import in.gov.wamis.master.form.DropdownMasterResponseForm;
import in.gov.wamis.master.repository.DropdownMasterRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DropdownMasterService {

    private static final Logger log = LoggerFactory.getLogger(DropdownMasterService.class);

    private final DropdownMasterRepository dropdownRepository;

    public DropdownMasterService(DropdownMasterRepository dropdownRepository) {
        this.dropdownRepository = dropdownRepository;
    }

    @PostConstruct
    @Transactional
    public void seedInitialMasters() {
        if (dropdownRepository.count() > 0) {
            return;
        }
        log.info("Seeding initial WAMIS dropdown master values...");

        List<DropdownMaster> seedList = new ArrayList<>();

        // 1. TENDER_TYPE
        seedList.add(new DropdownMaster("TENDER_TYPE", "OPEN_TENDER", "Open Tender", "Standard open competitive tender", 1));
        seedList.add(new DropdownMaster("TENDER_TYPE", "NCB", "National Competitive Bid", "National Competitive Bidding", 2));
        seedList.add(new DropdownMaster("TENDER_TYPE", "LIMITED", "Limited Tender", "Limited competitive bidding", 3));
        seedList.add(new DropdownMaster("TENDER_TYPE", "SINGLE", "Single Tender", "Single source proprietary bidding", 4));

        // 2. FORM_OF_CONTRACT
        seedList.add(new DropdownMaster("FORM_OF_CONTRACT", "SUPPLY", "Supply", "Supply of goods/materials", 1));
        seedList.add(new DropdownMaster("FORM_OF_CONTRACT", "PERCENTAGE", "Percentage", "Percentage rate contract", 2));
        seedList.add(new DropdownMaster("FORM_OF_CONTRACT", "ITEM_RATE", "Item Rate", "Item rate unit contract", 3));
        seedList.add(new DropdownMaster("FORM_OF_CONTRACT", "LUMP_SUM", "Lump-sum", "Fixed lump-sum contract", 4));
        seedList.add(new DropdownMaster("FORM_OF_CONTRACT", "TURNKEY", "Turnkey", "Turnkey EPC contract", 5));

        // 3. TENDER_CATEGORY
        seedList.add(new DropdownMaster("TENDER_CATEGORY", "WORKS", "Works", "Civil and structural works", 1));
        seedList.add(new DropdownMaster("TENDER_CATEGORY", "GOODS", "Goods", "Equipment and materials", 2));
        seedList.add(new DropdownMaster("TENDER_CATEGORY", "SERVICES", "Services", "Consultancy and support services", 3));

        // 4. PRODUCT_CATEGORY
        seedList.add(new DropdownMaster("PRODUCT_CATEGORY", "CIVIL_WORKS", "Civil Works", "General civil engineering works", 1));
        seedList.add(new DropdownMaster("PRODUCT_CATEGORY", "CIVIL_ROADS", "Civil Works - Roads", "Highway and road development", 2));
        seedList.add(new DropdownMaster("PRODUCT_CATEGORY", "ELECTRICAL", "Electrical Works", "Electrical wiring and power supply", 3));
        seedList.add(new DropdownMaster("PRODUCT_CATEGORY", "WATER_SUPPLY", "Water Supply", "Piped water and canals", 4));

        // 5. TENDERER_CLASS
        seedList.add(new DropdownMaster("TENDERER_CLASS", "CLASS_A", "Class A", "Contractor registration Class A", 1));
        seedList.add(new DropdownMaster("TENDERER_CLASS", "CLASS_A_EQUIV", "A or equivalent", "Class A or equivalent standard", 2));
        seedList.add(new DropdownMaster("TENDERER_CLASS", "CLASS_B", "Class B", "Contractor registration Class B", 3));
        seedList.add(new DropdownMaster("TENDERER_CLASS", "SUPER_CLASS", "Super Class", "Highest category contractor", 4));

        // 6. BID_OPENERS
        seedList.add(new DropdownMaster("BID_OPENERS", "2_OFF_2", "2 Off 2", "2 openers required out of 2", 1));
        seedList.add(new DropdownMaster("BID_OPENERS", "2_OFF_3", "2 Off 3", "2 openers required out of 3", 2));
        seedList.add(new DropdownMaster("BID_OPENERS", "3_OFF_3", "3 Off 3", "3 openers required out of 3", 3));

        // 7. DOC_CODE
        seedList.add(new DropdownMaster("DOC_CODE", "NITDOC", "NIT Document (.pdf)", "Notice Inviting Tender", 1));
        seedList.add(new DropdownMaster("DOC_CODE", "BOQDOC", "Financial BOQ (.xls)", "Bill of Quantities spreadsheet", 2));
        seedList.add(new DropdownMaster("DOC_CODE", "DRAWINGDOC", "Engineering Drawings", "Technical drawings and plans", 3));
        seedList.add(new DropdownMaster("DOC_CODE", "ADDLDOC", "Additional Document", "Other mandatory certificates", 4));

        // 8. DIVISION_OFFICE
        seedList.add(new DropdownMaster("DIVISION_OFFICE", "CCE_CRWC_BBSR", "O/o CCE CRWC BBSR", "Office of Chief Construction Engineer BBSR", 1));
        seedList.add(new DropdownMaster("DIVISION_OFFICE", "DIV_PUNE", "Division Office Pune", "Public Works Division Pune", 2));
        seedList.add(new DropdownMaster("DIVISION_OFFICE", "DIV_MUMBAI", "Division Office Mumbai", "Public Works Division Mumbai", 3));
        seedList.add(new DropdownMaster("DIVISION_OFFICE", "DIV_NAGPUR", "Division Office Nagpur", "Public Works Division Nagpur", 4));

        dropdownRepository.saveAll(seedList);
        log.info("Seeded {} dropdown master records.", seedList.size());
    }

    @Transactional(readOnly = true)
    public Map<String, List<DropdownMasterResponseForm>> getAllGrouped() {
        List<DropdownMaster> list = dropdownRepository.findByActiveTrueOrderByCategoryAscDisplayOrderAsc();
        Map<String, List<DropdownMasterResponseForm>> result = new LinkedHashMap<>();
        for (DropdownMaster item : list) {
            result.computeIfAbsent(item.getCategory(), k -> new ArrayList<>())
                    .add(DropdownMasterResponseForm.fromEntity(item));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<DropdownMasterResponseForm> getByCategory(String category) {
        return dropdownRepository.findByCategoryAndActiveTrueOrderByDisplayOrderAsc(category).stream()
                .map(DropdownMasterResponseForm::fromEntity)
                .toList();
    }

    @Transactional
    public DropdownMasterResponseForm createOption(DropdownMasterForm form, String actor) {
        DropdownMaster entity = new DropdownMaster(
                form.getCategory().toUpperCase().trim(),
                form.getCode().toUpperCase().trim(),
                form.getDisplayName().trim(),
                form.getDescription(),
                form.getDisplayOrder()
        );
        entity.setActive(form.getActive() != null ? form.getActive() : true);
        entity.setCreatedBy(actor != null ? actor : "USER");
        entity.setUpdatedBy(actor != null ? actor : "USER");
        DropdownMaster saved = dropdownRepository.save(entity);
        return DropdownMasterResponseForm.fromEntity(saved);
    }

    @Transactional
    public DropdownMasterResponseForm updateOption(Long id, DropdownMasterForm form, String actor) {
        DropdownMaster entity = dropdownRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DropdownMaster", id));
        entity.setDisplayName(form.getDisplayName());
        entity.setDescription(form.getDescription());
        entity.setDisplayOrder(form.getDisplayOrder());
        if (form.getActive() != null) {
            entity.setActive(form.getActive());
        }
        entity.setUpdatedBy(actor != null ? actor : "USER");
        entity.setUpdatedAt(LocalDateTime.now());
        DropdownMaster saved = dropdownRepository.save(entity);
        return DropdownMasterResponseForm.fromEntity(saved);
    }

    @Transactional
    public void deleteOption(Long id) {
        DropdownMaster entity = dropdownRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DropdownMaster", id));
        entity.setActive(false);
        entity.setUpdatedAt(LocalDateTime.now());
        dropdownRepository.save(entity);
    }
}

