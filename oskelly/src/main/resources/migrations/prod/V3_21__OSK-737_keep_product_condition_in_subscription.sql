ALTER TABLE product_alert_subscription
		ADD column product_condition_id BIGINT REFERENCES product_condition(id);
