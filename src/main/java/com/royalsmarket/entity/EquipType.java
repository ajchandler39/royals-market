package com.royalsmarket.entity;

import java.util.Set;

import static com.royalsmarket.entity.StatType.*;

/**
 * Equipment slot/type. Each type declares which {@link StatType}s can appear on it
 * (i.e. which it can carry or be scrolled for), so listing forms show only relevant fields.
 */
public enum EquipType {
    WEAPON("Weapon", Set.of(WATK, MATK, STR, DEX, INT, LUK)),
    SHIELD("Shield", Set.of(STR, DEX, INT, LUK, WATK, MATK, WDEF, MDEF, HP, MP)),
    HAT("Hat", Set.of(STR, DEX, INT, LUK, WDEF, MDEF, HP, MP, ACC, AVOID)),
    TOP("Top", Set.of(STR, DEX, INT, LUK, WDEF, MDEF, HP, MP)),
    BOTTOM("Bottom", Set.of(STR, DEX, INT, LUK, WDEF, MDEF, HP, MP)),
    OVERALL("Overall", Set.of(STR, DEX, INT, LUK, WDEF, MDEF, HP, MP)),
    SHOES("Shoes", Set.of(STR, DEX, INT, LUK, JUMP, SPEED, AVOID, WDEF)),
    GLOVES("Gloves", Set.of(WATK, MATK, STR, DEX, INT, LUK, ACC, WDEF)),
    CAPE("Cape", Set.of(STR, DEX, INT, LUK, WATK, MATK, WDEF, MDEF, HP, MP)),
    EARRINGS("Earrings", Set.of(INT, LUK, STR, DEX, MATK, MDEF, HP, MP)),
    EYE_ACCESSORY("Eye Accessory", Set.of(STR, DEX, INT, LUK, ACC, HP, MP, WATK, MATK)),
    FACE_ACCESSORY("Face Accessory", Set.of(STR, DEX, INT, LUK, AVOID, HP, MP, WATK, MATK)),
    RING("Ring", Set.of(STR, DEX, INT, LUK, WDEF, MDEF, HP, MP)),
    PENDANT("Pendant", Set.of(STR, DEX, INT, LUK, WDEF, MDEF, HP, MP)),
    BELT("Belt", Set.of(STR, DEX, INT, LUK, WDEF, MDEF, HP, MP));

    private final String label;
    private final Set<StatType> applicableStats;

    EquipType(String label, Set<StatType> applicableStats) {
        this.label = label;
        this.applicableStats = applicableStats;
    }

    public String getLabel() {
        return label;
    }

    public Set<StatType> getApplicableStats() {
        return applicableStats;
    }
}
