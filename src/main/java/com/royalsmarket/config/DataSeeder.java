package com.royalsmarket.config;

import com.royalsmarket.entity.*;
import com.royalsmarket.repository.CatalogItemRepository;
import com.royalsmarket.repository.ListingRepository;
import com.royalsmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Seeds the item catalog, demo accounts, and demo listings. Runs in every profile (incl. prod)
 * so the public demo has content and the demo logins work. Idempotent per entity: each section
 * only seeds when its table is empty, so it never overwrites real data or re-runs on redeploys.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedCatalog();
        seedUsers();
        seedListings();
    }

    // ---------- Catalog ----------
    private void seedCatalog() {
        if (catalogItemRepository.count() > 0) {
            return;
        }
        catalogItemRepository.saveAll(List.of(
                gear("Pink Adventurer Cape", EquipType.CAPE, ItemClass.ALL),
                gear("Maple Cape", EquipType.CAPE, ItemClass.ALL),
                gear("Zakum Helmet", EquipType.HAT, ItemClass.ALL),
                gear("Brown Work Gloves", EquipType.GLOVES, ItemClass.ALL),
                gear("Sauna Robe", EquipType.OVERALL, ItemClass.ALL),
                gear("Yellow Snowshoes", EquipType.SHOES, ItemClass.ALL),
                gear("Glittering Earrings", EquipType.EARRINGS, ItemClass.ALL),
                gear("Maple Shield", EquipType.SHIELD, ItemClass.WARRIOR),
                gear("Stonetooth Sword", EquipType.WEAPON, ItemClass.WARRIOR),
                gear("Wooden Wand", EquipType.WEAPON, ItemClass.MAGICIAN),
                gear("Dragon Khanjar", EquipType.WEAPON, ItemClass.THIEF),
                gear("Mithril Bow", EquipType.WEAPON, ItemClass.BOWMAN),
                gear("Maple Pyrope Knuckle", EquipType.WEAPON, ItemClass.PIRATE),
                // High-value market only (>= 100M): gear + Chaos Scrolls. No cheap scrolls/potions/etc. yet.
                other("Chaos Scroll", ItemCategory.SCROLL)));
    }

    // ---------- Users ----------
    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        userRepository.save(newUser("admin", "admin@royalsmarket.gg", "password", "AdminMule", "admin#0001", Role.ADMIN));
        userRepository.save(newUser("pinkbean", "pinkbean@royalsmarket.gg", "password", "PinkBean", "pinkbean#1234", Role.USER));
        userRepository.save(newUser("zakum", "zakum@royalsmarket.gg", "password", "ZakumArm", "zakum#5678", Role.USER));
    }

    // ---------- Listings ----------
    private void seedListings() {
        if (listingRepository.count() > 0) {
            return;
        }
        Map<String, CatalogItem> items = catalogItemRepository.findAll().stream()
                .collect(Collectors.toMap(CatalogItem::getName, Function.identity()));
        User pinkbean = userRepository.findByUsernameIgnoreCase("pinkbean").orElse(null);
        User zakum = userRepository.findByUsernameIgnoreCase("zakum").orElse(null);
        if (pinkbean == null || zakum == null || items.isEmpty()) {
            return;
        }

        Listing cape = sale(pinkbean, items.get("Pink Adventurer Cape"), 1, 150_000_000L, true,
                "Scrolled for LUK. Make an offer.");
        cape.setSlotsRemaining(3);
        cape.getStats().put(StatType.LUK, 5);
        listingRepository.save(cape);

        Listing sword = sale(pinkbean, items.get("Stonetooth Sword"), 1, 850_000_000L, false, null);
        sword.setSlotsRemaining(0);
        sword.getStats().put(StatType.WATK, 121);
        listingRepository.save(sword);

        listingRepository.save(sale(pinkbean, items.get("Chaos Scroll"), 5, 120_000_000L, false,
                "Bulk lot of five Chaos Scrolls."));

        Listing helm = auction(pinkbean, items.get("Zakum Helmet"), 200_000_000L, null, 24);
        helm.setSlotsRemaining(1);
        helm.getStats().put(StatType.INT, 9);
        helm.getStats().put(StatType.MDEF, 30);
        listingRepository.save(helm);

        Listing mapleCape = auction(pinkbean, items.get("Maple Cape"), 100_000_000L, 400_000_000L, 24);
        mapleCape.setSlotsRemaining(2);
        mapleCape.getStats().put(StatType.LUK, 8);
        listingRepository.save(mapleCape);

        // (zakum is left without listings so the demo has a clean buyer account.)
    }

    // ---------- helpers ----------
    private CatalogItem gear(String name, EquipType type, ItemClass cls) {
        CatalogItem c = new CatalogItem();
        c.setName(name);
        c.setCategory(ItemCategory.GEAR);
        c.setEquipType(type);
        c.setItemClass(cls);
        return c;
    }

    private CatalogItem other(String name, ItemCategory category) {
        CatalogItem c = new CatalogItem();
        c.setName(name);
        c.setCategory(category);
        return c;
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

    private Listing sale(User seller, CatalogItem item, int qty, long price, boolean allowOffers, String notes) {
        Listing l = new Listing();
        l.setSeller(seller);
        l.setItem(item);
        l.setQuantity(qty);
        l.setType(ListingType.SALE);
        l.setPrice(price);
        l.setAllowOffers(allowOffers);
        l.setDescription(notes);
        return l;
    }

    private Listing auction(User seller, CatalogItem item, long startingBid, Long buyNow, int hours) {
        Listing l = new Listing();
        l.setSeller(seller);
        l.setItem(item);
        l.setType(ListingType.AUCTION);
        l.setPrice(startingBid);
        l.setBuyNowPrice(buyNow);
        l.setEndsAt(LocalDateTime.now().plusHours(hours));
        return l;
    }
}
