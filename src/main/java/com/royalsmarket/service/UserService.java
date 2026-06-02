package com.royalsmarket.service;

import com.royalsmarket.dto.ProfileForm;
import com.royalsmarket.dto.RegisterForm;
import com.royalsmarket.entity.User;
import com.royalsmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername())
                .password(u.getPasswordHash())
                .authorities("ROLE_" + u.getRole().name())
                .disabled(!u.isEnabled())
                .build();
    }

    public boolean usernameTaken(String username) {
        return userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    public boolean emailTaken(String email) {
        return userRepository.existsByEmailIgnoreCase(email.trim());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    @Transactional
    public User register(RegisterForm form) {
        User u = new User();
        u.setUsername(form.getUsername().trim());
        u.setEmail(form.getEmail().trim());
        u.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        u.setIgn(blankToNull(form.getIgn()));
        u.setDiscordTag(blankToNull(form.getDiscordTag()));
        return userRepository.save(u);
    }

    @Transactional
    public void updateProfile(User user, ProfileForm form) {
        user.setIgn(blankToNull(form.getIgn()));
        user.setDiscordTag(blankToNull(form.getDiscordTag()));
        userRepository.save(user);
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
