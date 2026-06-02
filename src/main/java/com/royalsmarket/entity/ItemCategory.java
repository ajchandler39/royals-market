package com.royalsmarket.entity;

/**
 * MapleStory-flavored item categories for listings.
 */
public enum ItemCategory {
    WEAPON("Weapon"),
    ARMOR("Armor"),
    ACCESSORY("Accessory"),
    SCROLL("Scroll"),
    USE_ITEM("Use / Consumable"),
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
}
