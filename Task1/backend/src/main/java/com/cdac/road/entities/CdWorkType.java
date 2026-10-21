package com.cdac.road.entities;

public enum CdWorkType {

    MINOR_BRIDGE("Minor Bridge"),
    DD_DHAPA_DRAIN("DD - Dhapa Drain (In Nos)"),
    BC_BOX_CULVERT("BC - Box Culvert (In Nos)"),
    SD_SLAB_DRAIN("SD - Slab Drain (In Nos)"),
    ARCH_CULVERT("Arch Culvert (In Nos)"),
    HP_HARD_PASSAGE("HP - Hard Passage (In Nos)"),
    HPC_HUME_PIPE_CULVERT("HPC - Hume Pipe Culvert (In Nos)"),
    VC_VENTED_CAUSEWAY("VC - Vented Causeway (In Nos)");

    private final String label;

    CdWorkType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}