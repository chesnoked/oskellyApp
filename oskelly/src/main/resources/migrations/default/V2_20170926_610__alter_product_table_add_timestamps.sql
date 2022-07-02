ALTER TABLE product
    ADD COLUMN create_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    ADD COLUMN send_to_moderator_time TIMESTAMP WITH TIME ZONE NULL;


UPDATE product
SET send_to_moderator_time = NOW()
WHERE product_state = 'NEED_MODERATION';