package com.royalsmarket.dto;

import com.royalsmarket.entity.ListingType;
import com.royalsmarket.entity.StatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class ListingForm {

    /** The catalog item being listed. */
    @NotNull
    private Long catalogItemId;

    /** Structured stat values keyed by stat type; only those applicable to the item are kept. */
    private Map<StatType, Integer> stats = new LinkedHashMap<>();

    /** Gear only: upgrade (scroll) slots remaining. */
    @Min(0)
    private Integer slotsRemaining;

    /** Optional notes (e.g. obscure stats). */
    @Size(max = 2000)
    private String description;

    @Size(max = 500)
    private String imageUrl;

    @Min(1)
    private int quantity = 1;

    @NotNull
    private ListingType type = ListingType.SALE;

    /** SALE: asking price. AUCTION: starting bid. Minimum 100M mesos (high-value market). */
    @Min(value = 100_000_000L, message = "Listings must be priced at least 100,000,000 mesos.")
    private long price;

    /** AUCTION only, optional. */
    @Min(value = 100_000_000L, message = "Buy-now price must be at least 100,000,000 mesos.")
    private Long buyNowPrice;

    /** SALE only. */
    private boolean allowOffers;

    /** AUCTION only: how many hours the auction runs. */
    @Min(1)
    private Integer durationHours = 24;
}
