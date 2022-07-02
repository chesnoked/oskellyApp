ALTER TABLE size
  ADD COLUMN english TEXT,
	ADD COLUMN french TEXT,
	ADD COLUMN italian TEXT,
	ADD COLUMN danish TEXT,

	ADD COLUMN inches TEXT,
	ADD COLUMN centimeters TEXT,

	ADD COLUMN collar_inches TEXT,
	ADD COLUMN collar_centimeters TEXT,

	ADD COLUMN waist_inches TEXT,
	ADD COLUMN waist_centimeters TEXT,

	ADD COLUMN ring_russian TEXT,
	ADD COLUMN ring_european TEXT,

	ADD COLUMN waist_english_american TEXT,
	ADD COLUMN breast_english_american TEXT,

	ADD COLUMN jeans TEXT,

	ADD COLUMN height TEXT,
	ADD COLUMN age TEXT;

INSERT INTO size (category_id, international, russian, french, italian, english, american, danish) values
	((select id from category where url_name = 'woman-clothes'), 'XXS', '38',  '34',   '36',  '4',   '0',  '30'),
	((select id from category where url_name = 'woman-clothes'), 'XS' , '40',  '36',   '38',  '6',   '2',  '32/34'),
	((select id from category where url_name = 'woman-clothes'), 'S'  , '42',  '38',   '40',  '8',   '4',  '36'),
	((select id from category where url_name = 'woman-clothes'), 'M'  , '44',  '40',   '42',  '10',  '6',  '38'),
	((select id from category where url_name = 'woman-clothes'), 'L'  , '46',  '42',   '44',  '12',  '8',  '40'),
	((select id from category where url_name = 'woman-clothes'), 'XL' , '48',  '44',   '46',  '14',  '10', '42'),
	((select id from category where url_name = 'woman-clothes'), 'XXL', '50',  '46',   '48',  '16',  '12', '44');

INSERT INTO size (category_id, international, european, french, russian, english, american) values
	((select id from category where url_name = 'woman-shoes'), '34'   , '34'   ,'35',   '33',   '1',   '3,5'),
	((select id from category where url_name = 'woman-shoes'), '34,5' , '34,5' ,'35,5' , '33,5' , '1,5',  '4'),
	((select id from category where url_name = 'woman-shoes'), '35'   , '35'   ,'36'   , '34'   , '2'  ,  '4,5'),
	((select id from category where url_name = 'woman-shoes'), '35,5' , '35,5' ,'36,5' , '34,5' , '2,5',  '5'),
	((select id from category where url_name = 'woman-shoes'), '36'   , '36'   ,'37'   , '35'   , '3'  ,  '5,5'),
	((select id from category where url_name = 'woman-shoes'), '36,5' , '36,5' ,'37,5' , '35,5' , '3,5',  '6'),
	((select id from category where url_name = 'woman-shoes'), '37'   , '37'   ,'38'   , '36'   , '4'  ,  '6,5'),
	((select id from category where url_name = 'woman-shoes'), '37,5' , '37,5' ,'38,5' , '36,5' , '4,5',  '7'),
	((select id from category where url_name = 'woman-shoes'), '38'   , '38'   ,'39'   , '37'   , '5'  ,  '7,5'),
	((select id from category where url_name = 'woman-shoes'), '38,5' , '38,5' ,'39,5' , '37,5' , '5,5',  '8'),
	((select id from category where url_name = 'woman-shoes'), '39'   , '39'   ,'40'   , '38'   , '6'  ,  '8,5'),
	((select id from category where url_name = 'woman-shoes'), '39,5' , '39,5' ,'40,5' , '38,5' , '6,5',  '9'),
	((select id from category where url_name = 'woman-shoes'), '40'   , '40'   ,'41'   , '39'   , '7'  ,  '9,5'),
	((select id from category where url_name = 'woman-shoes'), '40,5' , '40,5' ,'41,5' , '39,5' , '7,5',  '10'),
	((select id from category where url_name = 'woman-shoes'), '41'   , '41'   ,'42'   , '40'   , '8'  ,  '10,5'),
	((select id from category where url_name = 'woman-shoes'), '41,5' , '41,5' ,'42,5' , '40,5' , '8,5',  '11'),
	((select id from category where url_name = 'woman-shoes'), '42'   , '42'   ,'43'   , '41'   , '9'  ,  '11,5');

