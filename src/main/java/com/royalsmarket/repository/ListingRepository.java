package com.royalsmarket.repository;

import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.ListingStatus;
import com.royalsmarket.entity.ListingType;
import com.royalsmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ListingRepository
        extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {

    List<Listing> findBySellerOrderByCreatedAtDesc(User seller);

    /** Auctions that have passed their end time but are still marked active. */
    List<Listing> findByStatusAndTypeAndEndsAtLessThanEqual(
            ListingStatus status, ListingType type, LocalDateTime cutoff);
}
