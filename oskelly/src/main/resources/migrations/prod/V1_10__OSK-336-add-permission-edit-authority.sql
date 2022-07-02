INSERT INTO authority (id, name, type) VALUES (4, 'PERMISSION_EDIT', 'MODERATOR');
INSERT INTO user_authority_binding (user_id, authority_id) VALUES (1, 4);
ALTER SEQUENCE authority_id_seq RESTART WITH 5;
