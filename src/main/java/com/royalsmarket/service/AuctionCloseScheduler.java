package com.royalsmarket.service;

import com.royalsmarket.entity.Bid;
import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.ListingStatus;
import com.royalsmarket.entity.ListingType;
import com.royalsmarket.repository.BidRepository;
import com.royalsmarket.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Periodically closes auctions whose end time has passed. */
@Component
@RequiredArgsConstructor
public class AuctionCloseScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionCloseScheduler.class);

    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;

    @Scheduled(fixedDelayString = "${royalsmarket.auction.close-interval-ms:60000}")
    @Transactional
    public void closeExpiredAuctions() {
        List<Listing> expired = listingRepository.findByStatusAndTypeAndEndsAtLessThanEqual(
                ListingStatus.ACTIVE, ListingType.AUCTION, LocalDateTime.now());
        for (Listing listing : expired) {
            Bid top = bidRepository.findTopByListingOrderByAmountDesc(listing).orElse(null);
            if (top != null) {
                listing.setStatus(ListingStatus.SOLD);
                listing.setWinner(top.getBidder());
                listing.setFinalPrice(top.getAmount());
            } else {
                listing.setStatus(ListingStatus.EXPIRED);
            }
            listingRepository.save(listing);
        }
        if (!expired.isEmpty()) {
            log.info("Closed {} expired auction(s).", expired.size());
        }
    }
}
