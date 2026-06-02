package com.royalsmarket.controller;

import com.royalsmarket.dto.BidForm;
import com.royalsmarket.service.BidService;
import com.royalsmarket.service.CurrentUserService;
import com.royalsmarket.service.DomainException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/listings/{id}")
public class BidController {

    private final BidService bidService;
    private final CurrentUserService currentUserService;

    @PostMapping("/bids")
    public String placeBid(@PathVariable Long id,
                          @Valid @ModelAttribute("bidForm") BidForm form,
                          BindingResult binding,
                          RedirectAttributes ra) {
        try {
            if (binding.hasErrors()) {
                throw new DomainException("Enter a valid bid amount.");
            }
            bidService.placeBid(id, form.getAmount(), currentUserService.require());
            ra.addFlashAttribute("flash", "Bid placed!");
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/listings/" + id;
    }

    @PostMapping("/buy-now")
    public String buyNow(@PathVariable Long id, RedirectAttributes ra) {
        try {
            bidService.buyNow(id, currentUserService.require());
            ra.addFlashAttribute("flash", "You won the auction via buy-now! Arrange the trade with the seller.");
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/listings/" + id;
    }
}
