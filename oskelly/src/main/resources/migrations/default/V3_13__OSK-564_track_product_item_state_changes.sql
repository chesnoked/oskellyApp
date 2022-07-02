CREATE TABLE state_change (
  id              BIGSERIAL PRIMARY KEY,
  product_item_id BIGINT REFERENCES product_item (id)

    -- хранить историю для удаленной вещи не нужно
  ON DELETE CASCADE
    -- иначе возникает нарушение целостности, когда вставляешь новую запись в product_item
    DEFERRABLE INITIALLY DEFERRED,
  new_state       TEXT                     NOT NULL,
  at              TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE OR REPLACE FUNCTION update_state_history()
  RETURNS TRIGGER AS
$$
BEGIN
  INSERT INTO state_change (product_item_id, new_state, at)
  VALUES (NEW.id, NEW.state, now());
  RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

-- при вставке новой вещи
CREATE TRIGGER on_new_product_item
BEFORE INSERT ON product_item
FOR EACH ROW
EXECUTE PROCEDURE update_state_history();

-- при обновлении _состояния_ вещи, если новое состояние отличается от уже имеющегося
CREATE TRIGGER on_product_item_update
AFTER UPDATE OF state
  ON product_item
FOR EACH ROW
WHEN (NEW.state <> OLD.state)
EXECUTE PROCEDURE update_state_history();
