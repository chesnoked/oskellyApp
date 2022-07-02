CREATE TABLE notification(
	id BIGSERIAL PRIMARY KEY,
	create_time TIMESTAMPTZ NOT NULL,
	read_time TIMESTAMPTZ,
	user_id BIGINT REFERENCES "user"(id) NOT NULL,
	product_item_id BIGINT REFERENCES product_item(id),
	dtype TEXT NOT NULL
);