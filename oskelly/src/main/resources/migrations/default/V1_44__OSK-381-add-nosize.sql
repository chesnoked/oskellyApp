ALTER TABLE size
	ADD COLUMN no_size TEXT;

INSERT INTO size (category_id, no_size) SELECT
																					id,
																					'Без размера'
																				FROM category
																				WHERE parent_id IN (SELECT id
																														FROM category
																														WHERE left_order = 1)