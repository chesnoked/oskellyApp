TRUNCATE TABLE user_authority_binding, authority RESTART IDENTITY;

INSERT INTO authority (id, name, type) VALUES
  (1, 'ADMIN', 'ADMIN'),
  (2, 'PRODUCT_MODERATION', 'MODERATOR'),
  (3, 'AUTHORITY_MODERATION', 'MODERATOR'),
  (4, 'PUBLICATION', 'MODERATOR'),
  (5, 'PUBLICATION_DELETE', 'MODERATOR'),
  (6, 'ORDER_MODERATION', 'MODERATOR');
ALTER SEQUENCE authority_id_seq RESTART WITH 7;

INSERT INTO user_authority_binding (user_id, authority_id) VALUES
  (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6);
