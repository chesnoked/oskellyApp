ALTER TABLE product_condition
    ADD COLUMN sort_order BIGINT;

UPDATE product_condition SET sort_order = 1 WHERE name = 'С биркой';
UPDATE product_condition SET sort_order = 2 WHERE name = 'Отличное состояние';
UPDATE product_condition SET sort_order = 3 WHERE name = 'Хорошее состояние';