insert into size (category_id, russian, european, french, english, american) values
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '70A',   '70A',   '85A',   '32A',  '32A'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '70B',   '70B',   '85B',   '32B',  '32B'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '70C',   '70C',   '85C',   '32C',  '32C'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '70D',   '70D',   '85D',   '32D',  '32D'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '70DD',  '70DD',  '85DD',  '32DD', '32DD'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '70E',   '70E',   '85E',   '32E',  '32E'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '75A',   '75A',   '90A',   '34A',  '34A'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '75B',   '75B',   '90B',   '34B',  '34B'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '75C',   '75C',   '90C',   '34C',  '34C'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '75D',   '75D',   '90D',   '34D',  '34D'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '75DD',  '75DD',  '90DD',  '34DD', '34DD'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '75E',   '75E',   '90E',   '34E',  '34E'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '80A',   '80A',   '80A',   '36A',  '36A'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '80B',   '80B',   '80B',   '36B',  '36B'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '80C',   '80C',   '80C',   '36C',  '36C'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '80D',   '80D',   '80D',   '36D',  '36D'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '80DD',  '80DD',  '80DD',  '36DD', '36DD'),
	((select id from category where url_name = 'woman-beachwear-swimsuits'), '80E',   '80E',   '80E',   '36E',  '36E');

insert into size (category_id, russian, french, italian, english, american) values
	((select id from category where url_name = 'woman-jewellery-rings'), '15'	, '46',	'8',	'H½',	'4'),
	((select id from category where url_name = 'woman-jewellery-rings'), '15½'	, '48',	'9',	'I½',	'4½'),
	((select id from category where url_name = 'woman-jewellery-rings'), '15¾'	, '49,5',	 '10/11',	'J½',	'5'),
	((select id from category where url_name = 'woman-jewellery-rings'), '16'	, '50,5',	'12',	'K½',	'5½'),
	((select id from category where url_name = 'woman-jewellery-rings'), '16½'	, '52',	'13',	'L½',	'6'),
	((select id from category where url_name = 'woman-jewellery-rings'), '17'	, '53,5',	'14/15',	'M½',	'6½'),
	((select id from category where url_name = 'woman-jewellery-rings'), '17¼'	, '54,5',	'15/16',	'N½',	'7'),
	((select id from category where url_name = 'woman-jewellery-rings'), '17¾'	, '55,5',	'17',	'O½',	'7½'),
	((select id from category where url_name = 'woman-jewellery-rings'), '18'	, '57',	'18',	'P½',	'8'),
	((select id from category where url_name = 'woman-jewellery-rings'), '18½'	, '58,5',	'19',	'Q½',	'8½'),
	((select id from category where url_name = 'woman-jewellery-rings'), '19'	, '60',	'20',	'R½', '');

insert into size (category_id, international, european, inches, centimeters) values
	((select id from category where url_name = 'woman-accessories-gloves'), 'XXS','6',	'6',	 '15,2'),
	((select id from category where url_name = 'woman-accessories-gloves'), 'XS',	'6,5','6,5',	'16,5'),
	((select id from category where url_name = 'woman-accessories-gloves'), 'S',	'7',	'7',	 '17,8'),
	((select id from category where url_name = 'woman-accessories-gloves'), 'M',	'7,5','7,5',	'19'),
	((select id from category where url_name = 'woman-accessories-gloves'), 'L',	'8',	'8',	 '20,3'),
	((select id from category where url_name = 'woman-accessories-gloves'), 'XL',	'8,5','8,5',	'21,6'),
	((select id from category where url_name = 'woman-accessories-gloves'), 'XXL','9',	'9',	 '22,9'),
  ((select id from category where url_name = 'woman-accessories-gloves'), 'XXXL','9,5','9,5', '24)');

