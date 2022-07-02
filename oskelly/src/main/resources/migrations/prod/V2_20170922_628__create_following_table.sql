CREATE TABLE following(
	id BIGSERIAL PRIMARY KEY,
	follower_id BIGINT REFERENCES "user"(id) NOT NULL,
	following_id BIGINT REFERENCES "user"(id) NOT NULL,
	create_time TIMESTAMPTZ NOT NULL,
	UNIQUE(follower_id, following_id)
);

ALTER TABLE notification
		ADD COLUMN product_id BIGINT REFERENCES product(id),
		ADD COLUMN following_id BIGINT REFERENCES following(id) ON DELETE CASCADE ON UPDATE CASCADE;