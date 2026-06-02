package com.royalsmarket.controller;

import com.royalsmarket.dto.ProfileForm;
import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.ListingStatus;
import com.royalsmarket.entity.User;
import com.royalsmarket.service.CurrentUserService;
import com.royalsmarket.service.DomainException;
import com.royalsmarket.service.ListingService;
import com.royalsmarket.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final ListingService listingService;
    private final CurrentUserService currentUserService;

    @GetMapping("/profile")
    public String myProfile() {
        return "redirect:/u/" + currentUserService.require().getUsername();
    }

    @GetMapping("/profile/edit")
    public String editForm(Model model) {
        User me = currentUserService.require();
        if (!model.containsAttribute("form")) {
            ProfileForm form = new ProfileForm();
            form.setIgn(me.getIgn());
            form.setDiscordTag(me.getDiscordTag());
            model.addAttribute("form", form);
        }
        return "profile/edit";
    }

    @PostMapping("/profile/edit")
    public String edit(@Valid @ModelAttribute("form") ProfileForm form, BindingResult binding) {
        if (binding.hasErrors()) {
            return "profile/edit";
        }
        userService.updateProfile(currentUserService.require(), form);
        return "redirect:/profile";
    }

    @GetMapping("/u/{username}")
    public String publicProfile(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new DomainException("User not found."));
        List<Listing> active = listingService.mySales(user).stream()
                .filter(l -> l.getStatus() == ListingStatus.ACTIVE)
                .toList();
        model.addAttribute("profileUser", user);
        model.addAttribute("listings", active);
        return "profile/view";
    }
}
