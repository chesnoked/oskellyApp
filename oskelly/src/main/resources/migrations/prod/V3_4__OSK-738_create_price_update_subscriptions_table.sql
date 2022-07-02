CREATE TABLE price_update_subscription(
    id BIGSERIAL PRIMARY KEY,
    subscriber_id BIGINT REFERENCES "user"(id),
    product_id BIGINT REFERENCES product(id)
);