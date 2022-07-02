package su.reddot.domain.service.product.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.product.CatalogProductPage;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductService.ViewQualification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 13.08.17.
 */
@Component
@RequiredArgsConstructor
public class DefaultProductApiService implements ProductApiService {

	private final CategoryService categoryService;

	private final ProductService productService;

	@Override
	public CatalogProductPage getCatalogProductPage(ProductRequest request, User nullableUser) {

		Optional<ProductService.SortAttribute> currentSort
				= ProductService.SortAttribute.of(request.getSort());

		List<Long> productCategories = request.getCategory() != null?
				categoryService.getLeafCategoriesIds(request.getCategory())
				: Collections.emptyList();

		ProductService.FilterSpecification spec
				= new ProductService.FilterSpecification()
				.categoriesIds(productCategories)
				.state(request.getState() == null? ProductState.PUBLISHED : request.getState())
				.itemState(ProductItem.State.INITIAL)
				.interestingAttributeValues(request.getFilter())
				.interestingSizes(request.getSize())
				.startPrice(request.getStartPrice())
				.endPrice(request.getEndPrice())
				.sellerId(request.getSeller())
				.interestingConditions(request.getProductCondition())
				.interestingBrands(request.getBrand());

        ProductService.SortAttribute sortAttribute = currentSort.orElse(ProductService.SortAttribute.PUBLISH_TIME_DESC);

		ViewQualification viewSettings = new ViewQualification()
				.interestingUser(nullableUser)
				.interestingSizeType(request.getSizeType())
				.withSavings(true);

		return productService.getProducts(spec,
				request.getPage() == null? 1 : request.getPage(),
				sortAttribute, viewSettings);
	}
}
