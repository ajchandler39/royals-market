package com.royalsmarket.controller;

import com.royalsmarket.dto.RegisterForm;
import com.royalsmarket.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                          BindingResult binding) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            binding.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }
        if (form.getUsername() != null && !form.getUsername().isBlank()
                && userService.usernameTaken(form.getUsername())) {
            binding.rejectValue("username", "taken", "That username is taken.");
        }
        if (form.getEmail() != null && !form.getEmail().isBlank()
                && userService.emailTaken(form.getEmail())) {
            binding.rejectValue("email", "taken", "That email is already registered.");
        }
        if (binding.hasErrors()) {
            return "auth/register";
        }
        userService.register(form);
        return "redirect:/login?registered";
    }
}
