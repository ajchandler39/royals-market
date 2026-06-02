package com.royalsmarket;

import com.royalsmarket.repository.ListingRepository;
import com.royalsmarket.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the app boots against a real PostgreSQL instance with Flyway-applied migrations.
 * Skipped automatically when Docker is unavailable (e.g. local dev without Docker); runs in CI.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class PostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    UserRepository userRepository;
    @Autowired
    ListingRepository listingRepository;

    @Test
    void flywayMigratedAndSeedDataLoadedOnPostgres() {
        // DataSeeder runs on the freshly-migrated Postgres schema.
        assertThat(userRepository.count()).isGreaterThan(0);
        assertThat(listingRepository.count()).isGreaterThan(0);
    }
}
