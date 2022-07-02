ALTER TABLE product
  ADD COLUMN buyer_id BIGINT REFERENCES "user" (id);
