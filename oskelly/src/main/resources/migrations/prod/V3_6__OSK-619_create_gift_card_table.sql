CREATE TABLE gift_card (

    id BIGSERIAL PRIMARY KEY,

    -- стоимость сертификата (и одновременно скидка, которую он дает при оплате заказа);
    -- шестизначной суммы должно быть достаточно.
    amount NUMERIC(6) NOT NULL,

    -- код сертификата должен всегда быть уникальным;
    -- может быть NULL на момент создания записи: код заполняется только после оплаты сертификата
    code TEXT UNIQUE,

    -- покупатель сертификата
    buyer_id BIGINT REFERENCES "user"(id) NOT NULL,

    -- сертификат можно применить только к одному - единственному заказу
    order_id BIGINT REFERENCES "order"(id) UNIQUE,

    state TEXT NOT NULL,
    state_time TIMESTAMP WITH TIME ZONE NOT NULL,

    -- имя пользователя, дарящего сертификат
    giving_name TEXT NOT NULL,

    -- кому дарят сертификат
    recipient_name TEXT NOT NULL,
    recipient_email TEXT,
    recipient_address TEXT,

    -- кассовый чек об оплате сертификата
    buyer_check TEXT,

    -- срок действия сертификата;
    -- выставляется на момент оплаты покупки сертификата
    expires_at  TIMESTAMP WITH TIME ZONE
);