insert into size (category_id, international, russian, italian, english, american) values
	((select id from category where url_name = 'woman-accessories-belts'), 'XXS',	'60-65',	'36',	'4',	'0'),
	((select id from category where url_name = 'woman-accessories-belts'), 'XS',	'70-75',	'38',	'6',	'2'),
	((select id from category where url_name = 'woman-accessories-belts'), 'S',	'80-85',	'40',	'8',	'4'),
	((select id from category where url_name = 'woman-accessories-belts'), 'M',	'90-95',	'42',	'10',	'6'),
	((select id from category where url_name = 'woman-accessories-belts'), 'L',	'100',	'44',	'12',	'8'),
	((select id from category where url_name = 'woman-accessories-belts'), 'L',	'105',	'46',	'14',	'10'),
	((select id from category where url_name = 'woman-accessories-belts'), 'XL',	'105',	'46',	'14',	'10'),
	((select id from category where url_name = 'woman-accessories-belts'), 'XXL',	'110',	'48',	'16',	'12');

insert into size (category_id, international, russian, european, american, english) values
	((select id from category where url_name = 'woman-accessories-hats'), 'XS',	'52-53','52-53',	'6⅜-6½',	'6½-6⅝'),
	((select id from category where url_name = 'woman-accessories-hats'), 'S',	'54-55','54-55',	'6⅝-6¾',	'6¾-6⅞'),
	((select id from category where url_name = 'woman-accessories-hats'), 'M',	'56-57','56-57',	'6⅞-7',	'7-7⅛'),
	((select id from category where url_name = 'woman-accessories-hats'), 'L',	'58-59','58-59',	'7⅛-7¼',	'7¼-7⅜'),
	((select id from category where url_name = 'woman-accessories-hats'), 'XL',	'60-61','60-61',	'7⅜-7½',	'7½-7⅝');

insert into size (category_id, 	international, russian, french, italian, breast_english_american, waist_english_american, danish) values
	((select id from category where url_name = 'man-clothes'),'XXS',	'42',	'42',	'42',	'32',	'26',	'42'),
	((select id from category where url_name = 'man-clothes'),'XS',	'44',		'44',	'44',	'34',	'28',	'44'),
	((select id from category where url_name = 'man-clothes'),'S',	'46',		'46',	'46',	'36',	'30',	'46'),
	((select id from category where url_name = 'man-clothes'),'M',	'48',		'48',	'48',	'38',	'32',	'48'),
	((select id from category where url_name = 'man-clothes'),'L',	'50',		'50',	'50',	'40',	'34',	'50'),
	((select id from category where url_name = 'man-clothes'),'XL',	'52',		'52',	'52',	'42',	'36',	'52'),
	((select id from category where url_name = 'man-clothes'),'XXL',	'54',	'54',	'54',	'44',	'38',	'54'),
	((select id from category where url_name = 'man-clothes'),'XXXL',	'56',	'56',	'56',	'46',	'40',	'56');

insert into size (category_id, international, collar_inches, collar_centimeters) values
	((select id from category where url_name = 'man-clothes-pajamas'), 'XXS',	'14',	'36'),
	((select id from category where url_name = 'man-clothes-pajamas'), 'XS',	'14,5',	'37'),
	((select id from category where url_name = 'man-clothes-pajamas'), 'S',	'15',	'38'),
	((select id from category where url_name = 'man-clothes-pajamas'), 'M',	'15,5',	'39'),
	((select id from category where url_name = 'man-clothes-pajamas'), 'L',	'16',	'41'),
	((select id from category where url_name = 'man-clothes-pajamas'), 'XL',	'16,5',	'42'),
	((select id from category where url_name = 'man-clothes-pajamas'), 'XXL',	'17',	'43'),
	((select id from category where url_name = 'man-clothes-pajamas'), 'XXXL',	'17,5',	'44');

