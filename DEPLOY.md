# Deploying RoyalsMarket

The app ships as a Docker image (multi-stage `Dockerfile`) and honors a `PORT` env var, so it runs
on essentially any container host. The `prod` Spring profile reads the database from three env vars:

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USERNAME=<user>
DB_PASSWORD=<password>
```

> Note the `jdbc:postgresql://` prefix. Managed providers often show a `postgres://user:pass@host/db`
> connection string — split that into the three vars above (and prepend `jdbc:`).

---

## Option A — Railway (recommended, fastest)

1. Create an account at <https://railway.app> and `New Project → Deploy from GitHub repo` (select
   this repo). Railway auto-detects the `Dockerfile`.
2. In the project, `New → Database → PostgreSQL`. Railway provisions it and exposes variables like
   `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`.
3. On the **app service → Variables**, add:
   - `SPRING_PROFILES_ACTIVE = prod`
   - `DB_URL = jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}`
   - `DB_USERNAME = ${{Postgres.PGUSER}}`
   - `DB_PASSWORD = ${{Postgres.PGPASSWORD}}`
   (Railway supports `${{Service.VAR}}` references — adjust the service name if yours differs.)
4. Deploy. Railway sets `PORT` automatically; Flyway creates the schema on first boot.
5. Under **Settings → Networking**, generate a public domain (or attach your custom one).

## Option B — Render

1. <https://render.com> → `New → Postgres` (free plan). Note its **Internal Connection** host, port,
   database, user, password.
2. `New → Web Service → Build from a repository`, pick this repo, runtime **Docker**.
3. Add environment variables: `SPRING_PROFILES_ACTIVE=prod`, and `DB_URL` / `DB_USERNAME` /
   `DB_PASSWORD` from the Postgres details (remember the `jdbc:postgresql://` prefix).
4. Create the service. Render injects `PORT`; the app binds to it.

## Option C — Any VPS (Hetzner / DigitalOcean)

```bash
git clone <your-repo> && cd royalsmarket
# edit docker-compose.yml: change the DB password
docker compose up -d --build
```

Put Caddy or Nginx in front for automatic HTTPS, then point your domain's DNS at the server.

---

## Custom domain

Buy a domain (e.g. `royalsmarket.gg`) at Cloudflare Registrar or Namecheap, then add the host's
CNAME/A record per your provider's instructions (Railway/Render show the exact target in their
Networking/Custom Domain settings).
