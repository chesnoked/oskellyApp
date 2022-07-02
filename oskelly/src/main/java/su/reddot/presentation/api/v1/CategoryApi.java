package su.reddot.presentation.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryApi {

	private final CategoryService categoryService;

	@GetMapping("/{id}/childs")
	public ResponseEntity<?> getChildCategories(@PathVariable Long id){
		List<CatalogCategory> categories = categoryService.getDirectChildrenCategories(id);
		return ResponseEntity.ok(categories);
	}
}
