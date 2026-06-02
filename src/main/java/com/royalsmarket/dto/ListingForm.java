package com.royalsmarket.dto;

import com.royalsmarket.entity.ItemCategory;
import com.royalsmarket.entity.ListingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingForm {

    @NotBlank
    @Size(max = 120)
    private String title;

    @NotNull
    private ItemCategory category;

    @Size(max = 4000)
    private String description;

    @Size(max = 500)
    private String imageUrl;

    @Min(1)
    private int quantity = 1;

    @NotNull
    private ListingType type = ListingType.SALE;

    /** SALE: asking price. AUCTION: starting bid. */
    @Min(0)
    private long price;

    /** AUCTION only, optional. */
    @Min(0)
    private Long buyNowPrice;

    /** SALE only. */
    private boolean allowOffers;

    /** AUCTION only: how many hours the auction runs. */
    @Min(1)
    private Integer durationHours = 24;
}
