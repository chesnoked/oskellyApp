package su.reddot.domain.service.staticPage;

import su.reddot.domain.model.staticPage.StaticPageTag;

import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 05.08.17.
 */
public interface StaticPageTagService {

	List<StaticPageTag> findAll();

	Optional<StaticPageTag> findOne(Long id);
}
