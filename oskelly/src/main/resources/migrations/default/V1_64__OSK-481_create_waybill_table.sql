CREATE TABLE waybill(
	id BIGSERIAL PRIMARY KEY,
	uuid UUID UNIQUE NOT NULL,

	pickup_phone TEXT NOT NULL,
	pickup_zip_code TEXT NOT NULL,
	pickup_name TEXT NOT NULL,
	pickup_address TEXT NOT NULL,

	delivery_phone TEXT NOT NULL,
	delivery_zip_code TEXT NOT NULL,
	delivery_name TEXT NOT NULL,
	delivery_address TEXT NOT NULL,

	order_position_id BIGINT REFERENCES order_position(id) NOT NULL,

	pickup_destination_type TEXT NOT NULL,
	delivery_destination_type TEXT NOT NULL,

	external_system_id TEXT NOT NULL
);