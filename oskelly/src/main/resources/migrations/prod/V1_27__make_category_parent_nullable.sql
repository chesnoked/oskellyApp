/* Убрать требование обязательного указания родительско категории,
иначе возникает сложность в представлении корня дерева. Чтобы его хранить
с существованием такого ограничения, приходится указывать в кач-ве родителя
его самого. Из-за этого возникает циклическая зависимость при представлении
этой категории при отображении в сущность.
 */
ALTER TABLE category ALTER COLUMN parent_id DROP NOT NULL;

UPDATE category SET parent_id = NULL WHERE left_order = 1;
