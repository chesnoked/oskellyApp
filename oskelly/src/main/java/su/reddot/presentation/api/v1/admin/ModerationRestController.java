package su.reddot.presentation.api.v1.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import su.reddot.domain.model.enums.AuthorityName;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.admin.moderation.ModerationService;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.product.CatalogProductPage;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.propublication.ProPublicationService;
import su.reddot.domain.service.propublication.view.ProPublicationGridInfo;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 23.06.17.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
@RequestMapping("/admin/api/v1/moderation")
@Slf4j
public class ModerationRestController {

	private final ModerationService     moderationService;
	private final SpringTemplateEngine  templateEngine;
	private final ServletContext        context;
	private final UserService           userService;
	private final ProPublicationService proPublicationService;
	private final CategoryService       categoryService;

	@ModelAttribute
	public void checkAuthorities(UserIdAuthenticationToken token) {
		Optional<User> user = userService.getUserById(token.getUserId());
		if(!user.isPresent()) {
			throw new AccessDeniedException("Access denied");
		}
		else if(!user.get().isPro() && !token.hasAuthority(AuthorityName.PRODUCT_MODERATION)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	@GetMapping(value = "/products")
	public Products getProducts(
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(required = false) Optional<ProductState> state,
			@RequestParam(required = false) Optional<Boolean> pro,
			@RequestParam(required = false) Optional<Boolean> vip,
			@RequestParam(required = false) Optional<Boolean> newCollection,
			@RequestParam(required = false) Optional<Boolean> descriptionExists,
			@RequestParam(required = false) Optional<Long> brand,
			@RequestParam(required = false) Optional<Long> condition,
			@RequestParam(required = false) Optional<Long> category,
			@RequestParam(required = false) Optional<BigDecimal> startPrice,
			@RequestParam(required = false) Optional<BigDecimal> endPrice,
			@RequestParam(required = false) Optional<String> email,
			@RequestParam(required = false) String sort,
			HttpServletRequest request,
			HttpServletResponse response,
			UserIdAuthenticationToken token
	) {
		Optional<ProductService.SortAttribute> currentSort = ProductService.SortAttribute.of(sort);
		ProductService.FilterSpecification filterSpecification = new ProductService.FilterSpecification()
				.state(state.orElse(null))
				.isPro(pro.orElse(null))
				.isVip(vip.orElse(null))
				.isNewCollection(newCollection.orElse(null))
				.isDescriptionExists(descriptionExists.orElse(null))
				.sellerEmailSubstring(email.orElse(null))
				.startPrice(startPrice.orElse(null))
				.endPrice(endPrice.orElse(null))
				.sellerId(token.hasAuthority(AuthorityName.PRODUCT_MODERATION) ? null : token.getUserId());
		brand.ifPresent(l -> filterSpecification.interestingBrands(Collections.singletonList(l)));
		condition.ifPresent(l -> filterSpecification.interestingConditions(Collections.singletonList(l)));
		category.ifPresent(l -> filterSpecification.categoriesIds(Collections.singletonList(l)));
		CatalogProductPage catalogProductPage = moderationService.getCatalogProductPage(
				filterSpecification,
				page.orElse(1),
				currentSort.orElse(ProductService.SortAttribute.ID)
		);
		return new Products(
				renderProductsToHtml(catalogProductPage, request, response, context),
				catalogProductPage.getProductsTotalAmount(),
				catalogProductPage.getTotalPages());
	}

	private String renderProductsToHtml(CatalogProductPage productPage,
										HttpServletRequest request, HttpServletResponse response,
										ServletContext servletContext) {

		// Правильнее возвращать специально подготовленную для этого случая страницу
		if (productPage.getProducts().isEmpty()) { return "<h4>Нет товаров</h4>"; }

		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable("pageWithProducts", productPage);
		return templateEngine.process("admin/moderation/product_card",
				new HashSet<>(Collections.singletonList("[th:fragment='product_card (products)']")),
				context);
	}

	@GetMapping("/leafCategories")
	public String getCategoryLeafs(@RequestParam Long categoryId, HttpServletRequest request, HttpServletResponse response) {
		ProPublicationGridInfo publicationGridInfo = proPublicationService.getPublicationGridInfo(categoryId);
		WebContext webContext = new WebContext(request, response, this.context);
		webContext.setVariable("publicationGridInfo", publicationGridInfo);
		return templateEngine.process("admin/moderation/categoriesSelect",
				webContext);
	}

	@GetMapping("/attributes")
	public String getAttributes(@RequestParam Long categoryId, HttpServletRequest request, HttpServletResponse response) {
		ProPublicationGridInfo publicationGridInfo = proPublicationService.getPublicationGridInfo(categoryId);
		WebContext webContext = new WebContext(request, response, this.context);
		webContext.setVariable("publicationGridInfo", publicationGridInfo);
		return templateEngine.process("admin/moderation/attributesBlock",
				webContext);
	}

	@GetMapping("/sizes")
	public String getSizes(@RequestParam Long categoryId,
						   @RequestParam String sizeType,
						   HttpServletRequest request,
						   HttpServletResponse response
	) {
		SizeType sizeTypeEnum = SizeType.valueOf(sizeType);
		List<CatalogSize> sizesGroupedBySizeType = categoryService.getSizesGroupedBySizeType(categoryId);
		Optional<CatalogSize> catalogSizeOpt = sizesGroupedBySizeType.stream().filter(s -> s.getSizeType().equals(sizeTypeEnum)).findFirst();
		WebContext webContext = new WebContext(request, response, this.context);
		webContext.setVariable("sizes", catalogSizeOpt.map(CatalogSize::getValues).orElseGet(Collections::emptyList));
		return templateEngine.process("admin/moderation/sizesBlock",
				webContext);
	}

	@Getter @Setter @AllArgsConstructor
	private static class Products {
		private String content;
		private Long totalProducts;
		private int totalPages;
	}
}
