/* Вещь знает, в каком заказе ее купили */
ALTER TABLE product_item
ADD COLUMN effective_order_id BIGINT REFERENCES "order" (id)
