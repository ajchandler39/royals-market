package com.royalsmarket.api;

import com.royalsmarket.api.dto.BidResponse;
import com.royalsmarket.api.dto.ListingResponse;
import com.royalsmarket.dto.BidForm;
import com.royalsmarket.dto.ListingForm;
import com.royalsmarket.entity.Bid;
import com.royalsmarket.entity.ItemCategory;
import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.ListingType;
import com.royalsmarket.service.BidService;
import com.royalsmarket.service.CurrentUserService;
import com.royalsmarket.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * JSON REST API over the same services that back the web UI.
 * Reads are public; writes require authentication (HTTP Basic — see SecurityConfig).
 */
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Tag(name = "Listings", description = "Browse, view, create listings and place bids")
public class ListingApiController {

    private final ListingService listingService;
    private final BidService bidService;
    private final CurrentUserService currentUserService;

    @GetMapping
    @Operation(summary = "List active listings", description = "Optional keyword, category, type filters and sort key.")
    public List<ListingResponse> list(@RequestParam(required = false) String q,
                                      @RequestParam(required = false) ItemCategory category,
                                      @RequestParam(required = false) ListingType type,
                                      @RequestParam(required = false) String sort) {
        return listingService.browse(q, category, type, sort).stream()
                .map(l -> ApiMapper.toListing(l, null))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a listing by id")
    public ListingResponse get(@PathVariable Long id) {
        Listing l = listingService.get(id);
        Long currentBid = l.getType() == ListingType.AUCTION
                ? bidService.highestBid(l).map(Bid::getAmount).orElse(null)
                : null;
        return ApiMapper.toListing(l, currentBid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a listing", description = "Requires authentication.")
    public ListingResponse create(@Valid @RequestBody ListingForm form) {
        Listing created = listingService.create(form, currentUserService.require());
        return ApiMapper.toListing(created, null);
    }

    @GetMapping("/{id}/bids")
    @Operation(summary = "List bids on an auction (highest first)")
    public List<BidResponse> bids(@PathVariable Long id) {
        return bidService.bidsFor(listingService.get(id)).stream()
                .map(ApiMapper::toBid)
                .toList();
    }

    @PostMapping("/{id}/bids")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a bid on an auction", description = "Requires authentication.")
    public BidResponse placeBid(@PathVariable Long id, @Valid @RequestBody BidForm form) {
        return ApiMapper.toBid(bidService.placeBid(id, form.getAmount(), currentUserService.require()));
    }
}
