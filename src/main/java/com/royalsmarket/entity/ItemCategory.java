package com.royalsmarket.entity;

/**
 * Top-level item grouping. GEAR items also carry an {@link EquipType} (which drives their
 * stat fields); the rest are stat-less item types.
 */
public enum ItemCategory {
    GEAR("Gear"),
    SCROLL("Scroll"),
    USE("Use / Consumable"),
    CHAIR("Chair"),
    PET("Pet"),
    MOUNT("Mount"),
    MESOS("Mesos"),
    SERVICE("Service"),
    MISC("Misc");

    private final String label;

    ItemCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isGear() {
        return this == GEAR;
    }
}
