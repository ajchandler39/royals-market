package com.royalsmarket.api.dto;

import java.time.LocalDateTime;

public record BidResponse(
        Long id,
        long amount,
        String bidder,
        LocalDateTime createdAt) {
}
