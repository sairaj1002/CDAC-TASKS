package com.cdac.egov.wms.work.subwork.building.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.egov.wms.work.subwork.building.dto.BuildingRequest;
import com.cdac.egov.wms.work.subwork.building.entity.Building;
import com.cdac.egov.wms.work.subwork.building.entity.BuildingAudit;
import com.cdac.egov.wms.work.subwork.building.entity.DropdownOption;
import com.cdac.egov.wms.work.subwork.building.repository.BuildingAuditRepository;
import com.cdac.egov.wms.work.subwork.building.repository.BuildingRepository;
import com.cdac.egov.wms.work.subwork.building.repository.BuildingDropdownOptionRepository;

@Service
public class BuildingService {

    private final BuildingRepository buildings;
    private final BuildingAuditRepository audits;
    private final BuildingDropdownOptionRepository options;
    private final JdbcTemplate jdbc;

    public BuildingService(
            BuildingRepository buildings,
            BuildingAuditRepository audits,
            BuildingDropdownOptionRepository options,
            JdbcTemplate jdbc) {

        this.buildings = buildings;
        this.audits = audits;
        this.options = options;
        this.jdbc = jdbc;
    }

    public Page<Building> list(int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by("id").descending());

        return buildings.findAll((root, query, cb) -> cb.isFalse(root.get("deleted")), pageable);
    }

    public Building get(Long id) {
        return buildings.findById(id)
                .filter(building -> !building.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Building not found"));
    }

    @Transactional
    public Building create(BuildingRequest request) {

        validateOptions(request);

        Building building = new Building();
        applyRequest(building, request);
        
        building.setWorkId(request.workId() == null
                ? jdbc.queryForObject("SELECT nextval('work_id_seq')", Long.class)
                : request.workId());

        building.setOfficeId(
                request.officeId() == null ? jdbc.queryForObject("SELECT nextval('office_id_seq')", Long.class)
                        : request.officeId());

        building.setTenantId(
                request.tenantId() == null ? jdbc.queryForObject("SELECT nextval('tenant_id_seq')", Long.class)
                        : request.tenantId());

        Building saved = buildings.saveAndFlush(building);

        audit(saved, "Created", "Initial building record created");
        return saved;
    }

    @Transactional
    public Building update(
            Long id, BuildingRequest request) {

        Building building = get(id);
        validateOptions(request);

        String before = snapshot(building);
        applyRequest(building, request);
        Building saved = buildings.saveAndFlush(building);
        audit(saved, "Updated",
                "Before: " + before + " | After: " + snapshot(saved));

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Building building = get(id);
        building.setDeleted(true);
        buildings.saveAndFlush(building);
        audit(building, "Deleted", "Record soft-deleted");
    }

    public List<BuildingAudit> auditHistory(Long id) {
        if (!buildings.existsById(id)) {
            throw new IllegalArgumentException("Building not found");
        }
        return audits.findByBuildingIdOrderByTimestampDesc(id);
    }

    public List<DropdownOption> dropdown(String category) {
        return options.findByCategoryAndActiveTrueOrderById(category);
    }

    private void validateOptions(BuildingRequest request) {
        boolean maintenanceValid = options
                .findByCategoryAndActiveTrueOrderById("MAINTENANCE_TYPE")
                .stream()
                .anyMatch(o -> o.getLabel().equals(request.maintenanceType()));

        boolean activityValid = options
                .findByCategoryAndActiveTrueOrderById("BUILDING_ACTIVITY")
                .stream()
                .anyMatch(o -> o.getLabel().equals(request.buildingActivity()));

        String maintenance = request.maintenanceType().toLowerCase();
        String expectedPrefix = maintenance.contains("current") || maintenance.startsWith("cr") ? "CR" :
                maintenance.contains("special") || maintenance.startsWith("slr") ? "SLR" :
                maintenance.contains("fdr") || maintenance.startsWith("sdr") || maintenance.contains("specified") ? "SDR" : "";
        activityValid = activityValid && !expectedPrefix.isEmpty()
                && request.buildingActivity().toUpperCase().startsWith(expectedPrefix);

        if (!maintenanceValid || !activityValid) {
            throw new IllegalArgumentException(
                    "Invalid maintenance type or building activity");
        }
    }

    private void applyRequest(Building b, BuildingRequest r) {
        b.setBuildingDescription(r.buildingDescription());
        b.setBuiltUpArea(r.builtUpArea());
        b.setNumberOfFloors(r.numberOfFloors());
        b.setNumberOfBasements(r.numberOfBasements());
        b.setMaintenanceType(r.maintenanceType());
        b.setBuildingActivity(r.buildingActivity());
    }

    private String snapshot(Building b) {
        return "description=" + b.getBuildingDescription()
                + ", area=" + b.getBuiltUpArea()
                + ", floors=" + b.getNumberOfFloors()
                + ", basements=" + b.getNumberOfBasements()
                + ", maintenanceType=" + b.getMaintenanceType()
                + ", buildingActivity=" + b.getBuildingActivity();
    }

    private void audit(Building b, String remark, String details) {
        audits.save(new BuildingAudit(
                b.getId(),
                remark,
                "System",
                "System",
                "System",
                details));
    }

}
