ALTER TABLE "order"
    ADD COLUMN effective_amount NUMERIC,
    ADD COLUMN buyer_check TEXT;

ALTER TABLE order_position
    ADD COLUMN is_effective BOOLEAN;

