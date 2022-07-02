UPDATE product
	SET current_price = start_price
	WHERE current_price IS NULL;