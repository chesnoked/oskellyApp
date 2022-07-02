package su.reddot.domain.service.product.status;

import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductStatus;

import java.util.Optional;

public interface ProductStatusService {

	/**
	 * Устанавливает статус для товара. В случае, если такой статус уже установлен, ничего не происходит
	 *
	 * @param product
	 * @param productStatus
	 */
	void setStatusForProduct(Product product, ProductStatus productStatus);

	/**
	 * Удаляем статус для товара. В случае, если такой статус не установлен, ничего не происходит
	 *
	 * @param product
	 * @param productStatus
	 */
	void removeStatusForProduct(Product product, ProductStatus productStatus);

	Optional<ProductStatus> getByName(String name);
}
