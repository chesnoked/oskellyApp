ALTER TABLE wish_list
  DROP COLUMN product_item_id,
  ADD COLUMN product_id BIGINT NOT NULL REFERENCES product(id);