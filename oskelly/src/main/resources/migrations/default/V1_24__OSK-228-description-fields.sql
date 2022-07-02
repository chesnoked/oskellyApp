ALTER TABLE product
  ADD COLUMN origin TEXT,
  ADD COLUMN purchase_price NUMERIC,
  ADD COLUMN purchase_year INTEGER;

ALTER TABLE product_item
  ADD COLUMN model TEXT;
