ALTER TABLE attribute_value
  ADD COLUMN transliterate_value TEXT;

CREATE OR REPLACE FUNCTION transliterate(p_string CHARACTER VARYING)
  RETURNS CHARACTER VARYING AS
$BODY$
SELECT replace(replace(
                   replace(
                       replace(
                           replace(
                               replace(
                                   replace(
                                       replace(
                                           replace(
                                               replace(
                                                   translate(lower($1),
                                                             'абвгдезийклмнопрстуфхцыэ',
                                                             'abvgdezijklmnoprstufhcye'),
                                                   'ъ', ''),
                                               'ь', ''),
                                           'ё', 'yo'),
                                       'ж', 'zh'),
                                   'ч', 'ch'),
                               'ш', 'sh'),
                           'щ', 'shch'),
                       'ю', 'yu'),
                   'я', 'ya'),
               ' ', '-');
$BODY$
LANGUAGE SQL
IMMUTABLE
COST 100;

UPDATE attribute_value
SET transliterate_value = transliterate("value");


DROP FUNCTION transliterate(p_string CHARACTER VARYING );