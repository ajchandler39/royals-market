package com.royalsmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/** A known MapleStory item sellers choose from (instead of typing a free-text title). */
@Entity
@Table(name = "catalog_item")
@Getter
@Setter
public class CatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemCategory category;

    /** GEAR only: drives which stat fields apply. Null for non-gear items. */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EquipType equipType;

    /** GEAR only: class the item is for. */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ItemClass itemClass;

    @Column(length = 500)
    private String iconUrl;

    @Transient
    public boolean isGear() {
        return category == ItemCategory.GEAR && equipType != null;
    }

    /** Stats that can appear on this item (empty for non-gear). */
    @Transient
    public Set<StatType> applicableStats() {
        return equipType != null ? equipType.getApplicableStats() : Set.of();
    }
}
