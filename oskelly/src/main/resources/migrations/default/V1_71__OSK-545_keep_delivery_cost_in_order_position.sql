-- стоимость доставки может быть null в том случае, если расчитать ее программно нельзя
-- и магазин согласовывает стоимость вручную
alter table order_position
    add column delivery_cost NUMERIC;
