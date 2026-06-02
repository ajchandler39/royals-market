package com.royalsmarket.config;

import com.royalsmarket.entity.ItemCategory;
import com.royalsmarket.entity.ListingType;
import com.royalsmarket.entity.User;
import com.royalsmarket.service.CurrentUserService;
import com.royalsmarket.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Exposes the signed-in user and their unread-message count to every view (used by the navbar). */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final CurrentUserService currentUserService;
    private final MessageService messageService;

    @ModelAttribute("currentUser")
    public User currentUser() {
        return currentUserService.current().orElse(null);
    }

    @ModelAttribute("unreadCount")
    public long unreadCount() {
        return currentUserService.current().map(messageService::unreadCount).orElse(0L);
    }

    @ModelAttribute("allCategories")
    public ItemCategory[] allCategories() {
        return ItemCategory.values();
    }

    @ModelAttribute("allTypes")
    public ListingType[] allTypes() {
        return ListingType.values();
    }
}
