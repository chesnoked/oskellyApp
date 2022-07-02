ALTER TABLE static_page
  ADD COLUMN modified_by_id bigint REFERENCES "user"(id),
  ADD COLUMN modified_at TIMESTAMPTZ;