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
	ADD COLUMN breast_english_american TEXT;

INSERT INTO size (category_id, international, russian, french, italian, english, american, danish) values
	((select id from category where url_name = 'women-clothes'), 'XXS', '38',  '34',   '36',  '4',   '0',  '30'),
	((select id from category where url_name = 'women-clothes'), 'XS' , '40',  '36',   '38',  '6',   '2',  '32/34'),
	((select id from category where url_name = 'women-clothes'), 'S'  , '42',  '38',   '40',  '8',   '4',  '36'),
	((select id from category where url_name = 'women-clothes'), 'M'  , '44',  '40',   '42',  '10',  '6',  '38'),
	((select id from category where url_name = 'women-clothes'), 'L'  , '46',  '42',   '44',  '12',  '8',  '40'),
	((select id from category where url_name = 'women-clothes'), 'XL' , '48',  '44',   '46',  '14',  '10', '42'),
	((select id from category where url_name = 'women-clothes'), 'XXL', '50',  '46',   '48',  '16',  '12', '44');

INSERT INTO size (category_id, international, european, french, russian, english, american) values
	((select id from category where url_name = 'women-footwear'), '34'   , '34'   ,'35',   '33',   '1',   '3,5'),
	((select id from category where url_name = 'women-footwear'), '34,5' , '34,5' ,'35,5' , '33,5' , '1,5',  '4'),
	((select id from category where url_name = 'women-footwear'), '35'   , '35'   ,'36'   , '34'   , '2'  ,  '4,5'),
	((select id from category where url_name = 'women-footwear'), '35,5' , '35,5' ,'36,5' , '34,5' , '2,5',  '5'),
	((select id from category where url_name = 'women-footwear'), '36'   , '36'   ,'37'   , '35'   , '3'  ,  '5,5'),
	((select id from category where url_name = 'women-footwear'), '36,5' , '36,5' ,'37,5' , '35,5' , '3,5',  '6'),
	((select id from category where url_name = 'women-footwear'), '37'   , '37'   ,'38'   , '36'   , '4'  ,  '6,5'),
	((select id from category where url_name = 'women-footwear'), '37,5' , '37,5' ,'38,5' , '36,5' , '4,5',  '7'),
	((select id from category where url_name = 'women-footwear'), '38'   , '38'   ,'39'   , '37'   , '5'  ,  '7,5'),
	((select id from category where url_name = 'women-footwear'), '38,5' , '38,5' ,'39,5' , '37,5' , '5,5',  '8'),
	((select id from category where url_name = 'women-footwear'), '39'   , '39'   ,'40'   , '38'   , '6'  ,  '8,5'),
	((select id from category where url_name = 'women-footwear'), '39,5' , '39,5' ,'40,5' , '38,5' , '6,5',  '9'),
	((select id from category where url_name = 'women-footwear'), '40'   , '40'   ,'41'   , '39'   , '7'  ,  '9,5'),
	((select id from category where url_name = 'women-footwear'), '40,5' , '40,5' ,'41,5' , '39,5' , '7,5',  '10'),
	((select id from category where url_name = 'women-footwear'), '41'   , '41'   ,'42'   , '40'   , '8'  ,  '10,5'),
	((select id from category where url_name = 'women-footwear'), '41,5' , '41,5' ,'42,5' , '40,5' , '8,5',  '11'),
	((select id from category where url_name = 'women-footwear'), '42'   , '42'   ,'43'   , '41'   , '9'  ,  '11,5');

insert into size (category_id, russian, european, french, english, american) values
	((select id from category where url_name = 'women-clothes-swimsuits'), '70A',   '70A',   '85A',   '32A',  '32A'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '70B',   '70B',   '85B',   '32B',  '32B'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '70C',   '70C',   '85C',   '32C',  '32C'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '70D',   '70D',   '85D',   '32D',  '32D'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '70DD',  '70DD',  '85DD',  '32DD', '32DD'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '70E',   '70E',   '85E',   '32E',  '32E'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '75A',   '75A',   '90A',   '34A',  '34A'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '75B',   '75B',   '90B',   '34B',  '34B'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '75C',   '75C',   '90C',   '34C',  '34C'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '75D',   '75D',   '90D',   '34D',  '34D'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '75DD',  '75DD',  '90DD',  '34DD', '34DD'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '75E',   '75E',   '90E',   '34E',  '34E'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '80A',   '80A',   '80A',   '36A',  '36A'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '80B',   '80B',   '80B',   '36B',  '36B'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '80C',   '80C',   '80C',   '36C',  '36C'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '80D',   '80D',   '80D',   '36D',  '36D'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '80DD',  '80DD',  '80DD',  '36DD', '36DD'),
	((select id from category where url_name = 'women-clothes-swimsuits'), '80E',   '80E',   '80E',   '36E',  '36E');

