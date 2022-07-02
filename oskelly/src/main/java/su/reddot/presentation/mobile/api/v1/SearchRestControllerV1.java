package su.reddot.presentation.mobile.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.domain.service.product.ProductService;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 02.09.17.
 */
@RestController
@RequestMapping("/mobile/api/v1/search")
@PreAuthorize("isFullyAuthenticated()")
@RequiredArgsConstructor
public class SearchRestControllerV1 {

	private final ProductService productService;

	@GetMapping
	public List<ProductService.FullTextSearchProductView> search(@RequestParam String query) {
		//FIXME захардкодил limit
		return productService.findProductsByFullTextSearch(query,500);
	}
}
