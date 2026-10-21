package com.cdac.road.entities;

public enum NatureOfWork {

    NEW_CONSTRUCTION("New Construction"),
    MAINTENANCE("Maintenance"),
    IMPROVEMENT("Improvement"),
    RECONSTRUCTION("Reconstruction");

    private final String label;

    NatureOfWork(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}