/* Позиция заказа хранит адрес, по которому курьер должен забрать вещь,
которая в этой позиции содержится. */
ALTER TABLE order_position
  ADD COLUMN pickup_first_name TEXT,
  ADD COLUMN pickup_last_name TEXT,
  ADD COLUMN pickup_company_name TEXT,
  ADD COLUMN pickup_phone TEXT,
  ADD COLUMN pickup_zip_code TEXT,
  ADD COLUMN pickup_city TEXT,
  ADD COLUMN pickup_address TEXT;

ALTER TABLE "user"
  RENAME COLUMN seller_address TO address;

ALTER TABLE "user"
  RENAME COLUMN postcode TO zip_code;