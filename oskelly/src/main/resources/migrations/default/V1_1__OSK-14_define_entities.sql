CREATE TABLE IF NOT EXISTS brand (
  id   BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS category (
  id           BIGSERIAL PRIMARY KEY,
  url_name     TEXT                            NOT NULL UNIQUE,
  display_name TEXT                            NOT NULL,
  left_order   INTEGER                         NOT NULL,
  right_order  INTEGER                         NOT NULL,
  parent_id    BIGINT REFERENCES category (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS size (
  id            BIGSERIAL PRIMARY KEY,
  category_id   BIGINT NOT NULL REFERENCES category (id),
  russian       TEXT,
  european      TEXT,
  american      TEXT,
  international TEXT
);

-- У каждой категории товаров может быть несколько атрибутов,
-- например, "рубашки" -> "материал", "цвет", "сезон".
CREATE TABLE IF NOT EXISTS attribute (
  id   BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS category_attribute_binding (
  id           BIGSERIAL PRIMARY KEY,
  category_id  BIGINT REFERENCES category (id),
  attribute_id BIGINT REFERENCES attribute (id),
  UNIQUE (category_id, attribute_id)
);

-- у каждого атрибута может быть конечный список значений,
-- например, "материал" -> { "хлопок", "ситец", "трикотаж" }
CREATE TABLE IF NOT EXISTS attribute_value (
  id           BIGSERIAL PRIMARY KEY,
  attribute_id BIGINT NOT NULL REFERENCES attribute (id),
  value        TEXT,
  -- у одного и того же атрибута не может быть повторяющихся значений
  UNIQUE (attribute_id, value)
);

-- Состояние товара (как новый и т.п.)
CREATE TABLE IF NOT EXISTS product_condition (
	id   BIGSERIAL PRIMARY KEY,
	name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS product (
  id                   BIGSERIAL PRIMARY KEY,
  name                 TEXT   NOT NULL,
  brand_id             BIGINT NOT NULL REFERENCES brand (id),
  size_id              BIGINT REFERENCES size (id),
  category_id          BIGINT NOT NULL REFERENCES category (id),
  seller_id            BIGINT NOT NULL REFERENCES "user" (id),
  product_state        TEXT   NOT NULL,
  start_price          NUMERIC,
  current_price        NUMERIC,
  buy_price            NUMERIC,
  description          TEXT,
  payment_description  TEXT,
  delivery_description TEXT,
  publish_time         TIMESTAMPTZ,
  product_condition_id BIGINT REFERENCES product_condition (id)
);

-- у товара может быть много аттрибутов с конкретными значениями
-- (товар: ботинки) -> (цвет: красный), (материал: кожа)
-- конкретное значение атрибута может быть присвоено разным товарам
-- (цвет: красный) -> (товар: ботинки), (товар: пальто)
CREATE TABLE IF NOT EXISTS product_attribute_value_binding (
  id                 BIGSERIAL PRIMARY KEY,
  product_id         BIGINT REFERENCES product (id),
  attribute_value_id BIGINT REFERENCES attribute_value (id),
  -- (товар: ботинки) -X-> (цвет: красный), (цвет: красный)
  UNIQUE (product_id, attribute_value_id)
);

CREATE TABLE IF NOT EXISTS product_status (
  id   BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS product_status_binding (
  id                BIGSERIAL PRIMARY KEY,
  product_id        BIGINT REFERENCES product (id),
  product_status_id BIGINT REFERENCES product_status (id),
  UNIQUE (product_id, product_status_id)
);

CREATE TABLE IF NOT EXISTS image (
  id             BIGSERIAL PRIMARY KEY,
  product_id     BIGINT REFERENCES product (id),
  image_path     TEXT    NOT NULL,
  thumbnail_path TEXT,
  is_main        BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS wish_list (
  id         BIGSERIAL PRIMARY KEY,
  product_id BIGINT REFERENCES product (id),
  user_id    BIGINT REFERENCES "user" (id),
  UNIQUE (product_id, user_id)
);

CREATE TABLE IF NOT EXISTS authority (
  id   BIGSERIAL PRIMARY KEY,
  name TEXT
);

CREATE TABLE IF NOT EXISTS user_authority_binding (
  id           BIGSERIAL PRIMARY KEY,
  user_id      BIGINT REFERENCES "user" (id),
  authority_id BIGINT REFERENCES authority (id),
  UNIQUE (user_id, authority_id)
);
