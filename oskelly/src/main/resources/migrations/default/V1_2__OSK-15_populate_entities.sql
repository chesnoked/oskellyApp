INSERT INTO category (id, url_name, display_name, left_order, right_order, parent_id) VALUES
  (1, 'root', 'root', 1, 234, 1),

  (2, 'women', 'Женское', 2, 93, 1),

  (3, 'women-clothes', 'Одежда', 3, 32, 2),
  (4, 'women-clothes-tops', 'Топы', 4, 5, 3),
  (5, 'women-clothes-knitwear', 'Вязаные изделия', 6, 7, 3),
  (6, 'women-clothes-dresses', 'Платья', 8, 9, 3),
  (7, 'women-clothes-skirts', 'Юбки', 10, 11, 3),
  (8, 'women-clothes-trousers', 'Брюки', 12, 13, 3),
  (9, 'women-clothes-shorts', 'Шорты', 14, 15, 3),
  (10, 'women-clothes-overalls', 'Комбинезоны', 16, 17, 3),
  (11, 'women-clothes-jeans', 'Джинсы', 18, 19, 3),
  (12, 'women-clothes-jackets', 'Жакеты', 20, 21, 3),
  (13, 'women-clothes-coat', 'Пальто', 22, 23, 3),
  (14, 'women-clothes-leather-jackets', 'Кожаные куртки', 24, 25, 3),
  (15, 'women-clothes-trenches', 'Тренчи', 26, 27, 3),
  (16, 'women-clothes-underwear', 'Женское белье', 28, 29, 3),
  (17, 'women-clothes-swimsuits', 'Купальники', 30, 31, 3),

  (18, 'women-footwear', 'Обувь', 33, 52, 2),
  (19, 'women-footwear-boots', 'Ботинки', 34, 35, 18),
  (20, 'women-footwear-sneakers', 'Кросовки', 36, 37, 18),
  (21, 'women-footwear-ballet-shoes', 'Балетки', 38, 39, 18),
  (22, 'women-footwear-moccasins', 'Мокасины', 40, 41, 18),
  (23, 'women-footwear-sandals', 'Сандалии', 42, 43, 18),
  (24, 'women-footwear-clogs', 'Сабо и мулы', 44, 45, 18),
  (25, 'women-footwear-lacing-shoes', 'Обувь на шнуровке', 46, 47, 18),
  (26, 'women-footwear-shoes', 'Туфли', 48, 49, 18),
  (27, 'women-footwear-ankle-boots', 'Ботильоны', 50, 51, 18),

  (28, 'women-accessories', 'Аксессуары', 53, 74, 2),
  (29, 'women-accessories-sunglasses', 'Солнцезащитные Очки', 54, 55, 28),
  (30, 'women-accessories-purses', 'Кошельки', 56, 57, 28),
  (31, 'women-accessories-belts', 'Ремни', 58, 59, 28),
  (32, 'women-accessories-scarfs', 'Шарфы', 60, 61, 28),
  (33, 'women-accessories-silk-shawls', 'Шелковые платки', 62, 63, 28),
  (34, 'women-accessories-headdresses', 'Головные уборы', 64, 65, 28),
  (35, 'women-accessories-gloves', 'Перчатки', 66, 67, 28),
  (36, 'women-accessories-for-hairs', 'Аксессуары для волос', 68, 69, 28),
  (37, 'women-accessories-for-handbags', 'Аксессуары для сумок', 70, 71, 28),
  (38, 'women-accessories-for-phones', 'Аксессуары для телефонов', 72, 73, 28),

  (39, 'women-jewelry', 'Украшения', 75, 92, 2),
  (40, 'women-jewelry-rings', 'Кольца', 76, 77, 39),
  (41, 'women-jewelry-bracelets', 'Браслеты', 78, 79, 39),
  (42, 'women-jewelry-brooches', 'Булавки и броши', 80, 81, 39),
  (43, 'women-jewelry-necklaces', 'Ожерелья', 82, 83, 39),
  (44, 'women-jewelry-pendants', 'Подвески', 84, 85, 39),
  (45, 'women-jewelry-long-necklaces', 'Длинные ожерелья', 86, 87, 39),
  (46, 'women-jewelry-jewelry-sets', 'Ювелирные наборы', 88, 89, 39),
  (47, 'women-jewelry-earrings', 'Серьги', 90, 91, 39),

  (48, 'man', 'Мужское', 94, 155, 1),

  (49, 'man-clothes', 'Одежда', 95, 118, 48),
  (50, 'man-clothes-shirts', 'Рубашки', 96, 97, 49),
  (51, 'man-clothes-polo-shirts', 'Футболки Поло', 98, 99, 49),
  (52, 'man-clothes-tshirts', 'Футболки', 100, 101, 49),
  (53, 'man-clothes-cardigans', 'Джемперы и свитшоты', 102, 103, 49),
  (54, 'man-clothes-jackets', 'Пиджаки', 104, 105, 49),
  (55, 'man-clothes-coats', 'Пальто', 106, 107, 49),
  (56, 'man-clothes-suits', 'Костюмы', 108, 109, 49),
  (57, 'man-clothes-trousers', 'Брюки', 110, 111, 49),
  (58, 'man-clothes-jeans', 'Джинсы', 112, 113, 49),
  (59, 'man-clothes-shorts', 'Шорты', 114, 115, 49),
  (60, 'man-clothes-swimming-trunks', 'Плавки', 116, 117, 49),

  (61, 'man-footwear', 'Обувь', 119, 130, 48),
  (62, 'man-footwear-boots', 'Ботинки', 120, 121, 61),
  (63, 'man-footwear-sandals', 'Сандалии', 122, 123, 61),
  (64, 'man-footwear-sneakers', 'Кроссовки', 124, 125, 61),
  (65, 'man-footwear-lacing-shoes', 'Обувь на шнуровке', 126, 127, 61),
  (66, 'man-footwear-topsiders-and-moccasins', 'Топсайдеры и мокасины', 128, 129, 61),

  (67, 'man-bags', 'Сумки', 131, 136, 48),
  (68, 'man-bags-handbags', 'Сумки', 132, 133, 67),
  (69, 'man-bags-purses', 'Маленькие сумки, кошельки и футляры', 134, 135, 67),

  (70, 'man-accessories', 'Аксессуары', 137, 154, 48),
  (71, 'man-accessories-sunglasses', 'Солнцезащитные очки', 138, 139, 70),
  (72, 'man-accessories-scarfs', 'Шарфы и платки', 140, 141, 70),
  (73, 'man-accessories-neckties', 'Галстуки', 142, 143, 70),
  (74, 'man-accessories-cufflinks', 'Запонки', 144, 145, 70),
  (75, 'man-accessories-belts', 'Ремни', 146, 147, 70),
  (76, 'man-accessories-hats', 'Шляпы и головные уборы', 148, 149, 70),
  (77, 'man-accessories-jewelry', 'Ювелирные изделия', 150, 151, 70),
  (78, 'man-accessories-gloves', 'Перчатки', 152, 153, 70),

  (79, 'chilren', 'Детское', 156, 221, 1),

  (80, 'children-girls', 'Для девочек', 157, 174, 79),
  (81, 'children-girls-dresses', 'Платья', 158, 159, 80),
  (82, 'children-girls-skirts', 'Юбки', 160, 161, 80),
  (83, 'children-girls-trousers', 'Брюки', 162, 163, 80),
  (84, 'children-girls-outwear', 'Верхняя одежда', 164, 165, 80),
  (85, 'children-girls-suits', 'Костюмы', 166, 167, 80),
  (86, 'children-girls-shorts', 'Шорты', 168, 169, 80),
  (87, 'children-girls-knitwear', 'Вязаные изделия', 170, 171, 80),
  (88, 'children-girls-jackets', 'Куртки и пальто', 172, 173, 80),

  (89, 'children-boys', 'Для мальчиков', 175, 188, 79),
  (90, 'children-boys-suits', 'Костюмы', 176, 177, 89),
  (91, 'children-boys-trousers', 'Брюки', 178, 179, 89),
  (92, 'children-boys-shorts', 'Шорты', 180, 181, 89),
  (93, 'children-boys-outwear', 'Верхняя одежда', 182, 183, 89),
  (94, 'children-boys-knitwear', 'Вязаные изделия', 184, 185, 89),
  (95, 'children-boys-jackets', 'Куртки и пальто', 186, 187, 89),

  (96, 'children-footwear', 'Обувь', 189, 206, 79),
  (97, 'children-footwear-slippers', 'Домашние тапочки', 190, 191, 96),
  (98, 'children-footwear-sandals', 'Сандалии', 192, 193, 96),
  (99, 'children-footwear-ballet-shoes', 'Балетки', 194, 195, 96),
  (100, 'children-footwear-sneakers', 'Кросовки', 196, 197, 96),
  (101, 'children-footwear-boots', 'Ботинки', 198, 199, 96),
  (102, 'children-footwear-lacing-shoes', 'Обувь на шнуровке', 200, 201, 96),
  (103, 'children-footwear-moccasins', 'Мокасины', 202, 203, 96),
  (104, 'children-footwear-first-days-footwear', 'Первая обувь', 204, 205, 96),

  (105, 'children-accessories', 'Аксессуары', 207, 220, 79),
  (106, 'children-accessories-sunglasses', 'Солнцезащитные очки', 208, 209, 105),
  (107, 'children-accessories-belts', 'Ремни, подтяжки', 210, 211, 105),
  (108, 'children-accessories-scarfs', 'Шарфы', 212, 213, 105),
  (109, 'children-accessories-hats', 'Шляпы и перчатки', 214, 215, 105),
  (110, 'children-accessories-bags', 'Сумки и пеналы', 216, 217, 105),
  (111, 'children-accessories-jewelry', 'Ювелирные изделия', 218, 219, 105),

  (112, 'lifestyle', 'Lifestyle', 222, 233, 1),
  (113, 'lifestyle-design', 'Дизайн и оформление', 223, 224, 112),
  (114, 'lifestyle-art', 'Искусство и культура', 225, 226, 112),
  (115, 'lifestyle-sport', 'Спорт и отдых', 227, 228, 112),
  (116, 'lifestyle-tech-accessories', 'Аксессуары для техники', 229, 230, 112),
  (117, 'lifestyle-pets-accessories', 'Аксессуары для домашних животных', 231, 232, 112);

