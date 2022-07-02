CREATE TABLE publication_photo_sample (
    id SERIAL PRIMARY KEY,
    category_id BIGINT REFERENCES category(id) NOT NULL,
    image_path TEXT NOT NULL,
    photo_order INTEGER NOT NULL

);