package su.reddot.domain.service.product.api;

import su.reddot.domain.model.user.User;
import su.reddot.domain.service.product.CatalogProductPage;

/**
 * @author Vitaliy Khludeev on 13.08.17.
 */
public interface ProductApiService {

	CatalogProductPage getCatalogProductPage(ProductRequest request, User nullableUser);
}
