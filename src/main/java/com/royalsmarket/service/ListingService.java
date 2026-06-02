package com.royalsmarket.service;

import com.royalsmarket.dto.ListingForm;
import com.royalsmarket.entity.*;
import com.royalsmarket.repository.ListingRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    /** Public browse with optional keyword, category, type filters and a sort key. */
    public List<Listing> browse(String q, ItemCategory category, ListingType type, String sort) {
        Specification<Listing> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("status"), ListingStatus.ACTIVE));
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like)));
            }
            if (category != null) {
                ps.add(cb.equal(root.get("category"), category));
            }
            if (type != null) {
                ps.add(cb.equal(root.get("type"), type));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return listingRepository.findAll(spec, sortFor(sort));
    }

    private Sort sortFor(String sort) {
        return switch (sort == null ? "" : sort) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "ending" -> Sort.by("endsAt").ascending();
            default -> Sort.by("createdAt").descending();
        };
    }

    public Listing get(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new DomainException("Listing not found."));
    }

    public List<Listing> mySales(User seller) {
        return listingRepository.findBySellerOrderByCreatedAtDesc(seller);
    }

    @Transactional
    public Listing create(ListingForm f, User seller) {
        Listing l = new Listing();
        apply(f, l);
        l.setSeller(seller);
        l.setStatus(ListingStatus.ACTIVE);
        if (l.getType() == ListingType.AUCTION) {
            int hours = f.getDurationHours() == null ? 24 : f.getDurationHours();
            l.setEndsAt(LocalDateTime.now().plusHours(hours));
            l.setAllowOffers(false);
        } else {
            l.setEndsAt(null);
            l.setBuyNowPrice(null);
        }
        return listingRepository.save(l);
    }

    @Transactional
    public Listing update(Long id, ListingForm f, User actor) {
        Listing l = get(id);
        requireOwner(l, actor);
        if (l.getStatus() != ListingStatus.ACTIVE) {
            throw new DomainException("Only active listings can be edited.");
        }
        // Type is fixed once created to keep bids/offers consistent.
        f.setType(l.getType());
        apply(f, l);
        if (l.getType() != ListingType.AUCTION) {
            l.setEndsAt(null);
            l.setBuyNowPrice(null);
        }
        return listingRepository.save(l);
    }

    @Transactional
    public void markSold(Long id, User actor) {
        Listing l = get(id);
        requireOwner(l, actor);
        l.setStatus(ListingStatus.SOLD);
        listingRepository.save(l);
    }

    @Transactional
    public void cancel(Long id, User actor) {
        Listing l = get(id);
        requireOwner(l, actor);
        l.setStatus(ListingStatus.CANCELLED);
        listingRepository.save(l);
    }

    public void requireOwner(Listing l, User actor) {
        if (!l.getSeller().getId().equals(actor.getId()) && actor.getRole() != Role.ADMIN) {
            throw new DomainException("You don't own this listing.");
        }
    }

    private void apply(ListingForm f, Listing l) {
        l.setTitle(f.getTitle().trim());
        l.setCategory(f.getCategory());
        l.setDescription(blankToNull(f.getDescription()));
        l.setImageUrl(blankToNull(f.getImageUrl()));
        l.setQuantity(Math.max(1, f.getQuantity()));
        l.setType(f.getType());
        l.setPrice(f.getPrice());
        l.setBuyNowPrice(f.getBuyNowPrice());
        l.setAllowOffers(f.isAllowOffers());
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
