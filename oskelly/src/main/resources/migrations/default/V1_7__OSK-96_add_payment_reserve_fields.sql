ALTER TABLE product
		ADD COLUMN reserve_expire_time TIMESTAMPTZ NULL,
		ADD COLUMN reserver_id BIGINT NULL REFERENCES "user"(id),
		ADD COLUMN reserve_type TEXT NULL;