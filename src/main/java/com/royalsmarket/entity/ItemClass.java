package com.royalsmarket.entity;

/** Job/class a piece of gear is for (or All Classes for shared-stat gear). */
public enum ItemClass {
    ALL("All Classes"),
    WARRIOR("Warrior"),
    MAGICIAN("Magician"),
    BOWMAN("Bowman"),
    THIEF("Thief"),
    PIRATE("Pirate");

    private final String label;

    ItemClass(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
