package com.royalsmarket.entity;

/** Old-school (v83) MapleStory equipment stats that an item can carry or be scrolled for. */
public enum StatType {
    STR("STR"),
    DEX("DEX"),
    INT("INT"),
    LUK("LUK"),
    WATK("W.Atk"),
    MATK("M.Atk"),
    WDEF("W.Def"),
    MDEF("M.Def"),
    ACC("Accuracy"),
    AVOID("Avoid"),
    SPEED("Speed"),
    JUMP("Jump"),
    HP("HP"),
    MP("MP");

    private final String label;

    StatType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
