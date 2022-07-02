ALTER TABLE public.product_alert_subscription
	ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp;
