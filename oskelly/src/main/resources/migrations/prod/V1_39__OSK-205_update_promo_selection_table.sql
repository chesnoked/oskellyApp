alter table "promo_selection"
    add column promo_group TEXT,
    add column alt TEXT;

update "promo_selection"
set promo_group = 'INDEX_PROMO'
where promo_group IS NULL;