insert into size (category_id, 	international, european, russian, english, american) values
	((select id from category where url_name = 'man-shoes'), '39',   '39',     '39',    '5',     '6'),
	((select id from category where url_name = 'man-shoes'), '39,5', '39,5',   '39,5',  '5,5',   '6,5'),
	((select id from category where url_name = 'man-shoes'), '40',   '40',     '40',    '6',     '7'),
	((select id from category where url_name = 'man-shoes'), '40,5', '40,5',   '40,5',  '6,5',   '7,5'),
	((select id from category where url_name = 'man-shoes'), '41',   '41',     '41',    '7',     '8'),
	((select id from category where url_name = 'man-shoes'), '41,5', '41,5',   '41,5',  '7,5',   '8,5'),
	((select id from category where url_name = 'man-shoes'), '42',   '42',     '42',    '8',     '9'),
	((select id from category where url_name = 'man-shoes'), '42,5', '42,5',   '42,5',  '8,5',   '9,5'),
	((select id from category where url_name = 'man-shoes'), '43',   '43',     '43',    '9',     '10'),
	((select id from category where url_name = 'man-shoes'), '43,5', '43,5',   '43,5',  '9,5',   '10,5'),
	((select id from category where url_name = 'man-shoes'), '44',   '44',     '44',    '10',    '11'),
	((select id from category where url_name = 'man-shoes'), '44,5', '44,5',   '44,5',  '10,5',  '11,5'),
	((select id from category where url_name = 'man-shoes'), '45',   '45',     '45',    '11',    '12'),
	((select id from category where url_name = 'man-shoes'), '45,5', '45,5',   '45,5',  '11,5',  '12,5'),
	((select id from category where url_name = 'man-shoes'), '46',   '46',     '46',    '12',    '13'),
	((select id from category where url_name = 'man-shoes'), '46,5', '46,5',   '46,5',  '12,5',  '13,5'),
	((select id from category where url_name = 'man-shoes'), '47',   '47',     '47',    '13',    '14'),
	((select id from category where url_name = 'man-shoes'), '47,5', '47,5',   '47,5',  '13,5',  '14,5'),
	((select id from category where url_name = 'man-shoes'), '48',   '48',     '48',    '14',    '15');

insert into size (category_id, international, european, waist_inches, waist_centimeters) values
	((select id from category where url_name = 'man-accessories-belts'), 'XS',	'85',	'28',	'70-75'),
	((select id from category where url_name = 'man-accessories-belts'), 'S',	'90',	'30',	'75-80'),
	((select id from category where url_name = 'man-accessories-belts'), 'M',	'95',	'32',	'80-85'),
	((select id from category where url_name = 'man-accessories-belts'), 'L',	'100',	'34',	'85-90'),
	((select id from category where url_name = 'man-accessories-belts'), 'XL',	'105',	'36',	'90-95'),
	((select id from category where url_name = 'man-accessories-belts'), 'XXL',	'110',	'38',	'95-100'),
	((select id from category where url_name = 'man-accessories-belts'), 'XXXL',	'115',	'40',	'100-105');

insert into size (category_id, international, inches, centimeters ) values
	((select id from category where url_name = 'man-accessories-gloves'), 'XS',	'7',	'17,8'),
	((select id from category where url_name = 'man-accessories-gloves'), 'S',	'7,5-8',	'19-20,3'),
	((select id from category where url_name = 'man-accessories-gloves'), 'M',	'8,5-9',	'21.6-22.9'),
	((select id from category where url_name = 'man-accessories-gloves'), 'L',	'9,5-10',	'24-25,4'),
	((select id from category where url_name = 'man-accessories-gloves'), 'XL',	'10,5-11',	'26.7-27.9'),
	((select id from category where url_name = 'man-accessories-gloves'), 'XXL',	'11,5-12',	'29.2-30.5');


