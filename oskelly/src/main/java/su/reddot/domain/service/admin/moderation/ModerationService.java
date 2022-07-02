package su.reddot.domain.service.admin.moderation;

import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.product.CatalogProductPage;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductSizeMapping;
import su.reddot.domain.service.publication.exception.PublicationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 22.06.17.
 */
public interface ModerationService {

	CatalogProductPage getCatalogProductPage(int pageNumber);

	CatalogProductPage getCatalogProductPage(int pageNumber, Long sellerId);

	CatalogProductPage getCatalogProductPage(int pageNumber, ProductState productState, Boolean isPro, Boolean isVip);

	CatalogProductPage getCatalogProductPage(int pageNumber, ProductState productState, Boolean isPro, Boolean isVip, Long sellerId);

	CatalogProductPage getCatalogProductPage(ProductService.FilterSpecification filterSpecification, int pageNumber, ProductService.SortAttribute sortAttribute);

	Optional<DetailedProduct> getDetailedProduct(Long productId);

	void updateAttributesAndCategory(Product product, Long categoryId, List<Long> attributeValues) throws PublicationException;

	void updateMarks(Long productId, Boolean vintage, Boolean ourChoice, boolean isNewCollection);

	void updateMarks(Long productId, Boolean vintage, boolean isNewCollection);

	void updateBrand(Long productId, Long brandId);

	void updateRrpPrice(Long productId, BigDecimal rrp);

	void updateVendorCode(Long productId, String vendorCode);

	void updateModel(Long productId, String model);

	void updateProductItemsCount(Long productId, List<ProductSizeMapping> productSizeMappings) throws CommissionException;
}
