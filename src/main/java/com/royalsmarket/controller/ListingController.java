package com.royalsmarket.controller;

import com.royalsmarket.dto.BidForm;
import com.royalsmarket.dto.ListingForm;
import com.royalsmarket.dto.OfferForm;
import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.ListingType;
import com.royalsmarket.entity.User;
import com.royalsmarket.repository.CatalogItemRepository;
import com.royalsmarket.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;
    private final BidService bidService;
    private final OfferService offerService;
    private final CurrentUserService currentUserService;
    private final CatalogItemRepository catalogItemRepository;

    @GetMapping("/new")
    public String newForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ListingForm());
        }
        model.addAttribute("editing", false);
        addCatalog(model);
        return "listings/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ListingForm form,
                        BindingResult binding,
                        Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("editing", false);
            addCatalog(model);
            return "listings/form";
        }
        Listing listing = listingService.create(form, currentUserService.require());
        return "redirect:/listings/" + listing.getId();
    }

    @GetMapping("/mine")
    public String mine(Model model) {
        model.addAttribute("listings", listingService.mySales(currentUserService.require()));
        return "listings/mine";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Listing listing = listingService.get(id);
        model.addAttribute("listing", listing);

        User me = currentUserService.current().orElse(null);
        boolean owner = me != null && listing.getSeller().getId().equals(me.getId());
        model.addAttribute("owner", owner);

        if (listing.getType() == ListingType.AUCTION) {
            model.addAttribute("bids", bidService.bidsFor(listing));
            model.addAttribute("highestBid", bidService.highestBid(listing).orElse(null));
            model.addAttribute("minimumBid", bidService.minimumBid(listing));
            if (!model.containsAttribute("bidForm")) {
                model.addAttribute("bidForm", new BidForm());
            }
        } else if (listing.isAllowOffers()) {
            if (owner) {
                model.addAttribute("offers", offerService.offersFor(listing));
            }
            if (!model.containsAttribute("offerForm")) {
                model.addAttribute("offerForm", new OfferForm());
            }
        }
        return "listings/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Listing listing = listingService.get(id);
        listingService.requireOwner(listing, currentUserService.require());
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", toForm(listing));
        }
        model.addAttribute("editing", true);
        model.addAttribute("listingId", id);
        addCatalog(model);
        return "listings/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                      @Valid @ModelAttribute("form") ListingForm form,
                      BindingResult binding,
                      Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("editing", true);
            model.addAttribute("listingId", id);
            addCatalog(model);
            return "listings/form";
        }
        listingService.update(id, form, currentUserService.require());
        return "redirect:/listings/" + id;
    }

    @PostMapping("/{id}/sold")
    public String markSold(@PathVariable Long id, RedirectAttributes ra) {
        listingService.markSold(id, currentUserService.require());
        ra.addFlashAttribute("flash", "Listing marked as sold.");
        return "redirect:/listings/mine";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        listingService.cancel(id, currentUserService.require());
        ra.addFlashAttribute("flash", "Listing cancelled.");
        return "redirect:/listings/mine";
    }

    private void addCatalog(Model model) {
        model.addAttribute("catalogItems", catalogItemRepository.findAllByOrderByCategoryAscNameAsc());
    }

    private ListingForm toForm(Listing l) {
        ListingForm f = new ListingForm();
        f.setCatalogItemId(l.getItem() != null ? l.getItem().getId() : null);
        f.getStats().putAll(l.getStats());
        f.setSlotsRemaining(l.getSlotsRemaining());
        f.setDescription(l.getDescription());
        f.setImageUrl(l.getImageUrl());
        f.setQuantity(l.getQuantity());
        f.setType(l.getType());
        f.setPrice(l.getPrice());
        f.setBuyNowPrice(l.getBuyNowPrice());
        f.setAllowOffers(l.isAllowOffers());
        return f;
    }
}