insert into size (category_id, ring_russian, ring_european, english, american) values
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '17,3',	'54',	'O',	'7'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '17,75',	'55,3',	'P',	'7½'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '18,2',	'56,6',	'Q',	'8'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '18,6',	'57,8',	'Q½-R',	'8½'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '19',	'59,1',	'R½',	'9'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '19,4',	'60,3',	'S½-T',	'9½'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '19,8',	'61,6',	'T½-U',	'10'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '20,2',	'62,8',	'U-U½',	'10½'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '20,6',	'64,1',	'V½',	'11'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '21',	'65,3',	'W½',	'11½'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '21,4',	'66,7',	'Y',	'12'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '21,8',	'68',	'Z',	'12½'),
	((select id from category where url_name = 'man-accessories-jewellery-rings'), '22,3',	'69,2',	'Z½',	'13');

insert into size (category_id, international, european, american, english) VALUES
	((select id from category where url_name = 'man-accessories-hats'), 'XS',	'52-53',	'6½-6⅝',	'6⅜-6½'),
	((select id from category where url_name = 'man-accessories-hats'), 'S',	'54-55',	'6¾-6⅞',	'6⅝-6¾'),
	((select id from category where url_name = 'man-accessories-hats'), 'M',	'56-57',	'7-7⅛',	'6⅞-7'),
	((select id from category where url_name = 'man-accessories-hats'), 'L',	'58-59',	'7¼-7⅜',	'7⅛-7¼'),
	((select id from category where url_name = 'man-accessories-hats'), 'XL',	'60-61',	'7½-7⅝',	'7⅜-7½'),
	((select id from category where url_name = 'man-accessories-hats'), 'XXL',	'62-63',	'7¾-7⅞',	'7⅝-7¾');

INSERT INTO size (category_id, international, russian, french, italian, english, american, danish, jeans) values
	((select id from category where url_name = 'woman-clothes-jeans'), 'XXS', '38',  '34',   '36',  '4',   '0', '23',  '30'),
	((select id from category where url_name = 'woman-clothes-jeans'), 'XS' , '40',  '36',   '38',  '6',   '2', '24/25', '32/34'),
	((select id from category where url_name = 'woman-clothes-jeans'), 'S'  , '42',  '38',   '40',  '8',   '4', '26/27', '36'),
	((select id from category where url_name = 'woman-clothes-jeans'), 'M'  , '44',  '40',   '42',  '10',  '6', '27/28', '38'),
	((select id from category where url_name = 'woman-clothes-jeans'), 'L'  , '46',  '42',   '44',  '12',  '8', '29/30', '40'),
	((select id from category where url_name = 'woman-clothes-jeans'), 'XL' , '48',  '44',   '46',  '14',  '10','31/32', '42'),
	((select id from category where url_name = 'woman-clothes-jeans'), 'XXL', '50',  '46',   '48',  '16',  '12','32/33', '44');

insert into size (category_id, 	international, russian, french, italian, breast_english_american, waist_english_american, danish, jeans) values
	((select id from category where url_name = 'man-clothes-jeans'),'XXS',	'42',	'42',	'42',	'32',	'26',	'42', '26/27'),
	((select id from category where url_name = 'man-clothes-jeans'),'XS',	'44',		'44',	'44',	'34',	'28',	'44', '28/29'),
	((select id from category where url_name = 'man-clothes-jeans'),'S',	'46',		'46',	'46',	'36',	'30',	'46', '30/31'),
	((select id from category where url_name = 'man-clothes-jeans'),'M',	'48',		'48',	'48',	'38',	'32',	'48', '32/33'),
	((select id from category where url_name = 'man-clothes-jeans'),'L',	'50',		'50',	'50',	'40',	'34',	'50', '34/35'),
	((select id from category where url_name = 'man-clothes-jeans'),'XL',	'52',		'52',	'52',	'42',	'36',	'52', '36/37'),
	((select id from category where url_name = 'man-clothes-jeans'),'XXL',	'54',	'54',	'54',	'44',	'38',	'54', '38/39'),
	((select id from category where url_name = 'man-clothes-jeans'),'XXXL',	'56',	'56',	'56',	'46',	'40',	'56', '40/41');

