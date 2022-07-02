ALTER TABLE order_position
    ADD COLUMN participates_in_payment BOOLEAN;

UPDATE order_position
SET participates_in_payment = TRUE
WHERE id IN (SELECT order_position.id
             FROM order_position JOIN "order" ON order_position.order_id = "order".id
             WHERE "order".state != 'CREATED');
