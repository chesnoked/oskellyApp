ALTER TABLE notification
		ADD COLUMN comment_id BIGINT REFERENCES comment(id),
		ADD COLUMN offer_id BIGINT REFERENCES offer(id);