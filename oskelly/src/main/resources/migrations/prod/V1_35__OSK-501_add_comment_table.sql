CREATE TABLE comment(
	id BIGSERIAL PRIMARY KEY,
	text TEXT NOT NULL,
	publish_time TIMESTAMPTZ NOT NULL,
	publisher_id BIGINT REFERENCES "user"(id) NOT NULL,
	product_id BIGINT REFERENCES product(id) -- NULLABLE, т.к. в дальнейшем комменты будут не только к товару
);