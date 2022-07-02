ALTER TABLE notification
		ADD COLUMN like_id BIGINT REFERENCES "like"(id),
		ADD COLUMN wish_list_id BIGINT REFERENCES wish_list(id);