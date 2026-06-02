package com.royalsmarket.repository;

import com.royalsmarket.entity.Bid;
import com.royalsmarket.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByListingOrderByAmountDesc(Listing listing);

    Optional<Bid> findTopByListingOrderByAmountDesc(Listing listing);

    long countByListing(Listing listing);
}
