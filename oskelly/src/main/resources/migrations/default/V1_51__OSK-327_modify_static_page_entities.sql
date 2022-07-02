CREATE TABLE static_page_tag(
	id BIGSERIAL PRIMARY KEY,
	name TEXT UNIQUE
);

ALTER TABLE static_page
	ADD COLUMN image_path TEXT,
	ADD COLUMN status TEXT,
	ADD COLUMN tag_id BIGINT REFERENCES static_page_tag(id);