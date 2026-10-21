package com.cdac.road.entities;

public enum ProposedRoadType {

    MURUM_EARTHEN("Murum (Earthen)"),
    WBM_METAL("WBM (Metal)"),
    BT_DAMBRI("BT (Dambri)"),
    CONCRETE("Concrete");

    private final String label;

    ProposedRoadType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}