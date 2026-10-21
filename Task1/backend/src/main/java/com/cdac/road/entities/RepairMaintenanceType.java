package com.cdac.road.entities;

public enum RepairMaintenanceType {

    CURRENT_REPAIR_AMC("Current Repair/AMC"),
    SPECIAL_REPAIR("Special Repair"),
    FDR("FDR");

    private final String label;

    RepairMaintenanceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}