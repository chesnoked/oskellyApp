ALTER TABLE "order"
  ADD COLUMN delivery_name TEXT,
  ADD COLUMN delivery_phone TEXT,
  ADD COLUMN delivery_country TEXT,
  ADD COLUMN delivery_city TEXT,
  ADD COLUMN delivery_address TEXT,
  ADD COLUMN delivery_zip_code TEXT;

ALTER TABLE "user"
  ADD COLUMN delivery_name TEXT,
  ADD COLUMN delivery_phone TEXT,
  ADD COLUMN delivery_country TEXT,
  ADD COLUMN delivery_city TEXT,
  ADD COLUMN delivery_zip_code TEXT;
