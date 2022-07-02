INSERT INTO attribute (id, name) VALUES
(1, 'Материал сумок'),
(2, 'Материал обуви'),
(3, 'Материал одежды'),
(4, 'Материал очков'),
(5, 'Материал украшений'),
(6, 'Материал детской одежды'),
(7, 'Материал часов'),
(8, 'Материал текстиля'),
(9, 'Материал посуды'),
(10, 'Цвет'),
(11, 'Браслет часов'),
(12, 'Механизм'),
(13, 'Посадка'),
(14, 'Материал аксессуаров'),
(15, 'Материал предметов декора');
ALTER SEQUENCE attribute_id_seq RESTART WITH 16;

INSERT INTO attribute_value (id, attribute_id, value) VALUES
  (1, 1, 'Бархат'),
  (2, 1, 'Гобелен'),
  (3, 1, 'Деним'),
  (4, 1, 'Джакард'),
  (5, 1, 'Замша'),
  (6, 1, 'Искусственная кожа'),
  (7, 1, 'Кашемир'),
  (8, 1, 'Кожа'),
  (9, 1, 'Лакированная кожа'),
  (10, 1, 'Лен'),
  (11, 1, 'Мех'),
  (12, 1, 'Плетенье'),
  (13, 1, 'Полиэстер'),
  (14, 1, 'Синтетика'),
  (15, 1, 'Твид'),
  (16, 1, 'Твил'),
  (17, 1, 'Ткань'),
  (18, 1, 'Хлопок'),
  (19, 1, 'Шелк'),
  (20, 1, 'Шерсть'),
  (21, 1, 'Экзотическая кожа'),
  (22, 1, 'Экокожа'),
  (23, 1, 'Другое'),

  (24, 2, 'Бархат'),
  (25, 2, 'Замша'),
  (26, 2, 'Кожа'),
  (27, 2, 'Лакированная кожа'),
  (28, 2, 'Нубук'),
  (29, 2, 'Пластик'),
  (30, 2, 'Полиэстер'),
  (31, 2, 'Резина'),
  (32, 2, 'Ткань'),
  (33, 2, 'Экзотическая кожа'),
  (34, 2, 'Экокожа'),
  (35, 2, 'Другое'),

  (36, 3, 'Атлас'),
  (37, 3, 'Бархат'),
  (38, 3, 'Вискоза'),
  (39, 3, 'Гобелен'),
  (40, 3, 'Губка (сетка)'),
  (41, 3, 'Деним-джинс'),
  (42, 3, 'Замша'),
  (43, 3, 'Кашемир'),
  (44, 3, 'Кожа'),
  (45, 3, 'Лен'),
  (46, 3, 'Мех'),
  (47, 3, 'Полиэстер'),
  (48, 3, 'Синтетика'),
  (49, 3, 'Твид'),
  (50, 3, 'Хлопок'),
  (51, 3, 'Хлопок-эластан'),
  (52, 3, 'Шелк'),
  (53, 3, 'Шерсть'),
  (54, 3, 'Шифон'),
  (55, 3, 'Экзотическая кожа'),
  (56, 3, 'Другое'),

  (57, 4, 'Дерево'),
  (58, 4, 'Металл'),
  (59, 4, 'Пластик'),
  (60, 4, 'Другое'),

  (61, 5, 'Белое золото'),
  (62, 5, 'Бисер'),
  (63, 5, 'Дерево'),
  (64, 5, 'Желтое золото'),
  (65, 5, 'Жемчуг'),
  (66, 5, 'Керамика'),
  (67, 5, 'Кожа'),
  (68, 5, 'Кристалл'),
  (69, 5, 'Металл'),
  (70, 5, 'Нефрит'),
  (71, 5, 'Пластик'),
  (72, 5, 'Платина'),
  (73, 5, 'Позолота'),
  (74, 5, 'Розовое золото'),
  (75, 5, 'Рубин'),
  (76, 5, 'Серебряное покрытие'),
  (77, 5, 'Серебро'),
  (78, 5, 'Сталь'),
  (79, 5, 'Стекло'),
  (80, 5, 'Экзотическая кожа'),
  (81, 5, 'Другое'),

  (82, 6, 'Атлас'),
  (83, 6, 'Бархат'),
  (84, 6, 'Вискоза'),
  (85, 6, 'Деним-джинс'),
  (86, 6, 'Замша'),
  (87, 6, 'Кашемир'),
  (88, 6, 'Кожа'),
  (89, 6, 'Лен'),
  (90, 6, 'Мех'),
  (91, 6, 'Полиэстер'),
  (92, 6, 'Синтетика'),
  (93, 6, 'Хлопок'),
  (94, 6, 'Шелк'),
  (95, 6, 'Шерсть'),
  (96, 6, 'Другое'),

  (97, 7, 'Белое золото'),
  (98, 7, 'Желтое золото'),
  (99, 7, 'Золото и сталь'),
  (100, 7, 'Каучук'),
  (101, 7, 'Керамика'),
  (102, 7, 'Платина'),
  (103, 7, 'Позолота'),
  (104, 7, 'Розовое золото'),
  (105, 7, 'Рубин'),
  (106, 7, 'Серебро'),
  (107, 7, 'Сталь'),
  (108, 7, 'Титан'),
  (109, 7, 'Другое'),

  (110, 8, 'Вискоза'),
  (111, 8, 'Гобелен'),
  (112, 8, 'Жаккард'),
  (113, 8, 'Кружево'),
  (114, 8, 'Сатин'),
  (115, 8, 'Твид'),
  (116, 8, 'Хлопок'),
  (117, 8, 'Шелк'),
  (118, 8, 'Шерсть'),
  (119, 8, 'Шифон'),
  (120, 8, 'Другое'),

  (121, 9, 'Дерево'),
  (122, 9, 'Металл'),
  (123, 9, 'Пластик'),
  (124, 9, 'Сталь'),
  (125, 9, 'Стекло'),
  (126, 9, 'Фарфор'),
  (127, 9, 'Другое'),

  (128, 10, 'Антрацит'),
  (129, 10, 'Бежевый'),
  (130, 10, 'Бесцветный'),
  (131, 10, 'Белый'),
  (132, 10, 'Бирюзовый'),
  (133, 10, 'Болотный'),
  (134, 10, 'Бордовый'),
  (135, 10, 'Бургунди'),
  (136, 10, 'Голубой'),
  (137, 10, 'Горчичный'),
  (138, 10, 'Желтый'),
  (139, 10, 'Зеленый'),
  (140, 10, 'Золотой'),
  (141, 10, 'Коралловый'),
  (142, 10, 'Коричневый'),
  (143, 10, 'Красный'),
  (144, 10, 'Металлический'),
  (145, 10, 'Молочный'),
  (146, 10, 'Мульти'),
  (147, 10, 'Оранжевый'),
  (148, 10, 'Персиковый'),
  (149, 10, 'Пудровый'),
  (150, 10, 'Пурпурный'),
  (151, 10, 'Розовый'),
  (152, 10, 'Рыжий'),
  (153, 10, 'Салатовый'),
  (154, 10, 'Серебряный'),
  (155, 10, 'Серый'),
  (156, 10, 'Синий'),
  (157, 10, 'Сиреневый'),
  (158, 10, 'Телесный'),
  (159, 10, 'Темно-синий'),
  (160, 10, 'Фиалковый'),
  (161, 10, 'Фиолетовый'),
  (162, 10, 'Фуксия'),
  (163, 10, 'Хаки'),
  (164, 10, 'Черный'),
  (165, 10, 'Другое'),

  (166, 11, 'Белое золото'),
  (167, 11, 'Желтое золото'),
  (168, 11, 'Золото и сталь'),
  (169, 11, 'Каучук'),
  (170, 11, 'Керамика'),
  (171, 11, 'Кожа'),
  (172, 11, 'Платина'),
  (173, 11, 'Позолота'),
  (174, 11, 'Розовое золото'),
  (175, 11, 'Рубин'),
  (176, 11, 'Серебро'),
  (177, 11, 'Сталь'),
  (178, 11, 'Титан'),
  (179, 11, 'Ткань'),
  (180, 11, 'Экзотическая кожа'),
  (181, 11, 'Другое'),

  (182, 12, 'Автоматические'),
  (183, 12, 'Кварцевые'),
  (184, 12, 'Механические'),
  (185, 12, 'Электронные'),

  (186, 13, 'Низкая'),
  (187, 13, 'Высокая'),

  (188, 14, 'Бисер'),
  (189, 14, 'Дерево'),
  (190, 14, 'Жемчуг'),
  (191, 14, 'Замша'),
  (192, 14, 'Карбон'),
  (193, 14, 'Керамика'),
  (194, 14, 'Кожа'),
  (195, 14, 'Кристалл'),
  (196, 14, 'Металл'),
  (197, 14, 'Нефрит'),
  (198, 14, 'Пластик'),
  (199, 14, 'Позолота'),
  (200, 14, 'Розовое золото'),
  (201, 14, 'Рубин'),
  (202, 14, 'Сталь'),
  (203, 14, 'Стекло'),
  (204, 14, 'Ткань'),
  (205, 14, 'Шелк'),
  (206, 14, 'Шерсть'),
  (207, 14, 'Экзотическая кожа'),
  (208, 14, 'Другое'),

  (209, 15, 'Дерево'),
  (210, 15, 'Карбон'),
  (211, 15, 'Кожа'),
  (212, 15, 'Металл'),
  (213, 15, 'Парафин'),
  (214, 15, 'Пластик'),
  (215, 15, 'Сталь'),
  (216, 15, 'Стекло'),
  (217, 15, 'Ткань'),
  (218, 15, 'Фарфор'),
  (219, 15, 'Другое');

