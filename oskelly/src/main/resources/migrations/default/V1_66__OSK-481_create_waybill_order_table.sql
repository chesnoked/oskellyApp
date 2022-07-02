CREATE TABLE waybill_order(
	id BIGSERIAL PRIMARY KEY,
	uuid UUID UNIQUE NOT NULL,
	external_system_id TEXT NOT NULL,
	create_time TIMESTAMPTZ NOT NULL
);

ALTER TABLE waybill
		ADD COLUMN waybill_order_id BIGINT REFERENCES waybill_order NOT NULL;