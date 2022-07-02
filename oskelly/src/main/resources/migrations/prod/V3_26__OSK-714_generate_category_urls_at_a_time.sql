CREATE OR REPLACE FUNCTION transliterate(p_string character varying)
	RETURNS character varying AS
$BODY$
select replace(
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
		'я', 'ya');
$BODY$
LANGUAGE sql IMMUTABLE
COST 100;

UPDATE category AS original
SET url_name = transliterated.url
FROM
	(SELECT
		 child.id id,
		 transliterate(replace(lower(string_agg(parent.display_name,
																						'/'
																 ORDER BY
																	 parent.left_order)), ' ', '-')) url
	 FROM
		 category child CROSS
		 JOIN
		 category parent
	 WHERE
		 parent.left_order <= child.left_order
		 AND parent.right_order >= child.right_order
	 	 AND parent.left_order <> 1
	 GROUP BY
		 child.id) transliterated
WHERE original.id = transliterated.id;

DROP FUNCTION transliterate(p_string CHARACTER VARYING );