insert into size (category_id, russian, french, italian, english, american) values
	((select id from category where url_name = 'women-jewelry-rings'), '15'	, '46',	'8',	'H½',	'4'),
	((select id from category where url_name = 'women-jewelry-rings'), '15½'	, '48',	'9',	'I½',	'4½'),
	((select id from category where url_name = 'women-jewelry-rings'), '15¾'	, '49,5',	 '10/11',	'J½',	'5'),
	((select id from category where url_name = 'women-jewelry-rings'), '16'	, '50,5',	'12',	'K½',	'5½'),
	((select id from category where url_name = 'women-jewelry-rings'), '16½'	, '52',	'13',	'L½',	'6'),
	((select id from category where url_name = 'women-jewelry-rings'), '17'	, '53,5',	'14/15',	'M½',	'6½'),
	((select id from category where url_name = 'women-jewelry-rings'), '17¼'	, '54,5',	'15/16',	'N½',	'7'),
	((select id from category where url_name = 'women-jewelry-rings'), '17¾'	, '55,5',	'17',	'O½',	'7½'),
	((select id from category where url_name = 'women-jewelry-rings'), '18'	, '57',	'18',	'P½',	'8'),
	((select id from category where url_name = 'women-jewelry-rings'), '18½'	, '58,5',	'19',	'Q½',	'8½'),
	((select id from category where url_name = 'women-jewelry-rings'), '19'	, '60',	'20',	'R½', '');

insert into size (category_id, international, european, inches, centimeters) values
	((select id from category where url_name = 'women-accessories-gloves'), 'XXS','6',	'6',	 '15,2'),
	((select id from category where url_name = 'women-accessories-gloves'), 'XS',	'6,5','6,5',	'16,5'),
	((select id from category where url_name = 'women-accessories-gloves'), 'S',	'7',	'7',	 '17,8'),
	((select id from category where url_name = 'women-accessories-gloves'), 'M',	'7,5','7,5',	'19'),
	((select id from category where url_name = 'women-accessories-gloves'), 'L',	'8',	'8',	 '20,3'),
	((select id from category where url_name = 'women-accessories-gloves'), 'XL',	'8,5','8,5',	'21,6'),
	((select id from category where url_name = 'women-accessories-gloves'), 'XXL','9',	'9',	 '22,9'),
  ((select id from category where url_name = 'women-accessories-gloves'), 'XXXL','9,5','9,5', '24)');

insert into size (category_id, international, russian, italian, english, american) values
	((select id from category where url_name = 'women-accessories-belts'), 'XXS',	'60-65',	'36',	'4',	'0'),
	((select id from category where url_name = 'women-accessories-belts'), 'XS',	'70-75',	'38',	'6',	'2'),
	((select id from category where url_name = 'women-accessories-belts'), 'S',	'80-85',	'40',	'8',	'4'),
	((select id from category where url_name = 'women-accessories-belts'), 'M',	'90-95',	'42',	'10',	'6'),
	((select id from category where url_name = 'women-accessories-belts'), 'L',	'100',	'44',	'12',	'8'),
	((select id from category where url_name = 'women-accessories-belts'), 'L',	'105',	'46',	'14',	'10'),
	((select id from category where url_name = 'women-accessories-belts'), 'XL',	'105',	'46',	'14',	'10'),
	((select id from category where url_name = 'women-accessories-belts'), 'XXL',	'110',	'48',	'16',	'12');

insert into size (category_id, international, russian, european, american, english) values
	((select id from category where url_name = 'women-accessories-headdresses'), 'XS',	'52-53','52-53',	'6⅜-6½',	'6½-6⅝'),
	((select id from category where url_name = 'women-accessories-headdresses'), 'S',	'54-55','54-55',	'6⅝-6¾',	'6¾-6⅞'),
	((select id from category where url_name = 'women-accessories-headdresses'), 'M',	'56-57','56-57',	'6⅞-7',	'7-7⅛'),
	((select id from category where url_name = 'women-accessories-headdresses'), 'L',	'58-59','58-59',	'7⅛-7¼',	'7¼-7⅜'),
	((select id from category where url_name = 'women-accessories-headdresses'), 'XL',	'60-61','60-61',	'7⅜-7½',	'7½-7⅝');

