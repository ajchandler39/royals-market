package com.royalsmarket.service;

import com.royalsmarket.entity.*;
import com.royalsmarket.repository.BidRepository;
import com.royalsmarket.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final ListingRepository listingRepository;

    public List<Bid> bidsFor(Listing listing) {
        return bidRepository.findByListingOrderByAmountDesc(listing);
    }

    public Optional<Bid> highestBid(Listing listing) {
        return bidRepository.findTopByListingOrderByAmountDesc(listing);
    }

    public long bidCount(Listing listing) {
        return bidRepository.countByListing(listing);
    }

    /** Minimum acceptable next bid: one above the current high, or the starting price if no bids yet. */
    public long minimumBid(Listing listing) {
        return highestBid(listing).map(b -> b.getAmount() + 1).orElse(listing.getPrice());
    }

    @Transactional
    public Bid placeBid(Long listingId, long amount, User bidder) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new DomainException("Listing not found."));
        if (listing.getType() != ListingType.AUCTION) {
            throw new DomainException("This listing is not an auction.");
        }
        if (listing.getStatus() != ListingStatus.ACTIVE
                || (listing.getEndsAt() != null && listing.getEndsAt().isBefore(LocalDateTime.now()))) {
            throw new DomainException("This auction has ended.");
        }
        if (listing.getSeller().getId().equals(bidder.getId())) {
            throw new DomainException("You can't bid on your own auction.");
        }
        long min = minimumBid(listing);
        if (amount < min) {
            throw new DomainException("Your bid must be at least " + String.format("%,d", min) + " mesos.");
        }

        Bid bid = new Bid();
        bid.setListing(listing);
        bid.setBidder(bidder);
        bid.setAmount(amount);
        bid = bidRepository.save(bid);

        // Instant win if a buy-now price is set and the bid meets it.
        if (listing.getBuyNowPrice() != null && amount >= listing.getBuyNowPrice()) {
            listing.setStatus(ListingStatus.SOLD);
            listing.setWinner(bidder);
            listing.setFinalPrice(amount);
            listing.setEndsAt(LocalDateTime.now());
            listingRepository.save(listing);
        }
        return bid;
    }

    @Transactional
    public void buyNow(Long listingId, User buyer) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new DomainException("Listing not found."));
        if (listing.getType() != ListingType.AUCTION || listing.getBuyNowPrice() == null) {
            throw new DomainException("This auction has no buy-now price.");
        }
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new DomainException("This auction has ended.");
        }
        if (listing.getSeller().getId().equals(buyer.getId())) {
            throw new DomainException("You can't buy your own auction.");
        }
        listing.setStatus(ListingStatus.SOLD);
        listing.setWinner(buyer);
        listing.setFinalPrice(listing.getBuyNowPrice());
        listing.setEndsAt(LocalDateTime.now());
        listingRepository.save(listing);
    }
}
