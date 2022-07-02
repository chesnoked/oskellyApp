package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.staticPage.StaticPageTag;

/**
 * @author Vitaliy Khludeev on 05.08.17.
 */
public interface StaticPageTagRepository extends JpaRepository<StaticPageTag, Long> {
}