insert into size (category_id, 	international, russian, french, italian, breast_english_american, waist_english_american, danish) values
	((select id from category where url_name = 'man-clothes'),'XXS',	'42',	'42',	'42',	'32',	'26',	'42'),
	((select id from category where url_name = 'man-clothes'),'XS',	'44',		'44',	'44',	'34',	'28',	'44'),
	((select id from category where url_name = 'man-clothes'),'S',	'46',		'46',	'46',	'36',	'30',	'46'),
	((select id from category where url_name = 'man-clothes'),'M',	'48',		'48',	'48',	'38',	'32',	'48'),
	((select id from category where url_name = 'man-clothes'),'L',	'50',		'50',	'50',	'40',	'34',	'50'),
	((select id from category where url_name = 'man-clothes'),'XL',	'52',		'52',	'52',	'42',	'36',	'52'),
	((select id from category where url_name = 'man-clothes'),'XXL',	'54',	'54',	'54',	'44',	'38',	'54'),
	((select id from category where url_name = 'man-clothes'),'XXXL',	'56',	'56',	'56',	'46',	'40',	'56');

insert into size (category_id, 	international, european, russian, english, american) values
	((select id from category where url_name = 'man-footwear'), '39',   '39',     '39',    '5',     '6'),
	((select id from category where url_name = 'man-footwear'), '39,5', '39,5',   '39,5',  '5,5',   '6,5'),
	((select id from category where url_name = 'man-footwear'), '40',   '40',     '40',    '6',     '7'),
	((select id from category where url_name = 'man-footwear'), '40,5', '40,5',   '40,5',  '6,5',   '7,5'),
	((select id from category where url_name = 'man-footwear'), '41',   '41',     '41',    '7',     '8'),
	((select id from category where url_name = 'man-footwear'), '41,5', '41,5',   '41,5',  '7,5',   '8,5'),
	((select id from category where url_name = 'man-footwear'), '42',   '42',     '42',    '8',     '9'),
	((select id from category where url_name = 'man-footwear'), '42,5', '42,5',   '42,5',  '8,5',   '9,5'),
	((select id from category where url_name = 'man-footwear'), '43',   '43',     '43',    '9',     '10'),
	((select id from category where url_name = 'man-footwear'), '43,5', '43,5',   '43,5',  '9,5',   '10,5'),
	((select id from category where url_name = 'man-footwear'), '44',   '44',     '44',    '10',    '11'),
	((select id from category where url_name = 'man-footwear'), '44,5', '44,5',   '44,5',  '10,5',  '11,5'),
	((select id from category where url_name = 'man-footwear'), '45',   '45',     '45',    '11',    '12'),
	((select id from category where url_name = 'man-footwear'), '45,5', '45,5',   '45,5',  '11,5',  '12,5'),
	((select id from category where url_name = 'man-footwear'), '46',   '46',     '46',    '12',    '13'),
	((select id from category where url_name = 'man-footwear'), '46,5', '46,5',   '46,5',  '12,5',  '13,5'),
	((select id from category where url_name = 'man-footwear'), '47',   '47',     '47',    '13',    '14'),
	((select id from category where url_name = 'man-footwear'), '47,5', '47,5',   '47,5',  '13,5',  '14,5'),
	((select id from category where url_name = 'man-footwear'), '48',   '48',     '48',    '14',    '15');

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


insert into size (category_id, international, european, american, english) VALUES
	((select id from category where url_name = 'man-accessories-hats'), 'XS',	'52-53',	'6½-6⅝',	'6⅜-6½'),
	((select id from category where url_name = 'man-accessories-hats'), 'S',	'54-55',	'6¾-6⅞',	'6⅝-6¾'),
	((select id from category where url_name = 'man-accessories-hats'), 'M',	'56-57',	'7-7⅛',	'6⅞-7'),
	((select id from category where url_name = 'man-accessories-hats'), 'L',	'58-59',	'7¼-7⅜',	'7⅛-7¼'),
	((select id from category where url_name = 'man-accessories-hats'), 'XL',	'60-61',	'7½-7⅝',	'7⅜-7½'),
	((select id from category where url_name = 'man-accessories-hats'), 'XXL',	'62-63',	'7¾-7⅞',	'7⅝-7¾');