ALTER SEQUENCE category_id_seq RESTART WITH 118;


INSERT INTO brand (id, name) VALUES
  (1, 'Arctic Explorer'),
  (2, 'ATP Atelier'),
  (3, 'Astrid Andersen'),
  (4, 'Alonova'),
  (5, 'Anna October'),
  (6, 'Anna K'),
  (7, 'Artem Klimchuk'),
  (8, 'A.M.G.'),
  (9, 'Audra'),
  (10, 'ADEAM'),
  (11, 'Adam Selman'),
  (12, 'Adam Lippes'),
  (13, 'Alejandra Alonso Rojas'),
  (14, 'Alexander Wang'),
  (15, 'A Détacher'),
  (16, 'Area'),
  (17, 'Altuzarra'),
  (18, 'Apiece Apart'),
  (19, 'Alice+Olivia'),
  (20, 'Anna Sui'),
  (21, 'ATM Anthony Thomas Melillo'),
  (22, 'Assembly'),
  (23, 'Ashley Williams'),
  (24, 'Alena Akhmadullina'),
  (25, 'Anya Hindmarch'),
  (26, 'Antonio Berardi'),
  (27, 'Ashish'),
  (28, 'A.W.A.K.E.'),
  (29, 'Alberta Ferretti'),
  (30, 'Attico'),
  (31, 'Antonio Marras'),
  (32, 'Agnona'),
  (33, 'Aquilano.Rimondi'),
  (34, 'Au Jour Le Jour'),
  (35, 'Arthur Arbesser'),
  (36, 'Albino Teodoro'),
  (37, 'Aalto'),
  (38, 'Anrealage'),
  (39, 'Ann Demeulemeester'),
  (40, 'Alexis Mabille'),
  (41, 'Atlein'),
  (42, 'Alyx'),
  (43, 'Andrew Gn'),
  (44, 'Alessandra Rich'),
  (45, 'Alexandre Vauthier'),
  (46, 'Acne Studios'),
  (47, 'Andreas Kronthaler for Vivienne Westwood'),
  (48, 'Akris'),
  (49, 'Aganovich'),
  (50, 'A.P.C.'),
  (51, 'Alexander McQueen'),
  (52, 'Alexandr Rogov'),
  (53, 'Anastasia Kondakova'),
  (54, 'Aka Nanita'),
  (55, 'Alexander Terekhov'),
  (56, 'Aula'),
  (57, 'A La Russe Anastasia Romantsova'),
  (58, 'Asian Fashion Meets Tokyo'),
  (59, 'AG'),
  (60, 'Ace & Jig'),
  (61, 'ACUOD by CHANU'),
  (62, 'Akikoaoki'),
  (63, 'Bonpoint'),
  (64, 'Beckmans'),
  (65, 'Baum und Pferdgarten'),
  (66, 'By Malene Birger'),
  (67, 'Brock Collection'),
  (68, 'Beaufille'),
  (69, 'Badgley Mischka'),
  (70, 'Brandon Maxwell'),
  (71, 'Barbara Tfank'),
  (72, 'Bibhu Mohapatra'),
  (73, 'Brooks Brothers'),
  (74, 'Baja East'),
  (75, 'BY. Bonnie Young'),
  (76, 'Belstaff'),
  (77, 'Brunello Cucinelli'),
  (78, 'Bally'),
  (79, 'Bottega Veneta'),
  (80, 'Blumarine'),
  (81, 'Braschi'),
  (82, 'Bobkova'),
  (83, 'Balmain'),
  (84, 'Balenciaga'),
  (85, 'Back'),
  (86, 'Barbara Bui'),
  (87, 'Bernhard Willhelm'),
  (88, 'Biryukov'),
  (89, 'Bella Potemkina'),
  (90, 'Boss Hugo Boss'),
  (91, 'Bed j.w. Ford'),
  (92, 'Cecilie Bahnsen'),
  (93, 'Chakshyn'),
  (94, 'Cynthia Rowley'),
  (95, 'Colovos'),
  (96, 'Cinq à Sept'),
  (97, 'Creatures of Comfort'),
  (98, 'Calvin Klein'),
  (99, 'Cushnie et Ochs'),
  (100, 'Chromat'),
  (101, 'Creatures of the Wind'),
  (102, 'CG'),
  (103, 'Christian Siriano'),
  (104, 'Co'),
  (105, 'Custo Barcelona'),
  (106, 'Carolina Herrera'),
  (107, 'Camilla and Marc'),
  (108, 'Coach 1941'),
  (109, 'Christopher Esber'),
  (110, 'Central Saint Martins'),
  (111, 'Chalayan'),
  (112, 'Christopher Kane'),
  (113, 'Chapurin'),
  (114, 'Courrèges'),
  (115, 'Carmen March'),
  (116, 'Chloé'),
  (117, 'Carven'),
  (118, 'Christian Dior'),
  (119, 'Christian Wijnants'),
  (120, 'Comme des Garçons'),
  (121, 'Céline'),
  (122, 'Chanel'),
  (123, 'Cacharel'),
  (124, 'Color.Temperature'),
  (125, 'Chika Kisada'),
  (126, 'Dorothee Schumacher'),
  (127, 'Dzhus'),
  (128, 'Dion Lee'),
  (129, 'Diane von Furstenberg'),
  (130, 'Dennis Basso'),
  (131, 'Delpozo'),
  (132, 'Derek Lam'),
  (133, 'Daks'),
  (134, 'Duro Olowu'),
  (135, 'David Koma'),
  (136, 'Dondup'),
  (137, 'Diesel Black Gold'),
  (138, 'Dolce & Gabbana'),
  (139, 'Dries Van Noten'),
  (140, 'Dice Kayek'),
  (141, 'Dimaneu'),
  (142, 'DressedUndressed'),
  (143, 'Discovered'),
  (144, 'Dsquared²'),
  (145, 'Esther Perbandt'),
  (146, 'Emelle Janrell'),
  (147, 'Elaine Hersby'),
  (148, 'Elena Rial'),
  (149, 'Elena Burenina'),
  (150, 'Elena Burba'),
  (151, 'Elenareva'),
  (152, 'Escada'),
  (153, 'Erin Fetherston'),
  (154, 'Eric Schlosberg'),
  (155, 'Elizabeth Kennedy'),
  (156, 'Edun'),
  (157, 'Eckhaus Latta'),
  (158, 'Elizabeth and James'),
  (159, 'Eudon Choi'),
  (160, 'Emilia Wickstead'),
  (161, 'Edeline Lee'),
  (162, 'Erdem'),
  (163, 'Emilio de la Morena'),
  (164, 'Emilio Pucci'),
  (165, 'Erika Cavallini'),
  (166, 'Emporio Armani'),
  (167, 'Etro'),
  (168, 'Ermanno Scervino'),
  (169, 'Elisabetta Franchi'),
  (170, 'Each x Other'),
  (171, 'Elie Saab'),
  (172, 'Esteban Cortazar'),
  (173, 'Ellery'),
  (174, 'Enteley'),
  (175, 'Erica Zaionts'),
  (176, 'Elena Piskulina'),
  (177, 'Ethosens'),
  (178, 'Freya Dalsjø'),
  (179, 'Frolov'),
  (180, 'Flow'),
  (181, 'Frame Denim'),
  (182, 'Fashion East'),
  (183, 'Faustine Steinmetz'),
  (184, 'Fay'),
  (185, 'Fausto Puglisi'),
  (186, 'Francesco Scognamiglio'),
  (187, 'Fendi'),
  (188, 'For Restless Sleepers'),
  (189, 'Faith Connexion'),
  (190, 'Fenty x Puma'),
  (191, 'Firdaws'),
  (192, 'fy:r'),
  (193, 'Ganni'),
  (194, 'Gasanova'),
  (195, 'Gibsh'),
  (196, 'Gudu'),
  (197, 'Gary Graham'),
  (198, 'Gypsy Sport'),
  (199, 'Gabriela Hearst'),
  (200, 'Gareth Pugh'),
  (201, 'Gucci'),
  (202, 'Grinko'),
  (203, 'Gabriele Colangelo'),
  (204, 'Giamba'),
  (205, 'Giorgio Armani'),
  (206, 'Guy Laroche'),
  (207, 'Giambattista Valli'),
  (208, 'Givenchy'),
  (209, 'Goga Nikabadze'),
  (210, 'Growing Pains'),
  (211, 'House of Dagmar'),
  (212, 'Hope'),
  (213, 'Han Kjøbenhavn'),
  (214, 'Henrik Vibskov'),
  (215, 'Haus Alkire'),
  (216, 'Hellessy'),
  (217, 'HANEY'),
  (218, 'House of Holland'),
  (219, 'Huishan Zhang'),
  (220, 'Helen Yarmak'),
  (221, 'Haider Ackermann'),
  (222, 'Hermès'),
  (223, 'Halston Heritage'),
  (224, 'House Commune'),
  (225, 'Hanae Mori Manuscrit'),
  (226, 'Ivanman'),
  (227, 'Ida Klamborn'),
  (228, 'Ivan Grundahl'),
  (229, 'ICB'),
  (230, 'Isa Arfen'),
  (231, 'Isabel Marant'),
  (232, 'Issey Miyake'),
  (233, 'Inshade'),
  (234, 'Ivka'),
  (235, 'Igor Gulyaev'),
  (236, 'Izeta'),
  (237, 'IRO'),
  (238, 'Julia Seemann'),
  (239, 'J.Lindeberg'),
  (240, 'Ji Oh'),
  (241, 'Jean Gritsfeldt'),
  (242, 'Jeffrey Dodd'),
  (243, 'Jenni Kayne'),
  (244, 'Jason Wu'),
  (245, 'Jeremy Scott'),
  (246, 'Jill Stuart'),
  (247, 'Jonathan Simkhai'),
  (248, 'Jenny Packham'),
  (249, 'J.Crew'),
  (250, 'Jil Sander Navy'),
  (251, 'Josie Natori'),
  (252, 'J. Mendel'),
  (253, 'Julien Macdonald'),
  (254, 'J.W.Anderson'),
  (255, 'Joseph'),
  (256, 'J. JS Lee'),
  (257, 'Jil Sander'),
  (258, 'Jacquemus'),
  (259, 'Junya Watanabe'),
  (260, 'Johanna Ortiz'),
  (261, 'Joie'),
  (262, 'John Galliano'),
  (263, 'Juan Carlos Obando'),
  (264, 'Jitrois'),
  (265, 'J.Kim'),
  (266, 'Jourden'),
  (267, 'J Brand'),
  (268, 'Kenzo'),
  (269, 'Ksenia Schnaider'),
  (270, 'Khaite'),
  (271, 'Katerina Kvit'),
  (272, 'Kendall + Kylie'),
  (273, 'Katie Gallagher'),
  (274, 'Kate Spade'),
  (275, 'Kimora Lee Simmons'),
  (276, 'Kiton'),
  (277, 'Krizia'),
  (278, 'Koché'),
  (279, 'Kenzo La Collection Memento'),
  (280, 'Kitx'),
  (281, 'Kolor'),
  (282, 'Karen Walker'),
  (283, 'Ksenia Knyazeva'),
  (284, 'Ksenia Seraya'),
  (285, 'KES'),
  (286, 'Keisukeyoshida'),
  (287, 'L’Homme Rouge'),
  (288, 'Lazoschmidi'),
  (289, 'Lala Berlin'),
  (290, 'Lake Studio'),
  (291, 'Litkovskaya'),
  (292, 'La Perla'),
  (293, 'Linder'),
  (294, 'Lacoste'),
  (295, 'Lela Rose'),
  (296, 'Libertine'),
  (297, 'Lisa Perry'),
  (298, 'Le Kilt'),
  (299, 'Luisa Beccaria'),
  (300, 'Les Copains'),
  (301, 'Lucio Vanotti'),
  (302, 'Lanvin'),
  (303, 'Lemaire'),
  (304, 'Loewe'),
  (305, 'Lutz Huelle'),
  (306, 'Leonard'),
  (307, 'Longchamp'),
  (308, 'Louis Vuitton'),
  (309, 'Lumier Garson by JeanRudoff'),
  (310, 'Laura Siegel'),
  (311, 'Lemlem'),
  (312, 'Lithium'),
  (313, 'Malaikaraiss'),
  (314, 'Marcel Ostertag'),
  (315, 'Maison Rabih Kayrouz'),
  (316, 'Mark Kenly Domino Tan'),
  (317, 'Matthew Adams Dolan'),
  (318, 'Marianna Senchina'),
  (319, 'Monique Lhuillier'),
  (320, 'M.Martin'),
  (321, 'Marissa Webb'),
  (322, 'Milly'),
  (323, 'Maryam Nassir Zadeh'),
  (324, 'Monse'),
  (325, 'M.Patmos'),
  (326, 'Mara Hoffman'),
  (327, 'Moncler Grenoble'),
  (328, 'Michael Kors Collection'),
  (329, 'Marchesa'),
  (330, 'Marc Jacobs'),
  (331, 'Maki Oh'),
  (332, 'Molly Goddard'),
  (333, 'Margaret Howell'),
  (334, 'MM6 Maison Margiela'),
  (335, 'Mulberry'),
  (336, 'Mary Katrantzou'),
  (337, 'Markus Lupfer'),
  (338, 'Marques''Almeida'),
  (339, 'Marta Jakubowski'),
  (340, 'Max Mara'),
  (341, 'Moschino'),
  (342, 'Max Mara Atelier'),
  (343, 'Marco de Vincenzo'),
  (344, 'Missoni'),
  (345, 'Marchesa Notte'),
  (346, 'Marni'),
  (347, 'MSGM'),
  (348, 'Maison Margiela'),
  (349, 'Maison Kitsuné'),
  (350, 'Manish Arora'),
  (351, 'Melitta Baumeister'),
  (352, 'Mugler'),
  (353, 'Maggie Marilyn'),
  (354, 'Marcelo Burlon County of Milan'),
  (355, 'Martin Grant'),
  (356, 'Mira Mikati'),
  (357, 'Miu Miu'),
  (358, 'Moncler Gamme Rouge'),
  (359, 'Maticevski'),
  (360, 'M Missoni'),
  (361, 'Mach & Mach'),
  (362, 'Masterpeace'),
  (363, 'Motohiro Tanji'),
  (364, 'Matohu'),
  (365, 'Moto Guo'),
  (366, 'Murral'),
  (367, 'Navro'),
  (368, 'Nadya Dzyak'),
  (369, 'Nicholas K'),
  (370, 'Novis'),
  (371, 'Nicole Miller'),
  (372, 'Nicopanda'),
  (373, 'Norma Kamali'),
  (374, 'Naeem Khan'),
  (375, 'Narciso Rodriguez'),
  (376, 'Nomia'),
  (377, 'Nili Lotan'),
  (378, 'Natasha Zinko'),
  (379, 'Nanette Lepore'),
  (380, 'No. 21'),
  (381, 'Neil Barrett'),
  (382, 'Nehera'),
  (383, 'Nina Ricci'),
  (384, 'Noir Kei Ninomiya'),
  (385, 'Noa Raviv'),
  (386, 'No. 6'),
  (387, 'Name'),
  (388, 'Ostel'),
  (389, 'Oak'),
  (390, 'Oscar de la Renta'),
  (391, 'Orla Kiely'),
  (392, 'Osman'),
  (393, 'Olivier Theyskens'),
  (394, 'Off-White'),
  (395, 'Officine Generale'),
  (396, 'Olympia Le-Tan'),
  (397, 'Perret Schaad'),
  (398, 'Poustovit'),
  (399, 'Patrow'),
  (400, 'PH5'),
  (401, 'Pam & Gela'),
  (402, 'Pamella Roland'),
  (403, 'Public School'),
  (404, 'Prabal Gurung'),
  (405, 'Protagonist'),
  (406, 'Proenza Schouler'),
  (407, 'Philipp Plein'),
  (408, 'Pyer Moss'),
  (409, 'Ports 1961'),
  (410, 'Palmer//Harding'),
  (411, 'Preen by Thornton Bregazzi'),
  (412, 'Peter Pilotto'),
  (413, 'Phoebe English'),
  (414, 'Pringle of Scotland'),
  (415, 'Peter Jensen'),
  (416, 'Prada'),
  (417, 'Philosophy di Lorenzo Serafini'),
  (418, 'Paule Ka'),
  (419, 'Piazza Sempione'),
  (420, 'Paul & Yakov'),
  (421, 'Paco Rabanne'),
  (422, 'Paul & Joe'),
  (423, 'Portnoy Beso'),
  (424, 'Rodebjer'),
  (425, 'Rotsaniyom'),
  (426, 'Rachel Zoe'),
  (427, 'Rachel Comey'),
  (428, 'Red Valentino'),
  (429, 'Raquel Allegra'),
  (430, 'R13'),
  (431, 'Rhié'),
  (432, 'Robert Rodriguez'),
  (433, 'Rebecca Taylor'),
  (434, 'Rebecca Minkoff'),
  (435, 'Rag & Bone'),
  (436, 'Ryan Roche'),
  (437, 'Rachel Antonoff'),
  (438, 'Rosetta Getty'),
  (439, 'Rosie Assoulin'),
  (440, 'Ryan Lo'),
  (441, 'Roland Mouret'),
  (442, 'Roksanda'),
  (443, 'Roberto Cavalli'),
  (444, 'Rossella Jardini'),
  (445, 'Rodarte'),
  (446, 'Rochas'),
  (447, 'Rick Owens'),
  (448, 'Redemption'),
  (449, 'Rahul Mishra'),
  (450, 'Reem Acra'),
  (451, 'Rasario'),
  (452, 'Roggyeki'),
  (453, 'Steinrohner'),
  (454, 'Space by RMH'),
  (455, 'Stand'),
  (456, 'Shushan'),
  (457, 'Sasha Kanevski'),
  (458, 'Sachin & Babi'),
  (459, 'Self-Portrait'),
  (460, 'Simon Miller'),
  (461, 'Sies Marjan'),
  (462, 'Suzanne Rae'),
  (463, 'Samuji'),
  (464, 'Sally LaPointe'),
  (465, 'See by Chloé'),
  (466, 'Saloni'),
  (467, 'Sea'),
  (468, 'Sid Neigum'),
  (469, 'Simone Rocha'),
  (470, 'Sharon Wauchob'),
  (471, 'Sportmax'),
  (472, 'Situationist'),
  (473, 'Stella Jean'),
  (474, 'Simonetta Ravizza'),
  (475, 'Salvatore Ferragamo'),
  (476, 'Sara Battaglia'),
  (477, 'Saint Laurent'),
  (478, 'Sass & Bide'),
  (479, 'Sonia Rykiel'),
  (480, 'Stella McCartney'),
  (481, 'Sacai'),
  (482, 'Slava Zaitsev'),
  (483, 'Saint-Tokyo'),
  (484, 'Sergey Sysoev'),
  (485, 'Sulvam'),
  (486, 'Tonsure'),
  (487, 'Tomas Maier'),
  (488, 'T.Mosca'),
  (489, 'Theo'),
  (490, 'the coat by Katya Silchenko'),
  (491, 'Trina Turk'),
  (492, 'Tadashi Shoji'),
  (493, 'TSE'),
  (494, 'Tanya Taylor'),
  (495, 'Telfar'),
  (496, 'Tibi'),
  (497, 'Tome'),
  (498, 'The Row'),
  (499, 'Tory Burch'),
  (500, 'Timofeeva'),
  (501, 'Thom Browne'),
  (502, 'Tracy Reese'),
  (503, 'The Elder Statesman'),
  (504, 'Theory'),
  (505, 'Topshop Unique'),
  (506, 'Toga'),
  (507, 'Temperley London'),
  (508, 'Tod''s'),
  (509, 'Trussardi'),
  (510, 'Talbot Runhof'),
  (511, 'Tsumori Chisato'),
  (512, 'Tegin'),
  (513, 'Tak.Ori'),
  (514, 'Tosia'),
  (515, 'Tessa'),
  (516, 'Tako Mekvabidze'),
  (517, 'Tom Ford'),
  (518, 'Tucker'),
  (519, 'Tabula Rasa'),
  (520, 'Taakk'),
  (521, 'Ulla Johnson'),
  (522, 'Undercover'),
  (523, 'Unravel'),
  (524, 'Ujoh'),
  (525, 'Vetements'),
  (526, 'V by GRES'),
  (527, 'Vozianov'),
  (528, 'VFiles'),
  (529, 'Visvim'),
  (530, 'Victoria Beckham'),
  (531, 'Vaquera'),
  (532, 'Veronica Beard'),
  (533, 'Vivienne Tam'),
  (534, 'Victor Alfaro'),
  (535, 'Versus'),
  (536, 'Victoria Victoria Beckham'),
  (537, 'Vivetta'),
  (538, 'Versace'),
  (539, 'Vionnet'),
  (540, 'Vera Wang'),
  (541, 'Vanessa Bruno'),
  (542, 'Vanessa Seward'),
  (543, 'Véronique Leroy'),
  (544, 'Vivienne Westwood'),
  (545, 'Vejas'),
  (546, 'Valentino'),
  (547, 'Veronique Branquinho'),
  (548, 'Valentin Yudashkin'),
  (549, 'Vika Gazinskaya'),
  (550, 'Victoria Andreyanova'),
  (551, 'Won Hundred'),
  (552, 'Whyred'),
  (553, 'Who is it?'),
  (554, 'WeAnnaBe'),
  (555, 'Whit'),
  (556, 'Warm'),
  (557, 'Wendy Nichol'),
  (558, 'Whistles'),
  (559, 'Wanda Nylon'),
  (560, 'Yulia Yefimtchuk+'),
  (561, 'Yeohlee'),
  (562, 'Yigal Azrouël'),
  (563, 'Yeezy'),
  (564, 'Y/Project'),
  (565, 'Yohji Yamamoto'),
  (566, 'Yanina'),
  (567, 'Y’s'),
  (568, 'Yulia Nikolaeva'),
  (569, 'Yasya Minochkina'),
  (570, 'Yohei Ohno'),
  (571, 'Zimmermann'),
  (572, 'Zadig & Voltaire'),
  (573, 'Zero + Maria Cornejo'),
  (574, 'ZAC Zac Posen'),
  (575, 'Zac Posen'),
  (576, 'Zoë Jordan'),
  (577, 'Zuhair Murad');