INSERT INTO category (id, url_name, display_name, left_order, right_order, parent_id, singular_name) VALUES (4, 'woman-bags-backpacks', 'Рюкзаки', 4, 5, 3, 'Рюкзак'),
  (5, 'woman-bags-clutchbags', 'Клатчи', 6, 7, 3, 'Клатч'),
  (6, 'woman-bags-bags', 'Сумки', 8, 9, 3, 'Сумка'),
  (7, 'woman-bags-travelbags', 'Дорожные сумки', 10, 11, 3, 'Дорожная сумка'),
  (8, 'woman-bags-cosmeticbags', 'Косметички', 12, 13, 3, 'Косметичка'),
  (3, 'woman-bags', 'Сумки', 3, 16, 2, 'Сумка'),
  (9, 'woman-bags-beachbags', 'Пляжные сумки', 14, 15, 3, 'Пляжная сумка'),
  (11, 'woman-shoes-ankleboots', 'Ботильоны', 18, 19, 10, 'Ботильоны'),
  (12, 'woman-shoes-boots', 'Ботинки', 20, 21, 10, 'Ботинки'),
  (13, 'woman-shoes-brogues', 'Брогги и оксфорды', 22, 23, 10, 'Брогги / Оксфорды'),
  (14, 'woman-shoes-balletflats', 'Балетки', 24, 25, 10, 'Балетки'),
  (15, 'woman-shoes-loafers', 'Лоферы и слиперы', 26, 27, 10, 'Лоферы / слиперы'),
  (16, 'woman-shoes-highboots', 'Сапоги', 28, 29, 10, 'Сапоги'),
  (17, 'woman-shoes-jackboots', 'Ботфорты', 30, 31, 10, 'Ботфорты'),
  (18, 'woman-shoes-moccasins', 'Мокасины', 32, 33, 10, 'Мокасины'),
  (19, 'woman-shoes-espadrilles', 'Эспадрильи', 34, 35, 10, 'Эспадрильи'),
  (20, 'woman-shoes-sliponshoes', 'Слипоны', 36, 37, 10, 'Слипоны'),
  (22, 'high-heeled-shoes', 'Туфли на высоком каблуке', 39, 40, 21, 'Туфли на высоком каблуке'),
  (23, 'average-heeled-shoes', 'Туфли на среднем каблуке', 41, 42, 21, 'Туфли на среднем каблуке'),
  (21, 'shoes', 'Туфли', 38, 45, 10, 'Туфли'),
  (24, 'low-heeled-shoes', 'Туфли на низком каблуке', 43, 44, 21, 'Туфли на низком каблуке'),
  (26, 'high-heeled-wedding-shoes', 'Свадебные туфли на высоком каблуке', 47, 48, 25, 'Свадебные туфли на высоком каблуке'),
  (27, 'average-heeled-wedding-shoes', 'Свадебные туфли на среднем каблуке', 49, 50, 25, 'Свадебные туфли на среднем каблуке'),
  (25, 'wedding-shoes', 'Свадебные туфли', 46, 53, 10, 'Свадебные туфли'),
  (28, 'low-heeled-wedding-shoes', 'Свадебные туфли на низком каблуке', 51, 52, 25, 'Свадебные туфли на низком каблуке'),
  (30, 'high-heeled-sandals', 'Босоножки на высоком каблуке', 55, 56, 29, 'Босоножки на высоком каблуке'),
  (29, 'sandals', 'Босоножки', 54, 59, 10, 'Босоножки'),
  (31, 'low-heeled-sandals', 'Босоножки на низком каблуке', 57, 58, 29, 'Босоножки на низком каблуке'),
  (32, 'woman-shoes-mules', 'Мюли', 60, 61, 10, 'Мюли'),
  (33, 'woman-shoes-sandals', 'Сандалии', 62, 63, 10, 'Сандалии'),
  (34, 'woman-shoes-flipflops', 'Шлепанцы', 64, 65, 10, 'Шлепанцы'),
  (10, 'woman-shoes', 'Обувь', 17, 68, 2, 'Обувь'),
  (35, 'woman-shoes-sneakers', 'Кроссовки', 66, 67, 10, 'Кроссовки'),
  (38, 'woman-outwear-coats', 'Пальто', 71, 72, 37, 'Пальто'),
  (39, 'woman-outwear-jackets', 'Куртки', 73, 74, 37, 'Куртка'),
  (40, 'woman-outwear-cloaks', 'Тренчи и плащи', 75, 76, 37, 'Тренч / Плащ'),
  (41, 'woman-outwear-parkajackets', 'Парки', 77, 78, 37, 'Парка'),
  (42, 'woman-outwear-sneakers', 'Пуховики', 79, 80, 37, 'Пуховик'),
  (43, 'woman-outwear-furcoats', 'Шубы', 81, 82, 37, 'Шуба'),
  (44, 'woman-outwear-vests', 'Жилеты', 83, 84, 37, 'Жилет'),
  (45, 'woman-outwear-capes', 'Накидки и пончо', 85, 86, 37, 'Накидка / Пончо'),
  (37, 'woman-outwear', 'Верхняя одежда', 70, 89, 36, 'Верхняя одежда'),
  (46, 'woman-outwear-other', 'Другое', 87, 88, 37, 'Другое'),
  (48, 'dresses-maxi', 'Платья макси', 91, 92, 47, 'Платье макси'),
  (49, 'dresses-midi', 'Платья миди', 93, 94, 47, 'Платье миди'),
  (50, 'dresses-mini', 'Платья мини', 95, 96, 47, 'Платье мини'),
  (51, 'evening-dresses', 'Вечерние платья', 97, 98, 47, 'Вечернее платье'),
  (52, 'cocktail-dresses', 'Коктейльные платья', 99, 100, 47, 'Коктейльное платье'),
  (53, 'wedding-dresses', 'Свадебные платья', 101, 102, 47, 'Свадебное платье'),
  (47, 'dresses', 'Платья', 90, 105, 36, 'Платье'),
  (54, 'other-dresses', 'Другие платья', 103, 104, 47, 'Платье'),
  (55, 'woman-clothes-blazers', 'Жакеты и пиджаки', 106, 107, 36, 'Жакет / Пиджак'),
  (57, 'flared-jeans', 'Расклешенные джинсы', 109, 110, 56, 'Джинсы клеш'),
  (58, 'skinny-jeans', 'Зауженные джинсы', 111, 112, 56, 'Джинсы скинни'),
  (56, 'woman-clothes-jeans', 'Джинсы', 108, 115, 36, 'Джинсы'),
  (59, 'straight-jeans', 'Прямые джинсы', 113, 114, 56, 'Прямые джинсы'),
  (60, 'woman-clothes-jumpsuits', 'Комбинезон', 116, 117, 36, 'Комбинезон'),
  (62, 'cardigans', 'Кардиганы', 119, 120, 61, 'Кардиган'),
  (63, 'jumpers', 'Джемперы и свитеры', 121, 122, 61, 'Джемпер / свитер'),
  (61, 'woman-clothes-knitwear', 'Трикотаж', 118, 125, 36, 'Трикотаж'),
  (64, 'sweatshirts', 'Свитшоты', 123, 124, 61, 'Свитшот'),
  (65, 'woman-clothes-shorts', 'Шорты', 126, 127, 36, 'Шорты'),
  (67, 'maxi-skirts', 'Юбки макси', 129, 130, 66, 'Юбка макси'),
  (68, 'midi-skirts', 'Юбки миди', 131, 132, 66, 'Юбка миди'),
  (66, 'woman-clothes-skirts', 'Юбки', 128, 135, 36, 'Юбка'),
  (69, 'mini-skirts', 'Юбки мини', 133, 134, 66, 'Юбка мини'),
  (71, 'sleeveless-tops', 'Топы без рукавов', 137, 138, 70, 'Топ без рукавов'),
  (72, 'long-sleeve-tops', 'Топы с длинным рукавом', 139, 140, 70, 'Топ с длинным рукавом'),
  (70, 'woman-clothes-tops', 'Топы', 136, 143, 36, 'Топ'),
  (73, 'short-sleeve-tops', 'Топы с коротким  рукавом', 141, 142, 70, 'Топ с коротким рукавом'),
  (74, 'woman-clothes-shirts', 'Рубашки и блузки', 144, 145, 36, 'Рубашка / Блузка'),
  (75, 'woman-clothes-suits', 'Костюмы', 146, 147, 36, 'Костюм'),
  (77, 'flared-trousers', 'Брюки клеш', 149, 150, 76, 'Брюки клеш'),
  (78, 'skinny-trousers', 'Брюки скинни', 151, 152, 76, 'Брюки скинни'),
  (76, 'woman-clothes-trousers', 'Брюки', 148, 155, 36, 'Брюки'),
  (79, 'straight-trousers', 'Прямые брюки', 153, 154, 76, 'Прямые брюки'),
  (80, 'woman-clothes-pajamas', 'Пижамы', 156, 157, 36, 'Пижама'),
  (81, 'woman-sportswear', 'Спортивная одежда', 158, 159, 36, 'Спортивная одежда'),
  (83, 'woman-beachwear-swimsuits', 'Купальники', 161, 162, 82, 'Купальник'),
  (36, 'woman-clothes', 'Одежда', 69, 166, 2, 'Одежда'),
  (82, 'woman-beachwear', 'Пляжная одежда', 160, 165, 36, 'Пляжная одежда'),
  (84, 'woman-beachwear-tunics', 'Туники', 163, 164, 82, 'Туника'),
  (86, 'woman-accessories-belts', 'Ремни', 168, 169, 85, 'Ремень'),
  (87, 'woman-accessories-gloves', 'Перчатки', 170, 171, 85, 'Перчатки'),
  (89, 'hats', 'Шляпы', 173, 174, 88, 'Шляпа'),
  (90, 'caps', 'Кепки', 175, 176, 88, 'Кепка'),
  (88, 'woman-accessories-hats', 'Головные уборы', 172, 179, 85, 'Головной убор'),
  (91, 'hats', 'Шапки', 177, 178, 88, 'Шапка'),
  (93, 'woman-jewellery-bracelets', 'Браслеты', 181, 182, 92, 'Браслет'),
  (94, 'woman-jewellery-earrings', 'Серьги', 183, 184, 92, 'Серьги'),
  (95, 'woman-jewellery-hairaccessories', 'Украшения для волос', 185, 186, 92, 'Украшение для волос'),
  (96, 'woman-jewellery-necklaces', 'Колье', 187, 188, 92, 'Колье'),
  (97, 'woman-jewellery-pins', 'Броши', 189, 190, 92, 'Брошь'),
  (98, 'woman-jewellery-rings', 'Кольца', 191, 192, 92, 'Кольцо'),
  (92, 'woman-jewellery', 'Украшения', 180, 195, 85, 'Украшения'),
  (99, 'woman-jewellery-other', 'Другое', 193, 194, 92, 'Другое'),
  (100, 'woman-accessories-wallets', 'Кошельки', 196, 197, 85, 'Кошелек'),
  (101, 'woman-accessories-cardholders', 'Визитницы', 198, 199, 85, 'Визитница'),
  (102, 'woman-accessories-scarfs', 'Шарфы и Платки', 200, 201, 85, 'Шарф / Платок'),
  (103, 'woman-accessories-sunglasses', 'Очки', 202, 203, 85, 'Очки'),
  (2, 'woman', 'Женское', 2, 207, 1, 'Женское'),
  (85, 'woman-accessories', 'Аксессуары', 167, 206, 2, 'Аксессуары'),
  (104, 'woman-accessories-watches', 'Часы', 204, 205, 85, 'Часы'),
  (108, 'man-outwear-coats', 'Пальто', 211, 212, 107, 'Пальто'),
  (109, 'man-outwear-jackets', 'Куртки', 213, 214, 107, 'Куртка'),
  (110, 'man-outwear-cloaks', 'Тренчи и Плащи', 215, 216, 107, 'Тренч / Плащ'),
  (111, 'man-outwear-parka', 'Парки', 217, 218, 107, 'Парки'),
  (112, 'man-outwear-downjackets', 'Пуховики', 219, 220, 107, 'Пуховик'),
  (113, 'man-outwear-vests', 'Жилеты', 221, 222, 107, 'Жилет'),
  (107, 'man-outwear', 'Верхняя одежда', 210, 225, 106, 'Верхняя одежда'),
  (114, 'man-outwear-other', 'Другое', 223, 224, 107, 'Другое'),
  (116, 'casual-jackets', 'Пиджаки casual', 227, 228, 115, 'Пиджак casual'),
  (115, 'man-clothes-jackets', 'Пиджаки', 226, 231, 106, 'Пиджак'),
  (117, 'classic-jackets', 'Пиджаки classic', 229, 230, 115, 'Пиджак classic'),
  (119, 'skinny-jeans', 'Зауженные джинсы', 233, 234, 118, 'Джинсы скинни'),
  (120, 'straight-jeans', 'Прямые джинсы', 235, 236, 118, 'Прямые джинсы'),
  (118, 'man-clothes-jeans', 'Джинсы', 232, 239, 106, 'Джинсы'),
  (121, 'other-jeans', 'Другие джинсы', 237, 238, 118, 'Джинсы'),
  (123, 'man-clothes-pullovers-cardigans', 'Джемперы', 241, 242, 122, 'Джемпер'),
  (124, 'man-clothes-pullovers-sweatshirts', 'Свитшоты', 243, 244, 122, 'Свитшот'),
  (125, 'man-clothes-pullovers-vests', 'Жилетки', 245, 246, 122, 'Жилетка'),
  (122, 'man-clothes-pullovers', 'Свитеры и свитшоты', 240, 249, 106, 'Свитеры и свитшоты'),
  (126, 'man-clothes-pullovers-other', 'Другое', 247, 248, 122, 'Другое'),
  (128, 'long-sleeve-polo', 'Поло с длинным рукавом', 251, 252, 127, 'Поло с длинным рукавом'),
  (127, 'man-clothes-polo', 'Поло', 250, 255, 106, 'Поло'),
  (129, 'short-sleeve-polo', 'Поло с коротким рукавом', 253, 254, 127, 'Поло с коротким рукавом'),
  (131, 'casual-shirts', 'Рубашки casual', 257, 258, 130, 'Рубашка casual'),
  (130, 'man-clothes-shirts', 'Рубашки', 256, 261, 106, 'Рубашка'),
  (132, 'classic-shirts', 'Рубашки classic', 259, 260, 130, 'Рубашка classic'),
  (133, 'man-clothes-shorts', 'Шорты', 262, 263, 106, 'Шорты'),
  (135, 'single-breasted-suits', 'Однобортные костюмы', 265, 266, 134, 'Однобортный костюм'),
  (136, 'double-breasted-suits', 'Двубортные костюмы', 267, 268, 134, 'Двубортный костюм'),
  (137, 'three-piece-suits', 'Костюмы-тройки', 269, 270, 134, 'Костюм-тройка'),
  (134, 'man-clothes-suits', 'Костюмы', 264, 273, 106, 'Костюм'),
  (138, 'tuxedos', 'Смокинги', 271, 272, 134, 'Смокинг'),
  (140, 'long-sleeve-shirts', 'Футболки с длинным рукавом', 275, 276, 139, 'Футболка с длинным рукавом'),
  (139, 'man-clothes-tshirts', 'Футболки', 274, 279, 106, 'Футболка'),
  (141, 'short-sleeve-shirts', 'Футболки с коротким  рукавом', 277, 278, 139, 'Футболка с коротким рукавом'),
  (143, 'casual-trousers', 'Брюки casual', 281, 282, 142, 'Брюки casual'),
  (142, 'man-clothes-trousers', 'Брюки', 280, 285, 106, 'Брюки'),
  (144, 'classic-trousers', 'Брюки classic', 283, 284, 142, 'Брюки classic'),
  (145, 'man-clothes-pajamas', 'Пижамы', 286, 287, 106, 'Пижама'),
  (146, 'man-beachwear', 'Пляжная одежда', 288, 289, 106, 'Пляжная одежда'),
  (106, 'man-clothes', 'Одежда', 209, 292, 105, 'Одежда'),
  (147, 'man-sportswear', 'Спортивная одежда', 290, 291, 106, 'Спортивная одежда'),
  (150, 'tall-boots', 'Высокие ботинки', 295, 296, 149, 'Высокие ботинки'),
  (149, 'man-shoes-boots', 'Ботинки', 294, 299, 148, 'Ботинки'),
  (151, 'short-boots', 'Низкие ботинки', 297, 298, 149, 'Низкие ботинки'),
  (153, 'tall-sneakers', 'Высокие кроссовки / кеды', 301, 302, 152, 'Высокие кроссовки / кеды'),
  (152, 'man-shoes-sneakers', 'Кроссовки и кеды', 300, 305, 148, 'Кроссовки и кеды'),
  (154, 'short-sneakers', 'Низкие кроссовки / кеды', 303, 304, 152, 'Низкие кроссовки / кеды'),
  (155, 'man-shoes-moccasins', 'Лоферы и мокасины', 306, 307, 148, 'Лоферы / Мокасины'),
  (156, 'man-shoes-sandals', 'Сандалии', 308, 309, 148, 'Сандалии'),
  (157, 'man-shoes-slipon', 'Слипоны', 310, 311, 148, 'Слипоны'),
  (148, 'man-shoes', 'Обувь', 293, 314, 105, 'Обувь'),
  (158, 'man-shoes-shoes', 'Туфли', 312, 313, 148, 'Туфли'),
  (160, 'man-bags-backpacks', 'Рюкзаки', 316, 317, 159, 'Рюкзак'),
  (161, 'man-bags-travelbags', 'Дорожные сумки', 318, 319, 159, 'Дорожная сумка'),
  (162, 'man-bags-briefcases', 'Портфели', 320, 321, 159, 'Портфель'),
  (163, 'man-bags-sportsbags', 'Спортивные сумки', 322, 323, 159, 'Спортивная сумка'),
  (164, 'man-bags-purses', 'Барсетки', 324, 325, 159, 'Барсетка'),
  (165, 'man-bags-cases', 'Для документов', 326, 327, 159, 'Сумка для документов'),
  (159, 'man-bags', 'Мужские сумки', 315, 330, 105, 'Мужская сумка'),
  (166, 'man-bags-shoulderbags', 'Сумки на плечо', 328, 329, 159, 'Сумка на плечо'),
  (168, 'man-accessories-belts', 'Ремни', 332, 333, 167, 'Ремень'),
  (169, 'man-accessories-wallets', 'Кошельки', 334, 335, 167, 'Кошелек'),
  (170, 'man-accessories-cardholders', 'Визитницы', 336, 337, 167, 'Визитница'),
  (171, 'man-accessories-cufflinks', 'Запонки', 338, 339, 167, 'Запонки'),
  (172, 'man-accessories-gloves', 'Перчатки', 340, 341, 167, 'Перчатки'),
  (174, 'caps', 'Кепки', 343, 344, 173, 'Кепка'),
  (173, 'man-accessories-hats', 'Головные уборы', 342, 347, 167, 'Головной убор'),
  (175, 'hats', 'Шляпы', 345, 346, 173, 'Шляпа'),
  (177, 'bracelets', 'Браслеты', 349, 350, 176, 'Браслет'),
  (178, 'man-accessories-jewellery-rings', 'Перстни', 351, 352, 176, 'Перстень'),
  (176, 'man-accessories-jewellery', 'Украшения', 348, 355, 167, 'Украшения'),
  (179, 'other-accessories', 'Другое', 353, 354, 176, 'Украшение'),
  (180, 'man-accessories-scarfs', 'Шарфы и платки', 356, 357, 167, 'Шарф / Платок'),
  (181, 'man-accessories-sunglasses', 'Очки', 358, 359, 167, 'Очки'),
  (182, 'man-accessories-ties', 'Галстуки и бабочки', 360, 361, 167, 'Галстук / Бабочка'),
  (183, 'man-accessories-watches', 'Часы', 362, 363, 167, 'Часы'),
  (185, 'dip-pen', 'Перьевые ручки', 365, 366, 184, 'Перьевая ручка'),
  (186, 'ink-pen', 'Чернильные ручки', 367, 368, 184, 'Чернильная ручка'),
  (105, 'man', 'Мужское', 208, 373, 1, 'Мужское'),
  (167, 'man-accessories', 'Аксессуары', 331, 372, 105, 'Аксессуар'),
  (184, 'man-accessories-pens', 'Ручки', 364, 371, 167, 'Ручка'),
  (187, 'ball-pen', 'Шариковые ручки', 369, 370, 184, 'Шариковая ручка'),
  (190, 'girls-babies-pajamas', 'Белье и пижамы', 376, 377, 189, 'Белье / пижама'),
  (191, 'girls-babies-bodysuits', 'Боди и песочники', 378, 379, 189, 'Боди / песочник'),
  (192, 'girls-babies-outwear', 'Верхняя одежда', 380, 381, 189, 'Верхняя одежда'),
  (193, 'girls-babies-jackets', 'Жакеты и жилеты', 382, 383, 189, 'Жакет / жилет'),
  (194, 'girls-babies-dresses', 'Платья', 384, 385, 189, 'Платье'),
  (195, 'girls-babies-shirts', 'Блузки', 386, 387, 189, 'Блузка'),
  (196, 'girls-babies-tshirts', 'Футболки и топы', 388, 389, 189, 'Футболка / топ'),
  (197, 'girls-babies-knitwear', 'Трикотаж', 390, 391, 189, 'Трикотаж'),
  (198, 'girls-babies-jeans', 'Джинсы', 392, 393, 189, 'Джинсы'),
  (199, 'girls-babies-trousers', 'Брюки', 394, 395, 189, 'Брюки'),
  (200, 'girls-babies-skirts', 'Юбки и шорты', 396, 397, 189, 'Юбка / шорты'),
  (201, 'girls-babies-sets', 'Комплекты', 398, 399, 189, 'Комплект'),
  (202, 'girls-babies-jumpsuits', 'Комбинезоны', 400, 401, 189, 'Комбинезон'),
  (203, 'girls-babies-beachwear', 'Пляжная одежда', 402, 403, 189, 'Пляжная одежда'),
  (204, 'girls-babies-shoes', 'Обувь', 404, 405, 189, 'Обувь'),
  (189, 'girls-babies', 'Девочки 0-3', 375, 408, 188, 'Девочки 0-3'),
  (205, 'girls-babies-accessories', 'Аксессуары', 406, 407, 189, 'Аксессуар'),
  (207, 'boys-babies-pajamas', 'Белье и пижамы', 410, 411, 206, 'Белье / пижама'),
  (208, 'boys-babies-bodysuits', 'Боди и песочники', 412, 413, 206, 'Боди / песочник'),
  (209, 'boys-babies-outwear', 'Верхняя одежда', 414, 415, 206, 'Верхняя одежда'),
  (210, 'boys-babies-jackets', 'Жакеты и жилеты', 416, 417, 206, 'Жакет / жилет'),
  (211, 'boys-babies-shirts', 'Рубашки', 418, 419, 206, 'Рубашка'),
  (212, 'boys-babies-tshirts', 'Футболки', 420, 421, 206, 'Футболка'),
  (213, 'boys-babies-knitwear', 'Трикотаж', 422, 423, 206, 'Трикотаж'),
  (214, 'boys-babies-jeans', 'Джинсы', 424, 425, 206, 'Джинсы'),
  (215, 'boys-babies-trousers', 'Брюки', 426, 427, 206, 'Брюки'),
  (216, 'boys-babies-shorts', 'Шорты', 428, 429, 206, 'Шорты'),
  (217, 'boys-babies-sets', 'Комплекты', 430, 431, 206, 'Комплект'),
  (218, 'boys-babies-jumpsuits', 'Комбинезоны', 432, 433, 206, 'Комбинезон'),
  (219, 'boys-babies-beachwear', 'Пляжная одежда', 434, 435, 206, 'Пляжная одежда'),
  (220, 'boys-babies-shoes', 'Обувь', 436, 437, 206, 'Обувь'),
  (206, 'boys-babies', 'Мальчики 0-3', 409, 440, 188, 'Мальчики 0-3'),
  (221, 'boys-babies-accessories', 'Аксессуары', 438, 439, 206, 'Аксессуар'),
  (223, 'girls-teen-pajamas', 'Пижамы', 442, 443, 222, 'Пижама'),
  (224, 'girls-teen-outwear', 'Верхняя одежда', 444, 445, 222, 'Верхняя одежда'),
  (225, 'girls-teen-jackets', 'Жакеты и жилеты', 446, 447, 222, 'Жакет / жилет'),
  (226, 'girls-teen-dresses', 'Платья', 448, 449, 222, 'Платье'),
  (227, 'girls-teen-shirts', 'Блузки', 450, 451, 222, 'Блузка'),
  (228, 'girls-teen-tshirts', 'Футболки и топы', 452, 453, 222, 'Футболка / топ'),
  (229, 'girls-teen-knitwear', 'Трикотаж', 454, 455, 222, 'Трикотаж'),
  (230, 'girls-teen-jeans', 'Джинсы', 456, 457, 222, 'Джинсы'),
  (231, 'girls-teen-trousers', 'Брюки', 458, 459, 222, 'Брюки'),
  (232, 'girls-teen-skirts', 'Юбки и шорты', 460, 461, 222, 'Юбка / шорты'),
  (233, 'girls-teen-sets', 'Комплекты', 462, 463, 222, 'Комплект'),
  (234, 'girls-teen-jumpsuits', 'Комбинезоны', 464, 465, 222, 'Комбинезон'),
  (235, 'girls-teen-beachwear', 'Пляжная одежда', 466, 467, 222, 'Пляжная одежда'),
  (236, 'girls-teen-shoes', 'Обувь', 468, 469, 222, 'Обувь'),
  (222, 'girls-teen', 'Девочки 4-14', 441, 472, 188, 'Девочки 4-14'),
  (237, 'girls-teen-accessories', 'Аксессуары', 470, 471, 222, 'Аксессуар'),
  (239, 'boys-teen-pajamas', 'Пижамы', 474, 475, 238, 'Пижама'),
  (240, 'boys-teen-outwear', 'Верхняя одежда', 476, 477, 238, 'Верхняя одежда'),
  (241, 'boys-teen-jackets', 'Жакеты и жилеты', 478, 479, 238, 'Жакет / жилет'),
  (242, 'boys-teen-shirts', 'Рубашки', 480, 481, 238, 'Рубашка'),
  (243, 'boys-teen-tshirts', 'Футболки', 482, 483, 238, 'Футболка'),
  (244, 'boys-teen-knitwear', 'Трикотаж', 484, 485, 238, 'Трикотаж'),
  (245, 'boys-teen-jeans', 'Джинсы', 486, 487, 238, 'Джинсы'),
  (246, 'boys-teen-trousers', 'Брюки', 488, 489, 238, 'Брюки'),
  (247, 'boys-teen-shorts', 'Шорты', 490, 491, 238, 'Шорты'),
  (248, 'boys-teen-sets', 'Комплекты', 492, 493, 238, 'Комплект'),
  (249, 'boys-teen-jumpsuits', 'Комбинезоны', 494, 495, 238, 'Комбинезон'),
  (250, 'boys-teen-beachwear', 'Пляжная одежда', 496, 497, 238, 'Пляжная одежда'),
  (251, 'boys-teen-shoes', 'Обувь', 498, 499, 238, 'Обувь'),
  (188, 'kids', 'Детское', 374, 503, 1, 'Детское'),
  (238, 'boys-teen', 'Мальчики 4-14', 473, 502, 188, 'Мальчики 4-14'),
  (252, 'boys-teen-accessories', 'Аксессуары', 500, 501, 238, 'Аксессуар'),
  (256, 'lifestyle-decoration-home-ashtrays', 'Пепельницы', 507, 508, 255, 'Пепельница'),
  (257, 'lifestyle-decoration-home-tableware', 'Посуда', 509, 510, 255, 'Посуда'),
  (258, 'lifestyle-decoration-home-vases', 'Вазы', 511, 512, 255, 'Ваза'),
  (259, 'lifestyle-decoration-home-cutlery', 'Столовые приборы', 513, 514, 255, 'Столовые приборы'),
  (260, 'lifestyle-decoration-home-candles', 'Свечи', 515, 516, 255, 'Свечи'),
  (261, 'lifestyle-decoration-home-clocks', 'Часы', 517, 518, 255, 'Часы'),
  (262, 'lifestyle-decoration-home-chests', 'Сундуки', 519, 520, 255, 'Сундук'),
  (263, 'lifestyle-decoration-home-paintings', 'Картины', 521, 522, 255, 'Картина'),
  (264, 'lifestyle-decoration-home-books', 'Книги', 523, 524, 255, 'Книга'),
  (265, 'lifestyle-decoration-home-casket', 'Шкатулки', 525, 526, 255, 'Шкатулка'),
  (266, 'lifestyle-decoration-home-furniture', 'Мебель', 527, 528, 255, 'Мебель'),
  (267, 'lifestyle-decoration-home-statues', 'Статуэтки', 529, 530, 255, 'Статуэтка'),
  (268, 'lifestyle-decoration-home-chandeliers', 'Лампы и люстры', 531, 532, 255, 'Лампа / люстра'),
  (255, 'lifestyle-decoration-home', 'Домашний декор', 506, 535, 254, 'Домашний декор'),
  (269, 'lifestyle-decoration-home-other', 'Другое', 533, 534, 255, 'Другое'),
  (271, 'lifestyle-decoration-textiles-covers', 'Покрывала и пледы', 537, 538, 270, 'Покрывало / плед'),
  (272, 'lifestyle-decoration-textiles-pillows', 'Подушки', 539, 540, 270, 'Подушки'),
  (273, 'lifestyle-decoration-textiles-linens', 'Постельное белье', 541, 542, 270, 'Подушки'),
  (274, 'lifestyle-decoration-textiles-cloth', 'Ткани', 543, 544, 270, 'Ткани'),
  (275, 'lifestyle-decoration-textiles-carpets', 'Ковры', 545, 546, 270, 'Ковры'),
  (1, '', '', 1, 552, 1, ''),
  (253, 'lifestyle', 'Стиль жизни', 504, 551, 1, 'Стиль жизни'),
  (254, 'lifestyle-decoration', 'Дизайн и декор', 505, 550, 253, 'Дизайн и декор'),
  (270, 'lifestyle-decoration-textiles', 'Текстиль', 536, 549, 254, 'Текстиль'),
  (276, 'lifestyle-decoration-textiles-other', 'Другое', 547, 548, 270, 'Другое');
