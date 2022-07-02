update size set waist_centimeters = '60-69' where category_id in (select id from category where url_name in ('man-accessories-belts','woman-accessories-belts')) and international = 'XXS';
update size set waist_centimeters = '70-79' where category_id in (select id from category where url_name in ('man-accessories-belts','woman-accessories-belts')) and international = 'XS';
update size set waist_centimeters = '80-89' where category_id in (select id from category where url_name in ('man-accessories-belts','woman-accessories-belts')) and international = 'S';
update size set waist_centimeters = '90-99' where category_id in (select id from category where url_name in ('man-accessories-belts','woman-accessories-belts')) and international = 'M';
update size set waist_centimeters = '100-109' where category_id in (select id from category where url_name in ('man-accessories-belts','woman-accessories-belts')) and international = 'L';
update size set waist_centimeters = '110-119' where category_id in (select id from category where url_name in ('man-accessories-belts','woman-accessories-belts')) and international = 'XL';
update size set waist_centimeters = '120-129' where category_id in (select id from category where url_name in ('man-accessories-belts','woman-accessories-belts')) and international = 'XXL';
update size set waist_centimeters = '130-139' where category_id in (select id from category where url_name in ('man-accessories-belts','woman-accessories-belts')) and international = 'XXXL';