ALTER SEQUENCE brand_id_seq RESTART WITH 578;


INSERT INTO attribute (id, name) VALUES
  (1, 'Цвет'),
  (2, 'Материал');
ALTER SEQUENCE attribute_id_seq RESTART WITH 3;

INSERT INTO attribute_value (id, attribute_id, value) VALUES
  (1, 1, 'Красный'),
  (2, 1, 'Черный'),
  (3, 1, 'Желтый'),
  (4, 1, 'Экрю'),
  (5, 1, 'Розовый'),
  (6, 1, 'Бежевый'),
  (7, 1, 'Синий'),

  (8, 2, 'Акрил'),
  (9, 2, 'Вискоза'),
  (10, 2, 'Искусственная кожа'),
  (11, 2, 'Искусственный мех'),
  (12, 2, 'Лайкра'),
  (13, 2, 'Лен'),
  (14, 2, 'Натуральная замша'),
  (15, 2, 'Натуральная кожа'),
  (16, 2, 'Неопрен'),
  (17, 2, 'Полиамид'),
  (18, 2, 'Полимер'),
  (19, 2, 'Полиэстер'),
  (20, 2, 'Текстиль'),
  (21, 2, 'Хлопок'),
  (22, 2, 'Шелк');
ALTER SEQUENCE attribute_value_id_seq RESTART WITH 23;

