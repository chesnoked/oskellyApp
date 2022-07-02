create table exported_product (

  id BIGSERIAL PRIMARY KEY,

  -- экспортируемый товар
  product_id BIGINT REFERENCES product(id),

  -- nullable если процедура экспорта началась, но еще не завершилась.
  -- внешний идентификатор сохраняется после завершения процедуры экспорта.
  external_id TEXT,

  -- nullable если процедура экспорта началась, но еще не завершилась
  exported_at TIMESTAMP WITH TIME ZONE
)