insert into size (category_id, height, age) VALUES
	((select id from category where url_name = 'girls-babies'), '56 см', '1 МЕСЯЦ'),
	((select id from category where url_name = 'girls-babies'), '62 см', '3 МЕСЯЦА'),
	((select id from category where url_name = 'girls-babies'), '68 см', '6 МЕСЯЦЕВ'),
	((select id from category where url_name = 'girls-babies'), '74 см', '9 МЕСЯЦЕВ'),
	((select id from category where url_name = 'girls-babies'), '80 см', '12 МЕСЯЦЕВ'),
	((select id from category where url_name = 'girls-babies'), '86 см', '1.5 ГОДА'),
	((select id from category where url_name = 'girls-babies'), '92 см', '2 ГОДА'),
	((select id from category where url_name = 'girls-babies'), '98 см', '3 ГОДА');

insert into size (category_id, height, age) VALUES
	((select id from category where url_name = 'boys-babies'), '56 см', '1 МЕСЯЦ'),
	((select id from category where url_name = 'boys-babies'), '62 см', '3 МЕСЯЦА'),
	((select id from category where url_name = 'boys-babies'), '68 см', '6 МЕСЯЦЕВ'),
	((select id from category where url_name = 'boys-babies'), '74 см', '9 МЕСЯЦЕВ'),
	((select id from category where url_name = 'boys-babies'), '80 см', '12 МЕСЯЦЕВ'),
	((select id from category where url_name = 'boys-babies'), '86 см', '1.5 ГОДА'),
	((select id from category where url_name = 'boys-babies'), '92 см', '2 ГОДА'),
	((select id from category where url_name = 'boys-babies'), '98 см', '3 ГОДА');

insert into size (category_id, height, age) VALUES
((select id from category where url_name = 'boys-teen'), '104 см', '4 ГОДА'),
((select id from category where url_name = 'boys-teen'), '110 см', '5 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '116 см', '6 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '122 см', '7 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '128 см', '8 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '134 см', '9 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '140 см', '10 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '146 см', '11 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '152 см', '12 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '158 см', '13 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '164 см', '14 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '167 см', '15 ЛЕТ'),
((select id from category where url_name = 'boys-teen'), '169 см', '16 ЛЕТ');

insert into size (category_id, height, age) VALUES
((select id from category where url_name = 'girls-teen'), '104 см', '4 ГОДА'),
((select id from category where url_name = 'girls-teen'), '110 см', '5 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '116 см', '6 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '122 см', '7 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '128 см', '8 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '134 см', '9 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '140 см', '10 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '146 см', '11 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '152 см', '12 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '158 см', '13 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '164 см', '14 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '167 см', '15 ЛЕТ'),
((select id from category where url_name = 'girls-teen'), '169 см', '16 ЛЕТ');


