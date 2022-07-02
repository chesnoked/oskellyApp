ALTER TABLE product_item
	ADD COLUMN state TEXT;

UPDATE product_item SET state = 'INITIAL';

ALTER TABLE product_item
	ALTER COLUMN state SET NOT NULL;
