package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductViewEvent.ProductModelEvent;
import su.reddot.domain.service.product.view.ProductCard;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.view.product.ProductView;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;
	private final BrandService brandService;
	private final UserService userService;

	private final ApplicationEventPublisher pub;

	@GetMapping("/{id}")
	public String getProductById(@PathVariable("id") Long id, Model model,
								 UserIdAuthenticationToken token,
								 @CookieValue(required = false) String osk)
			throws NotFoundException {

		Optional<User> thisUser = Optional.empty();

		if (token != null && token.getUserId() != null) {
			thisUser = userService.getUserById(token.getUserId());
		}

		Optional<ProductView> productView = productService.getProductView(id, thisUser.orElse(null));

		if (productView.isPresent()) {
			Boolean canUserEditProduct = productService.canUserEditProduct(id, thisUser.orElse(null));

			Product product = productService.getRawProduct(id).get();
			HashMap<String, Object> protoModel = new HashMap<>();
			/* заполнить модель данными о товаре */
			pub.publishEvent(new ProductModelEvent(product, thisUser.orElse(null), osk, protoModel));

			String searchQuery = productView.get().getBrand() + " " + productView.get().getName();

			model.addAttribute("sizeChart",productService.getSameProductsSizeChart(id));
			model.addAttribute("product", productView.get());
			model.addAttribute("searchResult", productService.findProductsByFullTextSearch(searchQuery,10));
			model.addAttribute("sellerProductsCount",productService.countBySeller(product.getSeller()));
			model.addAttribute("sellerFewProducts", productService.findSomeBySeller(product.getSeller(),3));
			model.addAttribute("allBrands", brandService.getAll());
			model.addAttribute("canUserEditProduct", canUserEditProduct);

			model.mergeAttributes(protoModel);

			return "product/page";
		}
		else {
			throw new NotFoundException("404:  /products/"+ id.toString() + " are not found");
		}
	}

	@GetMapping
	public String searchProducts(@RequestParam String search, Model m) {
		if (search.isEmpty()) { return "catalog/search-results"; }
		//fixme захардкодил limit
		List<ProductCard> productsFound = productService.findProducts(search, 500);
		m.addAttribute("products", productsFound);
		m.addAttribute("query", search);

		return "catalog/search-results";
	}
}
