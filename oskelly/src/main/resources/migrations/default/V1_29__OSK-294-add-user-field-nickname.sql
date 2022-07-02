alter table "user"
		add COLUMN nickname TEXT NOT NULL DEFAULT '' UNIQUE;

update "user" set nickname = 'user';