DELETE FROM commission
	WHERE type = 'STANDARD';

INSERT INTO commission(value, type, category_id, user_id, start_price, end_price)
	VALUES
		(0.3, 'STANDARD', NULL, NULL, 0, 99999.99999),
		(0.25, 'STANDARD', NULL, NULL, 100000, 199999.99999),
		(0.2, 'STANDARD', NULL, NULL, 200000, 999999999999.99999);
