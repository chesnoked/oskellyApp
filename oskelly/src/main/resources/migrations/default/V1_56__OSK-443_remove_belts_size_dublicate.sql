/* в размерах женских ремней есть два L размера. Один из них дублирует XL размер - сетка у них совпадает полностью.
В итоге на сайте должен быть только один L размер женских ремней. Второй - тот, что дублирует XL размер - нужно удалить.
Если на него уже ссылаются вещи, которые доступны для продажи, то удалить размер не получится.
*/
UPDATE product_item
SET size_id = NULL
WHERE size_id = (

  SELECT id
  FROM size
  WHERE category_id = (
    SELECT id
    FROM category
    WHERE url_name = 'woman-accessories-belts') AND international = 'L' AND russian = '105')

      AND delete_time IS NOT NULL;

DELETE FROM size
WHERE category_id = (

  SELECT id
  FROM category
  WHERE url_name = 'woman-accessories-belts')

AND international = 'L' AND russian = '105';

