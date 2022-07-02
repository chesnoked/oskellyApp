INSERT INTO commission(value, type)
VALUES (0.5, 'TURBO');

ALTER TABLE order_position
	ALTER COLUMN commission SET NOT NULL;

ALTER TABLE product
	ADD COLUMN is_turbo BOOLEAN;

UPDATE product
SET is_turbo = FALSE;

ALTER TABLE product
	ALTER COLUMN is_turbo SET NOT NULL;