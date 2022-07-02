package su.reddot.presentation.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.presentation.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sizes")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Slf4j
public class SizeApi {

	private final CategoryService categoryService;

	@GetMapping
	public ResponseEntity<?> loadSizesForCategoryAndSizeType(@RequestParam Long categoryId,
															 @RequestParam String sizeType) {
		SizeType sizeTypeEnum;
		try {
			sizeTypeEnum = SizeType.valueOf(sizeType);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(Utils.fieldError("Размер", "Некорректный тип размера"));
		}

		List<CatalogSize> sizesGroupedBySizeType = categoryService.getSizesGroupedBySizeType(categoryId);
		Optional<CatalogSize> catalogSizeOpt = sizesGroupedBySizeType.stream().filter(s -> s.getSizeType().equals(sizeTypeEnum)).findFirst();
		return ResponseEntity.ok(catalogSizeOpt.map(CatalogSize::getValues).orElseGet(Collections::emptyList));
	}
}
