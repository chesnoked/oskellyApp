CREATE TABLE product_alert_subscription
(
  id  BIGSERIAL PRIMARY KEY,
  subscriber_id BIGINT NOT NULL REFERENCES "user"(id),
  brand_id BIGINT REFERENCES brand(id),
  category_id BIGINT REFERENCES category(id),
  size_id BIGINT REFERENCES size(id),
  view_size_type TEXT
);

CREATE TABLE product_alert_attribute_value_binding
(
  id BIGSERIAL PRIMARY KEY,
  product_alert_subscription_id BIGINT NOT NULL REFERENCES product_alert_subscription(id),
  attribute_value_id BIGINT REFERENCES attribute_value (id),
  UNIQUE (product_alert_subscription_id, attribute_value_id)
);