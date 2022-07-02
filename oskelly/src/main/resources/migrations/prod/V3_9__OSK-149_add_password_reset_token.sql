ALTER TABLE "user"
    ADD COLUMN password_reset_token TEXT UNIQUE,
    ADD COLUMN password_reset_token_created_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN password_reset_token_used_at TIMESTAMP WITH TIME ZONE,
-- поля токена и времени его создания либо оба установлены, либо оба не установлены;
    ADD CONSTRAINT password_reset_token_check
CHECK (password_reset_token IS NOT NULL AND password_reset_token_created_at IS NOT NULL
       OR password_reset_token IS NULL
          AND password_reset_token_created_at IS NULL
          AND password_reset_token_used_at IS NULL);
