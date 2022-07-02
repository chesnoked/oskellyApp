-- предложение о снижении цены
CREATE TABLE offer (

  id          BIGSERIAL PRIMARY KEY,

  -- товар, по которому проводится торг
  product_id BIGINT REFERENCES product (id) NOT NULL,

  -- предложенная цена
  price       NUMERIC NOT NULL,

  -- торгующаяся сторона
  offeror_id BIGINT REFERENCES "user" (id) NOT NULL,

  -- true - продавец принял предложение, false - отказался, null - еще не вынес решения
  is_accepted    BOOLEAN,

  created_at TIMESTAMP WITH TIME ZONE NOT NULL
)