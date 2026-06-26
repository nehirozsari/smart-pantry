-- Smart Pantry baseline schema
-- Phase 0: PostgreSQL extensions and shared utilities for upcoming migrations.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

COMMENT ON EXTENSION "pgcrypto" IS 'Provides gen_random_uuid() and cryptographic functions';
