INSERT INTO commission(value, type)
VALUES (0.2, 'NEW_COLLECTION');

ALTER TABLE product
	ADD COLUMN is_new_collection BOOLEAN;

UPDATE product
	SET is_new_collection = FALSE;

ALTER TABLE product
		ALTER COLUMN is_new_collection SET NOT NULL;