CREATE TABLE promo_gallery (
	id          BIGSERIAL PRIMARY KEY,
	url         TEXT,
	image_name  TEXT,
	order_index BIGINT
);

INSERT INTO promo_gallery (url, image_name, order_index) VALUES
	('/', 'slide2.jpg', 1);

CREATE TABLE promo_selection (
	id          BIGSERIAL PRIMARY KEY,
	first_line  TEXT,
	second_line TEXT,
	third_line  TEXT,
	url         TEXT,
	image_name  TEXT,
	order_index BIGINT
);

INSERT INTO promo_selection (first_line, second_line, third_line, url, image_name, order_index) VALUES
	('Платья', 'base forms', 'смотреть бренд', '/', 'baseforms.jpg', 1);