package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import su.reddot.domain.service.product.ProductService;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 02.09.17.
 */
@Controller
@RequestMapping(value = "/search")
@RequiredArgsConstructor
public class SearchController {

	private final ProductService productService;

	@GetMapping
	@ResponseBody
	public List<ProductService.FullTextSearchProductView> search(@RequestParam String query) {
		//FIXME захардкодил limit
		return productService.findProductsByFullTextSearch(query,450);
	}
}
