ALTER TABLE notification
		DROP CONSTRAINT notification_wish_list_id_fkey,
		ADD CONSTRAINT notification_wish_list_id_fkey
				FOREIGN KEY (wish_list_id)
				REFERENCES wish_list(id)
				ON DELETE CASCADE,
		DROP CONSTRAINT notification_like_id_fkey,
		ADD CONSTRAINT notification_like_id_fkey
				FOREIGN KEY (like_id)
				REFERENCES "like"(id)
				ON DELETE CASCADE;