CREATE TABLE tilda_page(
  id BIGSERIAL PRIMARY KEY,
  tilda_page_id BIGINT NOT NULL,
  tilda_project_id BIGSERIAL,
  content TEXT,
  url TEXT,
  is_main_page BOOLEAN
)