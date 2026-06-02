package com.royalsmarket.api;

import com.royalsmarket.api.dto.BidResponse;
import com.royalsmarket.api.dto.ListingResponse;
import com.royalsmarket.api.dto.SellerSummary;
import com.royalsmarket.entity.Bid;
import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.User;

/** Maps entities to API response DTOs (keeps entities out of the JSON layer). */
public final class ApiMapper {

    private ApiMapper() {
    }

    public static SellerSummary toSeller(User u) {
        return new SellerSummary(u.getId(), u.getUsername(), u.getIgn(), u.getDiscordTag());
    }

    public static ListingResponse toListing(Listing l, Long currentBid) {
        return new ListingResponse(
                l.getId(),
                l.getTitle(),
                l.getCategory().name(),
                l.getType().name(),
                l.getStatus().name(),
                l.getPrice(),
                l.getBuyNowPrice(),
                currentBid,
                l.isAllowOffers(),
                l.getQuantity(),
                l.getImageUrl(),
                l.getDescription(),
                l.getEndsAt(),
                l.getCreatedAt(),
                toSeller(l.getSeller()));
    }

    public static BidResponse toBid(Bid b) {
        return new BidResponse(b.getId(), b.getAmount(), b.getBidder().getUsername(), b.getCreatedAt());
    }
}
