-- Re-curate the catalog for a high-value (>= 100M) market: gear + Chaos Scrolls only.
-- Clearing listings + catalog lets the (idempotent-per-table) DataSeeder reload the curated set
-- on existing deployments. Demo data only; order respects foreign keys.
DELETE FROM messages;
DELETE FROM conversations;
DELETE FROM listing_stat;
DELETE FROM offers;
DELETE FROM bids;
DELETE FROM listings;
DELETE FROM catalog_item;
