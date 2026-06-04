package com.royalsmarket.config;

import com.royalsmarket.entity.*;
import com.royalsmarket.repository.ListingRepository;
import com.royalsmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds demo accounts and listings. Runs in every profile (including prod) so the public
 * demo deployment shows content and the demo logins work. Idempotent: only seeds when the
 * database is empty, so it never overwrites real data or re-runs on redeploys.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = newUser("admin", "admin@royalsmarket.gg", "password", "AdminMule", "admin#0001", Role.ADMIN);
        User seller = newUser("pinkbean", "pinkbean@royalsmarket.gg", "password", "PinkBean", "pinkbean#1234", Role.USER);
        User buyer = newUser("zakum", "zakum@royalsmarket.gg", "password", "ZakumArm", "zakum#5678", Role.USER);
        userRepository.save(admin);
        userRepository.save(seller);
        userRepository.save(buyer);

        listingRepository.save(sale(seller, "Clean Fafnir Mit Snail",
                ItemCategory.WEAPON, "Untouched, 0 slots used. Perfect for scrolling.",
                850_000_000L, true));
        listingRepository.save(sale(seller, "30% Cold Protection Scroll x10",
                ItemCategory.SCROLL, "Bulk lot of ten. Great for early-game gear.",
                12_000_000L, false));
        listingRepository.save(sale(buyer, "Pink Adventurer Cape (clean)",
                ItemCategory.ARMOR, "Looking to offload, make an offer.",
                40_000_000L, true));

        Listing auction = auction(seller, "Rare Maple Leaf Chair",
                ItemCategory.CHAIR, "Hard to find these days. 24h auction.",
                100_000_000L, 500_000_000L, 24);
        listingRepository.save(auction);
    }

    private User newUser(String username, String email, String pw, String ign, String discord, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(pw));
        u.setIgn(ign);
        u.setDiscordTag(discord);
        u.setRole(role);
        return u;
    }

    private Listing sale(User seller, String title, ItemCategory cat, String desc, long price, boolean allowOffers) {
        Listing l = new Listing();
        l.setSeller(seller);
        l.setTitle(title);
        l.setCategory(cat);
        l.setDescription(desc);
        l.setType(ListingType.SALE);
        l.setPrice(price);
        l.setAllowOffers(allowOffers);
        return l;
    }

    private Listing auction(User seller, String title, ItemCategory cat, String desc,
                            long startingBid, long buyNow, int hours) {
        Listing l = new Listing();
        l.setSeller(seller);
        l.setTitle(title);
        l.setCategory(cat);
        l.setDescription(desc);
        l.setType(ListingType.AUCTION);
        l.setPrice(startingBid);
        l.setBuyNowPrice(buyNow);
        l.setEndsAt(LocalDateTime.now().plusHours(hours));
        return l;
    }
}
