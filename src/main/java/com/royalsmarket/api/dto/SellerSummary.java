package com.royalsmarket.api.dto;

/** Public-safe view of a seller (no email/password). */
public record SellerSummary(
        Long id,
        String username,
        String ign,
        String discordTag) {
}
