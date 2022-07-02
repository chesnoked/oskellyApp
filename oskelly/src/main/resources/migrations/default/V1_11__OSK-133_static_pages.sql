CREATE TABLE static_page (
  id         BIGSERIAL PRIMARY KEY,
  page_group TEXT,
  url        TEXT UNIQUE,
  name       TEXT,
  content    TEXT
);

INSERT INTO static_page (page_group, url, name, content) VALUES
  ('INFO', '/test', 'Тестовая страница', '<p>Тестовая страница</p>'),
  ('BLOG', '/table', 'Страница с таблицей', '<p>Тестовый контент</p>
<table border="1" cellpadding="1" cellspacing="1" style="width:500px">
	<tbody>
		<tr>
			<td>Ключ</td>
			<td>Значение</td>
		</tr>
	</tbody>
</table>

<p>&nbsp;</p>');
