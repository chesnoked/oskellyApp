UPDATE authority
SET name = 'CONTENT_CREATE' WHERE name = 'PUBLICATION';

UPDATE authority
SET name = 'CONTENT_DELETE' WHERE name = 'PUBLICATION_DELETE';

INSERT INTO authority (name, type)
    values ('USER_MODERATION', 'MODERATOR');

