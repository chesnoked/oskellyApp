CREATE TABLE IF NOT EXISTS "user" (
  id BIGSERIAL PRIMARY KEY,
  email TEXT UNIQUE,
  hashed_password TEXT,
  registration_time TIMESTAMPTZ,
  activation_time TIMESTAMPTZ,
  activation_token TEXT
);