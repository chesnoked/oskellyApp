ALTER TABLE notification
		ADD COLUMN order_id BIGINT REFERENCES "order"(id),
		ADD COLUMN new_order_state TEXT;