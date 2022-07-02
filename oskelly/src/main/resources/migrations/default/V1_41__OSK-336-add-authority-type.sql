ALTER TABLE authority
	ADD COLUMN type TEXT;

UPDATE authority
SET type = 'ADMIN'
WHERE id = 1 OR id = 2;

UPDATE authority
SET type = 'MODERATOR'
WHERE id = 3;

alter table authority alter column type set NOT NULL;