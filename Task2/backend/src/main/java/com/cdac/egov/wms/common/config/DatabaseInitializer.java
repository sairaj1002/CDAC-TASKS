package com.cdac.egov.wms.common.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cdac.egov.wms.work.subwork.building.entity.DropdownOption;
import com.cdac.egov.wms.work.subwork.building.repository.BuildingDropdownOptionRepository;
import com.cdac.egov.wms.work.subwork.unit.entity.Unit;
import com.cdac.egov.wms.work.subwork.unit.repository.UnitRepository;

@Configuration
public class DatabaseInitializer {

    @Bean
    ApplicationRunner initializeDatabase(
            JdbcTemplate jdbc,
            BuildingDropdownOptionRepository dropdownRepository,
            UnitRepository unitRepository) {

        return args -> {
            jdbc.execute(
                "CREATE SEQUENCE IF NOT EXISTS work_id_seq START WITH 1");
            jdbc.execute(
                "CREATE SEQUENCE IF NOT EXISTS subwork_id_seq START WITH 1");

            addOption(dropdownRepository, "MAINTENANCE_TYPE",
                    "CR (Current Repair)");
            addOption(dropdownRepository, "MAINTENANCE_TYPE",
                    "SLR (Special Repair)");
            addOption(dropdownRepository, "MAINTENANCE_TYPE",
                    "SDR (Specified Repair)");

            String[] buildingActivities = {
                "SLR 1 - Partial reroofing", "SLR 2 - Distempering and colouring",
                "SLR 3 - Replacement of water supply and sanitary pipes", "SLR 4 - Periodical Oil painting",
                "SLR 5 - Replacement of Electrical wiring", "SLR 6 - Plaster of walls",
                "SLR 7 - Misc. Item of special repair in case of any damage etc.",
                "CR 1- Repairs to fixtures and fastening of door & window", "CR 2 - Repairs to frames and shutter of door & window",
                "CR 3 - Repairs to sanitary and water supply fitting", "CR 4 - Colour,Washing, distempering & patch painting of walls and doors & windows",
                "CR 5 - Repairs to floors", "CR 6 - Repairs to plaster, pointing",
                "CR 7 - Repairs to roof such as turning tiles replacement of tiles attending to leakages water proofing course etc.",
                "CR 8 - Monsoon protection such as bamboo matting etc.", "CR 9 - Maintenance of Electrical fitting & fixtures",
                "CR 10 - Misc. item of similar nature", "SDR 1 - Renewal of old dilapidated roof with M. Tiles, A. C. Sheet etc.",
                "SDR 2 - Renewal of decayed doors and windows.", "SDR 3 - Replumbing to reduce leakages from water pipes and down take pipes.",
                "SDR 4 - Guniting and corrosion protection.", "SDR 5 - Renewal of electric wiring which has outlived its life. (Wherever guniting is done, necessary renewal of electrical wiring should be considered).",
                "SDR 6 - Where external walls are wet due to leakags etc., the replastering work can be undertaken, if that is the cause of leakage.",
                "SDR 7 - Water proofing to stop leakage and dampness.", "SDR 8 - Plinth protection work, if the building is situated on B. C. Soil strats.",
                "SDR 9 - Drainage improvements including provision of gutters around the plinth where required.",
                "SDR 10 - Reconstruction of old cracked/decayed brick pillars/Walls.", "SDR 11 - Work of replacement of weather sheds.",
                "SDR 12 - Renewal of damaged flooring"
            };
            for (String activity : buildingActivities) addOption(dropdownRepository, "BUILDING_ACTIVITY", activity);

            addUnits(unitRepository);
        };
    }

    private void addUnits(UnitRepository repository) {
        String[][] units = {
            {"sqm", "Square Metre"},
            {"kg", "Kilo Gram"},
            {"each", "each"},
            {"quintal", "Quintal"},
            {"cum", "Cubic Metre"},
            {"NA", "Not Applicable"},
            {"BAG", "BAGS"},
            {"BKL", "BUCKLES"},
            {"BOU", "BOU"},
            {"BOX", "BOX"},
            {"BTL", "BOTTLES"},
            {"BUN", "BUNCHES"},
            {"CCM", "CUBIC CENTIMETER"},
            {"CIN", "CUBIC INCHES"},
            {"CLT", "CENTILITRE"},
            {"CMT", "CENTIMETRE"},
            {"CUM", "CUBIC METRE"},
            {"DRM", "DRUM"},
            {"GMS", "GRAMS"},
            {"GRS", "GROSS"},
            {"GYD", "GROSS YARDS"},
            {"KGS", "KILOGRAMS"},
            {"KLR", "KILOLITRE"},
            {"KME", "KILOMETRE"},
            {"LTR", "LITRES"},
            {"MLT", "MILLILITRE"},
            {"MTR", "METRES"},
            {"NOS", "NUMBERS"},
            {"OTH", "OTHERS"},
            {"PAC", "PACKS"},
            {"PRS", "PAIRS"},
            {"QTL", "QUINTAL"},
            {"SET", "SETS"},
            {"SQF", "SQUARE FEET"},
            {"SQM", "SQUARE METRES"},
            {"SQY", "SQUARE YARDS"},
            {"TBS", "TABLETS"},
            {"TGM", "TEN GROSS"},
            {"THD", "THOUSANDS"},
            {"TON", "TONNES"},
            {"TUB", "TUBES"},
            {"UGS", "US GALLONS"},
            {"UNT", "UNITS"},
            {"YDS", "YARDS"}
        };

        for (String[] unit : units) {
            String shortName = unit[0];
            String description = unit[1];
            if (!repository.existsByShortName(shortName)) {
                repository.save(new Unit(shortName, description));
            }
        }
    }

    private void addOption(
            BuildingDropdownOptionRepository repository,
            String category,
            String label) {

        if (!repository.existsByCategoryAndLabel(category, label)) {
            repository.save(new DropdownOption(category, label));
        }
    }
}
