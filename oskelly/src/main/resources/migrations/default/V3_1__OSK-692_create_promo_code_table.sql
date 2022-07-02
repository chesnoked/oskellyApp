CREATE TABLE promo_code (
    id         SERIAL PRIMARY KEY,

    -- промо-код должен всегда быть уникальным
    code       TEXT UNIQUE NOT NULL,

    -- размер скидки (от 0.0000 до 9.9999) (0: 0%, 0.5: 50%, 1: 100%)
    value      NUMERIC(5, 4) NOT NULL,

    -- срок действия промо-кода
    expires_at  TIMESTAMP WITH TIME ZONE
);

-- один промо-код можно применить к разным заказам одновременно
ALTER TABLE "order" ADD COLUMN promo_code_id INTEGER REFERENCES promo_code(id);

-- ты можешь использовать промо-код только в одном своем заказе
ALTER TABLE "order" ADD UNIQUE(promo_code_id, buyer_id);
