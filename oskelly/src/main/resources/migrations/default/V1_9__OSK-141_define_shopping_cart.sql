-- Корзина товаров пользователя
CREATE TABLE shopping_cart (

  id         BIGSERIAL PRIMARY KEY,
  user_id    BIGSERIAL REFERENCES "user" (id)  NOT NULL,
  product_id BIGSERIAL REFERENCES product (id) NOT NULL,

  -- время добавления товара в корзину - для сортировки положенных в корзину товаров
  add_time   TIMESTAMPTZ                       NOT NULL DEFAULT now(),

  -- нельзя добавить один и тот же товар в одну корзину несколько раз
  UNIQUE (user_id, product_id)
)