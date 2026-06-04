# ♛ RoyalsMarket

[![CI](https://github.com/ajchandler39/royals-market/actions/workflows/ci.yml/badge.svg)](https://github.com/ajchandler39/royals-market/actions/workflows/ci.yml)

**🔗 Live demo: [royals-market-production.up.railway.app](https://royals-market-production.up.railway.app)** — try the demo login `pinkbean` / `password`.

A standalone web marketplace for a MapleStory private server — a modern replacement for the
"selling" section of a forum. Players post items for **fixed-price sale** or **timed auction**,
browse and search listings, and arrange the actual trade in-game. RoyalsMarket has no connection
to the game server; it's purely the listing/discovery/negotiation layer.

> Built as a portfolio project: Spring Boot + server-rendered Thymeleaf, with a light sprinkle of
> HTMX and Alpine.js — no SPA build pipeline.

## Features

- **Accounts & profiles** — register / log in (Spring Security, BCrypt), set your in-game name (IGN)
  and Discord tag so buyers can reach you.
- **Listings** — create either a **fixed-price sale** or a **timed auction**; categories, quantity,
  description, and an image URL.
- **Browse** — keyword search, category/type filters, and sorting, with HTMX-powered live results.
- **Auctions** — bidding with validation and outbid rules, a live Alpine.js countdown, an optional
  buy-now price, and a scheduled job that closes expired auctions and records the winner.
- **Offers** — buyers can submit best offers on sale listings; sellers accept or decline.
- **Messaging** — in-site conversations between buyer and seller, with unread counts.
- **Seller dashboard** — manage your listings (edit, mark sold, cancel) and review bids/offers.
- **JSON REST API** — `/api/**` with OpenAPI/Swagger docs (reads public, writes via HTTP Basic).

## Tech stack

| Layer     | Choice                                                            |
|-----------|-------------------------------------------------------------------|
| Language  | Java 21                                                           |
| Framework | Spring Boot 4.0 (Web MVC, Data JPA, Security, Validation)         |
| Views     | Thymeleaf + HTMX + Alpine.js (CDN), custom CSS                    |
| API docs  | springdoc-openapi / Swagger UI                                    |
| Database  | H2 file DB (dev) · PostgreSQL (prod) · Flyway migrations          |
| Testing   | JUnit 5, Mockito, MockMvc, Testcontainers (PostgreSQL)            |
| CI        | GitHub Actions (build + test on every push)                       |
| Build     | Maven (wrapper included — no separate Maven install needed)       |

## REST API

A JSON API mirrors the web features (handy for tooling/integrations):

| Method | Path                          | Auth | Description                |
|--------|-------------------------------|------|----------------------------|
| GET    | `/api/listings`               | —    | Browse (q/category/type/sort) |
| GET    | `/api/listings/{id}`          | —    | Listing detail             |
| POST   | `/api/listings`               | ✓    | Create a listing           |
| GET    | `/api/listings/{id}/bids`     | —    | Bid history                |
| POST   | `/api/listings/{id}/bids`     | ✓    | Place a bid                |

- **Swagger UI:** <http://localhost:8080/swagger-ui.html> · **OpenAPI JSON:** `/v3/api-docs`
- Writes use HTTP Basic, e.g.:
  ```bash
  curl -u zakum:password -H 'Content-Type: application/json' \
    -d '{"amount":150000000}' http://localhost:8080/api/listings/4/bids
  ```

## Testing

```bash
./mvnw verify
```

- **Unit** — `BidServiceTest` covers bid validation / outbid / buy-now rules with Mockito.
- **Integration (H2)** — `ListingApiIntegrationTest` drives the API through MockMvc + Spring Security.
- **Integration (PostgreSQL)** — `PostgresIntegrationTest` boots the app against a real Postgres
  container via Testcontainers (runs in CI; auto-skipped locally when Docker isn't available).

## Run locally (H2, zero setup)

Requires a JDK 21 on your `PATH` (or `JAVA_HOME`).

```bash
./mvnw spring-boot:run        # macOS/Linux
.\mvnw.cmd spring-boot:run    # Windows PowerShell
```

Then open <http://localhost:8080>. A few demo accounts and listings are seeded automatically
(only when the database is empty):

| Username   | Password   | Role  |
|------------|------------|-------|
| `admin`    | `password` | ADMIN |
| `pinkbean` | `password` | USER  |
| `zakum`    | `password` | USER  |

- H2 console (dev): <http://localhost:8080/h2-console> — JDBC URL `jdbc:h2:file:./data/royalsmarket`,
  user `sa`, empty password.
- The H2 data file lives under `./data/` (git-ignored).

## Run with Docker + PostgreSQL

```bash
docker compose up --build
```

This starts Postgres and the app (on the `prod` profile) at <http://localhost:8080>. Change the
default DB password in `docker-compose.yml` before using it anywhere real.

## Production profile

The `prod` profile reads the database from environment variables:

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://<host>:5432/royalsmarket
DB_USERNAME=...
DB_PASSWORD=...
```

Build and run the jar directly:

```bash
./mvnw -DskipTests package
java -jar target/royalsmarket-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Project layout

```
com.royalsmarket
├─ config/      Security, global model advice (navbar), dev data seeder
├─ entity/      User, Listing, Bid, Offer, Conversation, Message (+ enums)
├─ repository/  Spring Data JPA repositories
├─ service/     Listing/Bid/Offer/Message/User services + auction-close scheduler
├─ dto/         Form-backing objects (validated)
└─ controller/  Home, Auth, Listing, Bid, Offer, Profile, Message
src/main/resources/templates  Thymeleaf views (fragments, listings, auth, profile, messages)
src/main/resources/static     css/ and js/ (HTMX CSRF wiring + Alpine countdown)
```

## Deploying

Any host that can run a Java 21 jar or a Docker image works. Easiest paths:

- **PaaS** (Railway / Render / Fly.io): point at this repo or the Docker image, add a managed
  Postgres, set `SPRING_PROFILES_ACTIVE=prod` and the `DB_*` vars.
- **VPS** (Hetzner / DigitalOcean): `docker compose up -d` behind Caddy or Nginx for TLS.

Then point your domain's DNS at the host.
