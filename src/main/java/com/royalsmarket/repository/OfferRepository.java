package com.royalsmarket.repository;

import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByListingOrderByAmountDesc(Listing listing);
}
