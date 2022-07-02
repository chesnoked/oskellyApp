CREATE TABLE product_item (
	id BIGSERIAL PRIMARY KEY,
	product_id BIGINT REFERENCES product(id) NOT NULL,
	size_id BIGINT REFERENCES size (id),
	start_price NUMERIC,
	current_price NUMERIC,
	buy_price NUMERIC,
	serial_number TEXT,
	buyer_id BIGINT REFERENCES "user" (id),
	reserve_expire_time TIMESTAMPTZ NULL,
	reserver_id BIGINT NULL REFERENCES "user"(id),
	reserve_type TEXT NULL
);

INSERT INTO product_item
					(product_id, size_id, start_price, current_price, buy_price, serial_number, buyer_id, reserve_expire_time, reserver_id, reserve_type)
		SELECT id, size_id, start_price, start_price, buy_price, NULL, buyer_id, reserve_expire_time, reserver_id, reserve_type
			FROM product;

ALTER TABLE order_position
 		ADD COLUMN product_item_id BIGINT REFERENCES product_item(id);

UPDATE order_position x
		SET product_item_id = pi.id
		FROM product_item pi
		WHERE pi.product_id = x.product_id;

ALTER TABLE shopping_cart
	ADD COLUMN product_item_id BIGINT REFERENCES product_item(id);

UPDATE shopping_cart x
		SET product_item_id = pi.id
		FROM product_item pi
		WHERE pi.product_id = x.product_id;

ALTER TABLE wish_list
	ADD COLUMN product_item_id BIGINT REFERENCES product_item(id);

UPDATE wish_list x
		SET product_item_id = pi.id
		FROM product_item pi
		WHERE pi.product_id = x.product_id;

ALTER TABLE product
	DROP COLUMN size_id,
	DROP COLUMN start_price,
	DROP COLUMN current_price,
	DROP COLUMN buy_price,
	DROP COLUMN buyer_id,
	DROP COLUMN reserve_expire_time,
	DROP COLUMN reserver_id,
	DROP COLUMN reserve_type;

ALTER TABLE order_position
		DROP COLUMN product_id;

ALTER TABLE shopping_cart
		DROP COLUMN product_id;

ALTER TABLE wish_list
		DROP COLUMN product_id;