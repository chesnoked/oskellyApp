ALTER TABLE "order"

  ADD COLUMN guest_token text,

-- заказ, который находится в процессе формирования
-- (т.е. заказ со статусом CREATED), и который был создан анонимным пользователем,
-- не ссылается на существующего в системе пользователя.
  ALTER COLUMN buyer_id DROP NOT NULL,

-- кроме того, у такого заказа не будет выставлена сумма покупки (amount),
-- так как она выставляется только после завершения процесса оформления заказа
-- перед переходом на оплату.
  ALTER COLUMN amount DROP NOT NULL,

-- у заказа всегда должен быть покупатель, анонимный или зарегестрированный
  ADD CONSTRAINT valid_buyer CHECK (guest_token is not null or buyer_id is not null);

ALTER TABLE order_position
  ALTER COLUMN amount DROP NOT NULL,
  ALTER COLUMN commission DROP NOT NULL;