insert into size (category_id, russian, european, english, american) values
((select id from category where url_name = 'girls-babies-shoes'), '16', '17', '0', '1'),
((select id from category where url_name = 'girls-babies-shoes'), '17', '18', '1', '2'),
((select id from category where url_name = 'girls-babies-shoes'), '18', '19', '2', '3'),
((select id from category where url_name = 'girls-babies-shoes'), '19', '20', '3', '4'),
((select id from category where url_name = 'girls-babies-shoes'), '20', '21', '4', '5'),
((select id from category where url_name = 'girls-babies-shoes'), '21', '22', '4,5','5,5'),
((select id from category where url_name = 'girls-babies-shoes'), '22', '23', '5', '6'),
((select id from category where url_name = 'girls-babies-shoes'), '23', '24', '6', '7'),
((select id from category where url_name = 'girls-babies-shoes'), '24', '25', '7', '8'),
((select id from category where url_name = 'girls-babies-shoes'), '25', '26', '7,5', '8,5'),
((select id from category where url_name = 'girls-babies-shoes'), '26', '27', '8,5', '9,5'),
((select id from category where url_name = 'girls-babies-shoes'), '27', '28', '9', '10'),
((select id from category where url_name = 'girls-babies-shoes'), '28', '29', '10', '11'),
((select id from category where url_name = 'girls-babies-shoes'), '29', '30', '11', '12'),
((select id from category where url_name = 'girls-babies-shoes'), '30', '31', '11,5', '12,5'),
((select id from category where url_name = 'girls-babies-shoes'), '31', '32', '12', '13'),
((select id from category where url_name = 'girls-babies-shoes'), '32', '33', '13', '14'),
((select id from category where url_name = 'girls-babies-shoes'), '33', '34', '1', '2'),
((select id from category where url_name = 'girls-babies-shoes'), '34', '35', '2', '2,5-3'),
((select id from category where url_name = 'girls-babies-shoes'), '35', '36', '2,5', '3,5-4'),
((select id from category where url_name = 'girls-babies-shoes'), '36', '37', '3', '5'),
((select id from category where url_name = 'girls-babies-shoes'), '37', '38', '4', '5-5,5'),
((select id from category where url_name = 'girls-babies-shoes'), '38', '39', '5', '6'),
((select id from category where url_name = 'girls-babies-shoes'), '39', '40', '6', '7'),
((select id from category where url_name = 'girls-babies-shoes'), '40', '41', '7', '8');

insert into size (category_id, russian, european, english, american) values
	((select id from category where url_name = 'boys-babies-shoes'), '16', '17', '0', '1'),
	((select id from category where url_name = 'boys-babies-shoes'), '17', '18', '1', '2'),
	((select id from category where url_name = 'boys-babies-shoes'), '18', '19', '2', '3'),
	((select id from category where url_name = 'boys-babies-shoes'), '19', '20', '3', '4'),
	((select id from category where url_name = 'boys-babies-shoes'), '20', '21', '4', '5'),
	((select id from category where url_name = 'boys-babies-shoes'), '21', '22', '4,5','5,5'),
	((select id from category where url_name = 'boys-babies-shoes'), '22', '23', '5', '6'),
	((select id from category where url_name = 'boys-babies-shoes'), '23', '24', '6', '7'),
	((select id from category where url_name = 'boys-babies-shoes'), '24', '25', '7', '8'),
	((select id from category where url_name = 'boys-babies-shoes'), '25', '26', '7,5', '8,5'),
	((select id from category where url_name = 'boys-babies-shoes'), '26', '27', '8,5', '9,5'),
	((select id from category where url_name = 'boys-babies-shoes'), '27', '28', '9', '10'),
	((select id from category where url_name = 'boys-babies-shoes'), '28', '29', '10', '11'),
	((select id from category where url_name = 'boys-babies-shoes'), '29', '30', '11', '12'),
	((select id from category where url_name = 'boys-babies-shoes'), '30', '31', '11,5', '12,5'),
	((select id from category where url_name = 'boys-babies-shoes'), '31', '32', '12', '13'),
	((select id from category where url_name = 'boys-babies-shoes'), '32', '33', '13', '14'),
	((select id from category where url_name = 'boys-babies-shoes'), '33', '34', '1', '2'),
	((select id from category where url_name = 'boys-babies-shoes'), '34', '35', '2', '2,5-3'),
	((select id from category where url_name = 'boys-babies-shoes'), '35', '36', '2,5', '3,5-4'),
	((select id from category where url_name = 'boys-babies-shoes'), '36', '37', '3', '5'),
	((select id from category where url_name = 'boys-babies-shoes'), '37', '38', '4', '5-5,5'),
	((select id from category where url_name = 'boys-babies-shoes'), '38', '39', '5', '6'),
	((select id from category where url_name = 'boys-babies-shoes'), '39', '40', '6', '7'),
	((select id from category where url_name = 'boys-babies-shoes'), '40', '41', '7', '8');

