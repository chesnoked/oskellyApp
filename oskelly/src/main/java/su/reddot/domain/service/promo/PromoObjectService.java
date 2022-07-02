package su.reddot.domain.service.promo;

import com.google.common.base.Strings;
import su.reddot.domain.service.promo.selection.TooManyPromoException;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления частью галереи на главной странице
 *
 * @param <G> возвращаемый объект
 * @param <P> получаемый объект
 */
public interface PromoObjectService<G, P> {

	List<G> getAll();

	Optional<G> getById(Long id);

	void create(P obj) throws TooManyPromoException;

	void update(P obj);

	void delete(Long id);

	default String normalizeUrl(String url) {
		if (Strings.isNullOrEmpty(url)) {
			return "/";
		}
		if (!url.substring(0, 1).equals("/")) {
			return "/" + url;
		}
		return url;
	}
}
