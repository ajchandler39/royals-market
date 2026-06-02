package com.royalsmarket.api.dto;

import java.time.LocalDateTime;

/** JSON representation of a listing. {@code currentBid} is populated for auctions only. */
public record ListingResponse(
        Long id,
        String title,
        String category,
        String type,
        String status,
        long price,
        Long buyNowPrice,
        Long currentBid,
        boolean allowOffers,
        int quantity,
        String imageUrl,
        String description,
        LocalDateTime endsAt,
        LocalDateTime createdAt,
        SellerSummary seller) {
}
