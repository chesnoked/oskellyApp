package su.reddot.domain.service.staticPage;

import su.reddot.domain.model.staticPage.StaticPage;
import su.reddot.domain.model.staticPage.StaticPageGroup;
import su.reddot.domain.model.staticPage.StaticPageTag;

import java.util.List;
import java.util.Optional;

public interface StaticPageService {

	List<StaticPage> getAllStaticPages();

	Optional<StaticPage> getStaticPageById(Long id);

	/**
	 * Создает новую статическую страницу
	 *
	 * @param request объект, содержащий в себе данные для создания стат страницы
	 * @param pageGroup группа  статических страниц
	 * @param userId Id пользователя, который вносит изменения в статическую страницу
	 * @throws StaticPageException Возникла ошибка при сохранении стат страницы в БД
	 */
	StaticPage createStaticPage(StaticPageRequest request, StaticPageGroup pageGroup, Long userId) throws StaticPageException;

	/**
	 * Обновляет уже существующую стат страницу
	 *
	 * @param request объект, содержащий в себе данные для создания стат страницы
	 * @param userId Id пользователя, который вносит изменения в статическую страницу
	 * @throws StaticPageException  Возникла ошибка при сохранении стат страницы в БД
	 * @throws NullPointerException не был указан Id страницы или по нему не была найдена уже существующая страница
	 */
	void updateStaticPage(StaticPageRequest request, Long userId) throws StaticPageException, NullPointerException;

	/**
	 * Удаляет уже существующую стат страницу
	 *
	 * @param id идентификатор страницы в БД
	 * @throws NullPointerException по указанному Id не была найдена страница
	 */
	void deleteStaticPageById(Long id) throws NullPointerException;

	/**
	 * Получает страницу по частям ее URL-а
	 *
	 * @param pageGroup URL подмножества статических страниц (блог, информационные страницы и т.п.)
	 * @param url       часть URL-а, непосредственно определяющая страницу
	 * @return Optional, содержащий:<br/>
	 * <ul><li>Информацию о статической странице</li><li>null, если страница не найдена</li></ul>
	 */
	Optional<StaticPage> getStaticPageByPageGroupAndUrl(String pageGroup, String url);

	/**
	 * Получает список страниц по указанной группе стат страниц, сначала старые
	 *
	 * @param staticPageGroup группа статических страниц (блог, инфо и т.п.)
	 * @return список страниц
	 */
	List<StaticPage> getStaticPageByGroup(StaticPageGroup staticPageGroup);

	/**
	 * Получает список страниц по указанной группе стат страниц, сначала новые
	 *
	 * @param staticPageGroup группа статических страниц (блог, инфо и т.п.)
	 * @return список страниц
	 */
	List<StaticPage> getStaticPageByGroupDesc(StaticPageGroup staticPageGroup);

	/**
	 * получает список  всех страниц Блога
	 * @return список страниц
	 */
	List<StaticPage> getStaticPagesForBlog();

	List<StaticPage> getStaticPagesForBlog(Optional<StaticPageTag> tag);

    /**
	 * Публикует статью
	 * @param id ID статьи
	 * @param userId ID пользователя
	 */
	void publish(Long id, Long userId) throws StaticPageException;
}
