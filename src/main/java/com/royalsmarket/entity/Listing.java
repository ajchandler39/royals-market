package com.royalsmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(nullable = false, length = 120)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemCategory category;

    @Column(length = 4000)
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

    @Transient
    public boolean isAuction() {
        return type == ListingType.AUCTION;
    }

    @Transient
    public boolean isActive() {
        return status == ListingStatus.ACTIVE;
    }
}
