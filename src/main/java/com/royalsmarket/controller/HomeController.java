package com.royalsmarket.controller;

import com.royalsmarket.entity.ItemCategory;
import com.royalsmarket.entity.ListingType;
import com.royalsmarket.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ListingService listingService;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String q,
                        @RequestParam(required = false) ItemCategory category,
                        @RequestParam(required = false) ListingType type,
                        @RequestParam(required = false) String sort,
                        Model model) {
        populate(model, q, category, type, sort);
        return "index";
    }

    /** HTMX target: returns just the listing grid fragment for live search/filter. */
    @GetMapping("/browse")
    public String browse(@RequestParam(required = false) String q,
                         @RequestParam(required = false) ItemCategory category,
                         @RequestParam(required = false) ListingType type,
                         @RequestParam(required = false) String sort,
                         Model model) {
        populate(model, q, category, type, sort);
        return "fragments/listing-grid :: grid";
    }

    private void populate(Model model, String q, ItemCategory category, ListingType type, String sort) {
        model.addAttribute("listings", listingService.browse(q, category, type, sort));
        model.addAttribute("q", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedType", type);
        model.addAttribute("sort", sort);
    }
}
