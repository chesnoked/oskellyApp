INSERT INTO commission(value, type)
		VALUES (0.25, 'PRO_STANDARD'),
			(0.3, 'STANDARD');

UPDATE product_item
	SET current_price_without_commission = current_price
	WHERE current_price_without_commission IS NULL;

UPDATE product_item
	SET current_price = current_price_without_commission / (1 - 0.25);