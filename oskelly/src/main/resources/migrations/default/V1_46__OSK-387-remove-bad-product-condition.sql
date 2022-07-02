UPDATE product
SET product_condition_id = (SELECT id
														FROM product_condition
														WHERE name = 'Хорошее состояние')
WHERE product_condition_id = (SELECT id
															FROM product_condition
															WHERE name = 'Так себе');

DELETE FROM product_condition
WHERE name = 'Так себе';