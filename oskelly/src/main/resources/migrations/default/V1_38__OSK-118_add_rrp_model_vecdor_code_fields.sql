ALTER TABLE product
		ADD COLUMN rrp_price NUMERIC,
		ADD COLUMN model TEXT,
		ADD COLUMN vendor_code TEXT;

UPDATE product p
	SET model = pi.model
	FROM product_item pi
	WHERE pi.product_id = p.id;

ALTER TABLE product_item
		DROP COLUMN model;