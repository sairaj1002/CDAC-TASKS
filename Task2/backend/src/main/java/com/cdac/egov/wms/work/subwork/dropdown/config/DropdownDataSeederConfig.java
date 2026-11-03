package com.cdac.egov.wms.work.subwork.dropdown.config;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.egov.wms.work.subwork.dropdown.entity.DropdownOptionEntity;
import com.cdac.egov.wms.work.subwork.dropdown.repository.DropdownOptionRepository;

import jakarta.annotation.PostConstruct;


/*------------------------------------------------------- */
/* Dropdown Data Seeder for Roads and Bridge Configuration */
/*------------------------------------------------------- */
@Configuration
public class DropdownDataSeederConfig {

    private static final Logger log = LoggerFactory.getLogger(DropdownDataSeederConfig.class);

    private final DropdownOptionRepository repository;

    public DropdownDataSeederConfig(DropdownOptionRepository repository) {
        this.repository = repository;
    }

    /**
     * Seed initial options before requests are served.
     * Guaranteed to be idempotent: will never overwrite admin changes.
     */
    @PostConstruct
    public void preInit() {
        seedDefaults(repository);
    }

    public record SeedItem(String category, String code, String label, int displayOrder) {}

    public static final List<SeedItem> DEFAULT_OPTIONS = List.of(
        // ROAD_TYPE
        new SeedItem("ROAD_TYPE", "MURUM_EARTHEN", "Murum (Earthen)", 1),
        new SeedItem("ROAD_TYPE", "WBM_METAL", "WBM (Metal)", 2),
        new SeedItem("ROAD_TYPE", "BT_DAMBRI", "BT (Dambri)", 3),
        new SeedItem("ROAD_TYPE", "CONCRETE", "Concrete", 4),

        // NATURE_OF_WORK
        new SeedItem("NATURE_OF_WORK", "NEW_CONSTRUCTION", "New Construction", 1),
        new SeedItem("NATURE_OF_WORK", "MAINTENANCE", "Maintenance", 2),
        new SeedItem("NATURE_OF_WORK", "IMPROVEMENT", "Improvement", 3),
        new SeedItem("NATURE_OF_WORK", "RECONSTRUCTION", "Reconstruction", 4),

        // REPAIR_MAINTENANCE_TYPE
        new SeedItem("REPAIR_MAINTENANCE_TYPE", "CURRENT_REPAIR", "Current repair", 1),
        new SeedItem("REPAIR_MAINTENANCE_TYPE", "SPECIAL_REPAIR", "Special Repair", 2),
        new SeedItem("REPAIR_MAINTENANCE_TYPE", "FDR", "FDR", 3),

        // BRIDGE_ACTIVITY
        new SeedItem("BRIDGE_ACTIVITY", "FOUNDATION", "Foundation", 1),
        new SeedItem("BRIDGE_ACTIVITY", "ABUTMENT", "Abutment", 2),
        new SeedItem("BRIDGE_ACTIVITY", "PIERS", "Piers", 3),
        new SeedItem("BRIDGE_ACTIVITY", "SUPER_STRUCTURES", "Super Structures", 4),
        new SeedItem("BRIDGE_ACTIVITY", "BEARING", "Bearing", 5),
        new SeedItem("BRIDGE_ACTIVITY", "EXPANSION_JOINTS", "Expansion Joints", 6),
        new SeedItem("BRIDGE_ACTIVITY", "RAILING", "Railing", 7),
        new SeedItem("BRIDGE_ACTIVITY", "WEARING_COAT", "Wearing Coat", 8),
        new SeedItem("BRIDGE_ACTIVITY", "RETURNS", "Returns", 9),
        new SeedItem("BRIDGE_ACTIVITY", "APPROACH_ROAD", "Approach Road", 10),
        new SeedItem("BRIDGE_ACTIVITY", "REPAIR_TO_BRIDGE", "Repair to Bridge", 11)
    );

    @Bean
    @Order(1)
    public ApplicationRunner seedDropdownOptions() {
        return args -> seedDefaults(this.repository);
    }

    /**
     * Idempotent seeding logic:
     * Only inserts records whose (category, code) does not exist in the database.
     * Never updates, overrides, or resets existing labels, active flags, or display orders.
     */
    @Transactional
    public int seedDefaults(DropdownOptionRepository repository) {
        int inserted = 0;
        Instant now = Instant.now();

        // Retire the previous label/code so Road and Bridge expose one Current repair choice.
        repository.findByCategoryAndCode("REPAIR_MAINTENANCE_TYPE", "CURRENT_REPAIR_AMC")
                .filter(option -> Boolean.TRUE.equals(option.getActive()))
                .ifPresent(option -> {
                    option.setActive(false);
                    repository.save(option);
                });

        for (SeedItem item : DEFAULT_OPTIONS) {
            if (!repository.existsByCategoryAndCode(item.category(), item.code())) {
                DropdownOptionEntity entity = new DropdownOptionEntity();
                entity.setCategory(item.category());
                entity.setCode(item.code());
                entity.setLabel(item.label());
                entity.setDisplayOrder(item.displayOrder());
                entity.setActive(true);
                entity.setCreatedAt(now);
                entity.setUpdatedAt(now);
                repository.save(entity);
                inserted++;
            }
        }

        if (inserted > 0) {
            log.info("Idempotent seeder: inserted {} missing default dropdown options.", inserted);
        }
        return inserted;
    }
}