ALTER SEQUENCE category_id_seq RESTART WITH 277;

INSERT INTO category_attribute_binding (id, category_id, attribute_id) VALUES (1, 2, 10),
(2, 3, 1),
(3, 10, 2),
(4, 36, 3),
(5, 56, 13),
(6, 76, 13),
(7, 86, 14),
(8, 87, 14),
(9, 88, 14),
(10, 92, 5),
(11, 100, 14),
(12, 101, 14),
(13, 102, 14),
(14, 103, 4),
(15, 104, 7),
(16, 104, 11),
(17, 104, 12),
(18, 105, 10),
(19, 106, 3),
(20, 118, 13),
(21, 142, 13),
(22, 148, 2),
(23, 159, 1),
(24, 168, 14),
(25, 169, 14),
(26, 170, 14),
(27, 171, 14),
(28, 172, 14),
(29, 173, 14),
(30, 176, 5),
(31, 180, 14),
(32, 181, 4),
(33, 182, 14),
(34, 183, 7),
(35, 183, 11),
(36, 183, 12),
(37, 184, 14),
(38, 188, 10),
(39, 190, 6),
(40, 191, 6),
(41, 192, 6),
(42, 193, 6),
(43, 194, 6),
(44, 195, 6),
(45, 196, 6),
(46, 197, 6),
(47, 198, 6),
(48, 199, 6),
(49, 200, 6),
(50, 201, 6),
(51, 202, 6),
(52, 203, 6),
(53, 204, 2),
(54, 205, 14),
(55, 207, 6),
(56, 208, 6),
(57, 209, 6),
(58, 210, 6),
(59, 211, 6),
(60, 212, 6),
(61, 213, 6),
(62, 214, 6),
(63, 215, 6),
(64, 216, 6),
(65, 217, 6),
(66, 218, 6),
(67, 219, 6),
(68, 220, 2),
(69, 221, 14),
(70, 223, 6),
(71, 224, 6),
(72, 225, 6),
(73, 226, 6),
(74, 227, 6),
(75, 228, 6),
(76, 229, 6),
(77, 230, 6),
(78, 231, 6),
(79, 232, 6),
(80, 233, 6),
(81, 234, 6),
(82, 235, 6),
(83, 236, 2),
(84, 237, 14),
(85, 239, 6),
(86, 240, 6),
(87, 241, 6),
(88, 242, 6),
(89, 243, 6),
(90, 244, 6),
(91, 245, 6),
(92, 246, 6),
(93, 247, 6),
(94, 248, 6),
(95, 249, 6),
(96, 250, 6),
(97, 251, 2),
(98, 252, 14),
(99, 253, 10),
(100, 256, 9),
(101, 257, 9),
(102, 258, 9),
(103, 259, 9),
(104, 260, 15),
(105, 261, 7),
(106, 262, 15),
(107, 263, 15),
(108, 264, 15),
(109, 265, 15),
(110, 266, 15),
(111, 267, 15),
(112, 268, 15),
(113, 269, 15),
(114, 270, 8);
ALTER SEQUENCE category_attribute_binding_id_seq RESTART WITH 115;