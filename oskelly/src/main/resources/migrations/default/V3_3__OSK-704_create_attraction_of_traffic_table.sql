CREATE TABLE attraction_of_traffic(
	id BIGSERIAL PRIMARY KEY,
	type TEXT NOT NULL,
	cookie TEXT NOT NULL,
	expire_time TIMESTAMP WITH TIME ZONE NOT NULL,
	user_id BIGINT REFERENCES "user"(id),
	use_time TIMESTAMP WITH TIME ZONE,
	create_time TIMESTAMP WITH TIME ZONE NULL
);