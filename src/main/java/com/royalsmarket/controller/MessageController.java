package com.royalsmarket.controller;

import com.royalsmarket.dto.MessageForm;
import com.royalsmarket.entity.Conversation;
import com.royalsmarket.entity.User;
import com.royalsmarket.service.CurrentUserService;
import com.royalsmarket.service.DomainException;
import com.royalsmarket.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final CurrentUserService currentUserService;

    @GetMapping("/messages")
    public String inbox(Model model) {
        User me = currentUserService.require();
        model.addAttribute("conversations", messageService.inbox(me));
        model.addAttribute("me", me);
        return "messages/inbox";
    }

    @GetMapping("/messages/{id}")
    public String conversation(@PathVariable Long id, Model model) {
        User me = currentUserService.require();
        Conversation c = messageService.getForUser(id, me);
        model.addAttribute("conversation", c);
        model.addAttribute("messages", messageService.openConversation(c, me));
        model.addAttribute("me", me);
        model.addAttribute("other", c.other(me));
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new MessageForm());
        }
        return "messages/conversation";
    }

    @PostMapping("/messages/{id}")
    public String send(@PathVariable Long id,
                      @Valid @ModelAttribute("form") MessageForm form,
                      BindingResult binding,
                      RedirectAttributes ra) {
        User me = currentUserService.require();
        Conversation c = messageService.getForUser(id, me);
        if (!binding.hasErrors()) {
            messageService.send(c, me, form.getBody());
        }
        return "redirect:/messages/" + id;
    }

    @PostMapping("/listings/{id}/contact")
    public String contactSeller(@PathVariable Long id,
                               @Valid @ModelAttribute("form") MessageForm form,
                               BindingResult binding,
                               RedirectAttributes ra) {
        try {
            if (binding.hasErrors()) {
                throw new DomainException("Enter a message to send.");
            }
            Conversation c = messageService.contactSeller(id, currentUserService.require(), form.getBody());
            return "redirect:/messages/" + c.getId();
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/listings/" + id;
        }
    }
}
