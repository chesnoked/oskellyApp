package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.staticPage.StaticPage;
import su.reddot.domain.model.staticPage.StaticPageGroup;
import su.reddot.domain.model.staticPage.StaticPageStatus;
import su.reddot.domain.model.staticPage.StaticPageTag;

import java.util.List;
import java.util.Optional;

public interface StaticPageRepository extends JpaRepository<StaticPage, Long> {

	List<StaticPage> findAllByOrderByIdAsc();

	StaticPage findByPageGroupAndUrl(StaticPageGroup pageGroup, String url);

	Optional<StaticPage> findByUrl(String url);

	List<StaticPage> findByPageGroupAndIsRawHtmlEqualsOrderByIdAsc(StaticPageGroup pageGroup, boolean rawHtml);

	List<StaticPage> findByPageGroupOrderByIdDesc(StaticPageGroup pageGroup);

	List<StaticPage> findByPageGroupAndStatusOrderByIdAsc(StaticPageGroup pageGroup, StaticPageStatus status);

	List<StaticPage> findByPageGroupAndStatusOrderByIdDesc(StaticPageGroup pageGroup, StaticPageStatus status);

	List<StaticPage> findByPageGroupAndStatusAndTagOrderByIdDesc(StaticPageGroup pageGroup, StaticPageStatus status, StaticPageTag tag);
}
