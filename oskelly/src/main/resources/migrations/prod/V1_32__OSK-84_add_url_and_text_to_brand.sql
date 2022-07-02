ALTER TABLE brand
    ADD COLUMN url text;

ALTER TABLE brand
    ADD COLUMN info_text text;

UPDATE brand SET url = LOWER(REGEXP_REPLACE(name,'[\s+]|[\\.]','-','g'));

UPDATE brand SET url = left(url, -1) WHERE url LIKE '%-';

ALTER TABLE brand
    ALTER COLUMN url SET NOT NULL;