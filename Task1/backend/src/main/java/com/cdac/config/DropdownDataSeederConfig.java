package com.cdac.config;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.scope.entities.DropdownOptionEntity;
import com.cdac.scope.repository.DropdownOptionRepository;

import jakarta.annotation.PostConstruct;

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

        // WORK_PLAN_TYPE
        new SeedItem("WORK_PLAN_TYPE", "PLAN", "Plan", 1),
        new SeedItem("WORK_PLAN_TYPE", "NON_PLAN", "Non-Plan", 2),

        // CD_WORK_TYPE
        new SeedItem("CD_WORK_TYPE", "MINOR_BRIDGE", "Minor Bridge", 1),
        new SeedItem("CD_WORK_TYPE", "DD_DHAPA_DRAIN", "DD - Dhapa Drain (In Nos)", 2),
        new SeedItem("CD_WORK_TYPE", "BC_BOX_CULVERT", "BC - Box Culvert (In Nos)", 3),
        new SeedItem("CD_WORK_TYPE", "SD_SLAB_DRAIN", "SD - Slab Drain (In Nos)", 4),
        new SeedItem("CD_WORK_TYPE", "ARCH_CULVERT", "Arch Culvert (In Nos)", 5),
        new SeedItem("CD_WORK_TYPE", "HP_HARD_PASSAGE", "HP - Hard Passage (In Nos)", 6),
        new SeedItem("CD_WORK_TYPE", "HPC_HUME_PIPE_CULVERT", "HPC - Hume Pipe Culvert (In Nos)", 7),
        new SeedItem("CD_WORK_TYPE", "VC_VENTED_CAUSEWAY", "VC - Vented Causeway (In Nos)", 8),

        // REPAIR_MAINTENANCE_TYPE
        new SeedItem("REPAIR_MAINTENANCE_TYPE", "CURRENT_REPAIR_AMC", "Current Repair/AMC", 1),
        new SeedItem("REPAIR_MAINTENANCE_TYPE", "SPECIAL_REPAIR", "Special Repair", 2),
        new SeedItem("REPAIR_MAINTENANCE_TYPE", "FDR", "FDR", 3),

        // BRIDGE_ACTIVITY
        new SeedItem("BRIDGE_ACTIVITY", "REPAIR_TO_BRIDGE", "Repair-to-Bridge", 1),
        new SeedItem("BRIDGE_ACTIVITY", "NEW_CONSTRUCTION", "New Construction", 2),
        new SeedItem("BRIDGE_ACTIVITY", "OTHER_ACTIVITY", "Other Activity", 3)
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
