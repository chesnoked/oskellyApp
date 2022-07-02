ALTER TABLE promo_code ADD COLUMN dtype TEXT;
-- на текущий момент в системе создавались только промокоды в процентах.
UPDATE promo_code SET dtype = 'FractionalPromoCode';
ALTER TABLE promo_code ALTER COLUMN dtype SET NOT NULL;

ALTER TABLE promo_code
  -- скидка промокода в рублях
  ADD COLUMN absolute_value NUMERIC,
  -- цена, начиная с которой промокод работает
  ADD COLUMN begin_price NUMERIC,
  -- промокод теперь необязательно выражается в процентах
  ALTER COLUMN value DROP NOT NULL;