-- пароль 123
INSERT INTO "user" (id, email, hashed_password, registration_time, activation_time) VALUES
  (1, 'user@domain.ru', '$2a$10$P6f4lEaTfw3suijLIiNXBOvKmTZNabIOy5rvMgSjWZeGBFjJIjIx2', now(), now());
ALTER SEQUENCE user_id_seq RESTART WITH 2;

INSERT INTO product_status (id, name) VALUES
  (1, 'Винтаж');
ALTER SEQUENCE product_status_id_seq RESTART WITH 2;

INSERT INTO product_condition(id, name) VALUES
	(1, 'Ни разу'),
	(2, 'Отличное состояние'),
	(3, 'Хорошее состояние'),
	(4, 'Так себе');
ALTER SEQUENCE product_condition_id_seq RESTART WITH 5;

INSERT INTO category_attribute_binding (category_id, attribute_id) values
  (2, 1),
  (2, 2);

INSERT INTO size (id, category_id, russian, european, international, american) VALUES
  (1, 3, '40','34', 'XS', null),
  (2, 3, '42','36', 'S', null),
  (3, 3, '44','38', 'S', null),
  (4, 3, '46','40', 'M', null),
  (5, 3, '48','42', 'M', null),
  (6, 3, '50','44', 'L', null),
  (7, 3, '52','46', 'L', null),
  (8, 3, '54','48', 'XL', null),
  (9, 3, '56','50', 'XL', null),
  (10, 3, '58','52', 'XL', null),
  (11, 3, '60','54', 'XXL', null),
  (12, 3, '62','56', 'XXl', null),
  (13, 3, '64','58', 'BXL', null),
  (14, 18, '34', '35', null, '5'),
  (15, 18, '34.5', '35.5', null, '5.5'),
  (16, 18, '35', '36', null, '6'),
  (17, 18, '36', '36.5', null, '6.5'),
  (18, 18, '36.5', '37', null, '7'),
  (19, 18, '37', '37.5', null, '7.5'),
  (20, 18, '37.5', '38', null, '8'),
  (21, 18, '38', '38.5', null, '8.5'),
  (22, 18, '39', '39', null, '9'),
  (23, 18, '39.5', '39.5', null, '9.5'),
  (24, 18, '40', '40-41', null, '10'),
  (25, 18, '41', '41', null, '10.5'),
  (26, 18, '41.5', '41-42', null, '11'),
  (27, 18, '42', '42', null, '11.5'),
  (28, 18, '42.5', '42-43', null, '12');
ALTER SEQUENCE size_id_seq RESTART WITH 29;

