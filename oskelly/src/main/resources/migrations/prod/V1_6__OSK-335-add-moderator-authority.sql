INSERT INTO authority (id, name) VALUES (3, 'MODERATOR');
INSERT INTO user_authority_binding (user_id, authority_id) VALUES (1, 3);
ALTER SEQUENCE authority_id_seq RESTART WITH 4;
