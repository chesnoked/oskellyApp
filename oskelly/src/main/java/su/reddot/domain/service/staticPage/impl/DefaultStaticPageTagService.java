package su.reddot.domain.service.staticPage.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.StaticPageTagRepository;
import su.reddot.domain.model.staticPage.StaticPageTag;
import su.reddot.domain.service.staticPage.StaticPageTagService;

import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 05.08.17.
 */
@Component
@AllArgsConstructor
public class DefaultStaticPageTagService implements StaticPageTagService {

	private final StaticPageTagRepository staticPageTagRepository;

	@Override
	public List<StaticPageTag> findAll() {
		return staticPageTagRepository.findAll();
	}

	@Override
	public Optional<StaticPageTag> findOne(Long id) {
		return Optional.of(staticPageTagRepository.findOne(id));
	}
}
