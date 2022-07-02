create table exported_image (

  id bigserial PRIMARY KEY ,

  -- экспортируемое изображение
  image_id BIGINT REFERENCES image(id),

  -- nullable если процедура экспорта началась, но еще не завершилась.
  -- внешний идентификатор сохраняется после завершения процедуры экспорта.
  external_id TEXT,

  -- nullable если процедура экспорта началась, но еще не завершилась
  exported_at TIMESTAMP WITH TIME ZONE
)