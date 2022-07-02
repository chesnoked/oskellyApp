CREATE TABLE commission(
	id BIGSERIAL PRIMARY KEY,
	value NUMERIC NOT NULL,
	type TEXT NOT NULL,
	category_id BIGINT REFERENCES category(id),
	user_id BIGINT REFERENCES "user"(id)
);

ALTER TABLE product_item
		ADD COLUMN current_price_without_commission NUMERIC;

ALTER TABLE order_position
		ADD COLUMN commission NUMERIC;