package com.royalsmarket.service;

import com.royalsmarket.entity.*;
import com.royalsmarket.repository.BidRepository;
import com.royalsmarket.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock
    BidRepository bidRepository;
    @Mock
    ListingRepository listingRepository;
    @InjectMocks
    BidService bidService;

    User seller;
    User bidder;
    Listing auction;

    @BeforeEach
    void setUp() {
        seller = user(1L, "seller");
        bidder = user(2L, "bidder");
        auction = new Listing();
        auction.setId(10L);
        auction.setSeller(seller);
        auction.setType(ListingType.AUCTION);
        auction.setStatus(ListingStatus.ACTIVE);
        auction.setPrice(100_000_000L);
        auction.setEndsAt(LocalDateTime.now().plusHours(1));
    }

    @Test
    void minimumBid_isStartingPrice_whenNoBids() {
        when(bidRepository.findTopByListingOrderByAmountDesc(auction)).thenReturn(Optional.empty());
        assertThat(bidService.minimumBid(auction)).isEqualTo(100_000_000L);
    }

    @Test
    void minimumBid_isOneAboveHighest_whenBidsExist() {
        Bid top = new Bid();
        top.setAmount(150_000_000L);
        when(bidRepository.findTopByListingOrderByAmountDesc(auction)).thenReturn(Optional.of(top));
        assertThat(bidService.minimumBid(auction)).isEqualTo(150_000_001L);
    }

    @Test
    void placeBid_rejectsBelowMinimum() {
        when(listingRepository.findById(10L)).thenReturn(Optional.of(auction));
        when(bidRepository.findTopByListingOrderByAmountDesc(auction)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bidService.placeBid(10L, 99_999_999L, bidder))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("at least");
        verify(bidRepository, never()).save(any());
    }

    @Test
    void placeBid_rejectsSellerBiddingOnOwnAuction() {
        when(listingRepository.findById(10L)).thenReturn(Optional.of(auction));
        assertThatThrownBy(() -> bidService.placeBid(10L, 200_000_000L, seller))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("own auction");
    }

    @Test
    void placeBid_rejectsNonAuctionListing() {
        Listing sale = new Listing();
        sale.setId(11L);
        sale.setType(ListingType.SALE);
        sale.setStatus(ListingStatus.ACTIVE);
        sale.setSeller(seller);
        when(listingRepository.findById(11L)).thenReturn(Optional.of(sale));
        assertThatThrownBy(() -> bidService.placeBid(11L, 1L, bidder))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not an auction");
    }

    @Test
    void placeBid_acceptsValidBid() {
        when(listingRepository.findById(10L)).thenReturn(Optional.of(auction));
        when(bidRepository.findTopByListingOrderByAmountDesc(auction)).thenReturn(Optional.empty());
        when(bidRepository.save(any(Bid.class))).thenAnswer(inv -> inv.getArgument(0));

        Bid placed = bidService.placeBid(10L, 120_000_000L, bidder);

        assertThat(placed.getAmount()).isEqualTo(120_000_000L);
        assertThat(placed.getBidder()).isEqualTo(bidder);
        verify(bidRepository).save(any(Bid.class));
    }

    @Test
    void placeBid_buyNowClosesAuction() {
        auction.setBuyNowPrice(500_000_000L);
        when(listingRepository.findById(10L)).thenReturn(Optional.of(auction));
        when(bidRepository.findTopByListingOrderByAmountDesc(auction)).thenReturn(Optional.empty());
        when(bidRepository.save(any(Bid.class))).thenAnswer(inv -> inv.getArgument(0));

        bidService.placeBid(10L, 500_000_000L, bidder);

        assertThat(auction.getStatus()).isEqualTo(ListingStatus.SOLD);
        assertThat(auction.getWinner()).isEqualTo(bidder);
        assertThat(auction.getFinalPrice()).isEqualTo(500_000_000L);
    }

    private static User user(Long id, String name) {
        User u = new User();
        u.setId(id);
        u.setUsername(name);
        return u;
    }
}
