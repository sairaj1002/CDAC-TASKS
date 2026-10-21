package com.cdac.road.entities;

public enum WorkPlanType {

    PLAN("Plan"),
    NON_PLAN("Non-Plan");

    private final String label;

    WorkPlanType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}