package com.royalsmarket.controller;

import com.royalsmarket.dto.OfferForm;
import com.royalsmarket.entity.Offer;
import com.royalsmarket.repository.OfferRepository;
import com.royalsmarket.service.CurrentUserService;
import com.royalsmarket.service.DomainException;
import com.royalsmarket.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final OfferRepository offerRepository;
    private final CurrentUserService currentUserService;

    @PostMapping("/listings/{id}/offers")
    public String makeOffer(@PathVariable Long id,
                           @Valid @ModelAttribute("offerForm") OfferForm form,
                           BindingResult binding,
                           RedirectAttributes ra) {
        try {
            if (binding.hasErrors()) {
                throw new DomainException("Enter a valid offer amount.");
            }
            offerService.makeOffer(id, form, currentUserService.require());
            ra.addFlashAttribute("flash", "Offer sent to the seller!");
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/listings/" + id;
    }

    @PostMapping("/offers/{offerId}/accept")
    public String accept(@PathVariable Long offerId, RedirectAttributes ra) {
        Long listingId = listingIdOf(offerId);
        try {
            offerService.accept(offerId, currentUserService.require());
            ra.addFlashAttribute("flash", "Offer accepted. The listing is now marked sold.");
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/listings/" + listingId;
    }

    @PostMapping("/offers/{offerId}/decline")
    public String decline(@PathVariable Long offerId, RedirectAttributes ra) {
        Long listingId = listingIdOf(offerId);
        try {
            offerService.decline(offerId, currentUserService.require());
            ra.addFlashAttribute("flash", "Offer declined.");
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/listings/" + listingId;
    }

    private Long listingIdOf(Long offerId) {
        return offerRepository.findById(offerId)
                .map(Offer::getListing)
                .map(l -> l.getId())
                .orElse(null);
    }
}
