package com.royalsmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "listings")
@Getter
@Setter
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    /** The catalog item being sold (replaces the old free-text title). */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "catalog_item_id")
    private CatalogItem item;

    /** Structured stat values (gear only); only stats applicable to the item's equip type are kept. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "listing_stat", joinColumns = @JoinColumn(name = "listing_id"))
    @MapKeyColumn(name = "stat_type", length = 10)
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "stat_value", nullable = false)
    private Map<StatType, Integer> stats = new LinkedHashMap<>();

    /** Gear only: upgrade (scroll) slots remaining on the item. */
    private Integer slotsRemaining;

    /** Optional free-text notes (e.g. obscure stats not covered by the structured fields). */
    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private int quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ListingType type = ListingType.SALE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ListingStatus status = ListingStatus.ACTIVE;

    /** SALE: asking price in mesos. AUCTION: starting bid in mesos. */
    @Column(nullable = false)
    private long price;

    /** Optional instant-win price for auctions. */
    private Long buyNowPrice;

    /** SALE only: whether the seller accepts best offers. */
    @Column(nullable = false)
    private boolean allowOffers = false;

    /** AUCTION only: when bidding closes. */
    private LocalDateTime endsAt;

    /** Set when an auction closes or a buy-now/offer is accepted. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    /** Final mesos amount the listing sold for. */
    private Long finalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Display title, derived from the catalog item. */
    @Transient
    public String getTitle() {
        return item != null ? item.getName() : null;
    }

    /** Category, derived from the catalog item. */
    @Transient
    public ItemCategory getCategory() {
        return item != null ? item.getCategory() : null;
    }

    @Transient
    public boolean isGear() {
        return item != null && item.isGear();
    }

    @Transient
    public boolean isAuction() {
        return type == ListingType.AUCTION;
    }

    @Transient
    public boolean isActive() {
        return status == ListingStatus.ACTIVE;
    }
}
