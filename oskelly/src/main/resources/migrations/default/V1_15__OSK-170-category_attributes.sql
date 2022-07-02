INSERT INTO attribute (id, name) VALUES (3, 'Принт');

ALTER SEQUENCE attribute_id_seq RESTART WITH 4;

INSERT INTO attribute_value (id, attribute_id, value)
VALUES (23, 3, 'Абстрактный'),
	(24, 3, 'Крокодил'),
	(25, 3, 'Цветочный'),
	(26, 3, 'Другое'),
	(27, 3, 'Зебра'),
	(28, 3, 'Леопард');
ALTER SEQUENCE attribute_value_id_seq RESTART WITH 29;

ALTER SEQUENCE category_attribute_binding_id_seq RESTART WITH 3;

INSERT INTO category_attribute_binding (category_id, attribute_id) VALUES
	(2, 3),
	(48, 1),
	(48, 2),
	(48, 3),
	(79, 1),
	(79, 2),
	(79, 3),
	(112, 1),
	(112, 2),
	(112, 3);

ALTER SEQUENCE category_attribute_binding_id_seq RESTART WITH 13;