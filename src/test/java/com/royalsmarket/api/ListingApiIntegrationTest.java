package com.royalsmarket.api;

import com.royalsmarket.entity.Listing;
import com.royalsmarket.entity.ListingType;
import com.royalsmarket.repository.ListingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack API test on in-memory H2 (Flyway builds the schema, DataSeeder loads demo rows).
 * Verifies public reads, auth enforcement on writes, and a successful authenticated bid.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ListingApiIntegrationTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ListingRepository listingRepository;

    @Test
    void browseIsPublicAndReturnsSeededListings() throws Exception {
        mvc.perform(get("/api/listings"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Fafnir")));
    }

    @Test
    void createListingRequiresAuthentication() throws Exception {
        mvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Unauthorized","category":"MISC","type":"SALE","price":1000,"quantity":1}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanBidOnAuction() throws Exception {
        Listing auction = listingRepository.findAll().stream()
                .filter(l -> l.getType() == ListingType.AUCTION)
                .findFirst()
                .orElseThrow();

        mvc.perform(post("/api/listings/" + auction.getId() + "/bids")
                        .with(httpBasic("zakum", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":" + auction.getPrice() + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("zakum")));
    }
}
