package com.royalsmarket.service;

import com.royalsmarket.dto.OfferForm;
import com.royalsmarket.entity.*;
import com.royalsmarket.repository.ListingRepository;
import com.royalsmarket.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final ListingRepository listingRepository;

    public List<Offer> offersFor(Listing listing) {
        return offerRepository.findByListingOrderByAmountDesc(listing);
    }

    @Transactional
    public Offer makeOffer(Long listingId, OfferForm form, User buyer) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new DomainException("Listing not found."));
        if (listing.getType() != ListingType.SALE || !listing.isAllowOffers()) {
            throw new DomainException("This listing isn't accepting offers.");
        }
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new DomainException("This listing is no longer active.");
        }
        if (listing.getSeller().getId().equals(buyer.getId())) {
            throw new DomainException("You can't make an offer on your own listing.");
        }
        Offer offer = new Offer();
        offer.setListing(listing);
        offer.setBuyer(buyer);
        offer.setAmount(form.getAmount());
        offer.setMessage(form.getMessage() == null || form.getMessage().isBlank() ? null : form.getMessage().trim());
        return offerRepository.save(offer);
    }

    @Transactional
    public void accept(Long offerId, User seller) {
        Offer offer = load(offerId, seller);
        Listing listing = offer.getListing();
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new DomainException("This listing is no longer active.");
        }
        offer.setStatus(OfferStatus.ACCEPTED);
        offerRepository.save(offer);
        listing.setStatus(ListingStatus.SOLD);
        listing.setWinner(offer.getBuyer());
        listing.setFinalPrice(offer.getAmount());
        listingRepository.save(listing);
    }

    @Transactional
    public void decline(Long offerId, User seller) {
        Offer offer = load(offerId, seller);
        offer.setStatus(OfferStatus.DECLINED);
        offerRepository.save(offer);
    }

    private Offer load(Long offerId, User seller) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new DomainException("Offer not found."));
        if (!offer.getListing().getSeller().getId().equals(seller.getId()) && seller.getRole() != Role.ADMIN) {
            throw new DomainException("You don't own this listing.");
        }
        return offer;
    }
}
