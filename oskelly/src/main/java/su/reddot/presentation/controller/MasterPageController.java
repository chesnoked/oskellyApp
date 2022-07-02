package su.reddot.presentation.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductService.FilterSpecification;
import su.reddot.domain.service.product.ProductService.ViewQualification;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.domain.service.promo.gallery.PromoGalleryService;
import su.reddot.domain.service.promo.selection.PromoSelectionService;
import su.reddot.infrastructure.security.SecurityService;

@Controller
@RequiredArgsConstructor
public class MasterPageController {
	private static final int PUBLISHED_ITEMS_SIZE = 15;

	private final PromoGalleryService   promoGalleryService;
	private final PromoSelectionService promoSelectionService;
	private final ProductService        productService;
	private final SecurityService       securityService;

	@GetMapping("/")
	public String getMasterPage(
			@RequestParam(name = "t", required = false) String passwordResetToken,
			Model model) {

		model.addAttribute("galleryItems", promoGalleryService.getAll());
		model.addAttribute("selectionItems", promoSelectionService.getIndexPromo());
		model.addAttribute("favoritesItems", promoSelectionService.getIndexFavorites());

		FilterSpecification spec = new FilterSpecification()
				.state(ProductState.PUBLISHED)
				.itemState(ProductItem.State.INITIAL);

		ProductsList productsList = productService.getProductsList(
				spec, 1, ProductService.SortAttribute.PUBLISH_TIME_DESC,
				new ViewQualification().pageLength(PUBLISHED_ITEMS_SIZE).withSavings(true));

		model.addAttribute("productList", productsList);
		model.addAttribute("count", productService.getLastWeekPublishedProductsCount());

		if (passwordResetToken == null) { return "new/main"; }

        try {
            securityService.assertTokenValidity(passwordResetToken);
			model.addAttribute("passwordResetRequest",
					new PasswordResetRequest(true, null));

        } catch (Exception e) {
			model.addAttribute("passwordResetRequest",
					new PasswordResetRequest(false, e.getMessage()));
        }

        return "new/main";
	}

	@RequiredArgsConstructor @Getter
	public static class PasswordResetRequest {

	    private final boolean isValid;

	    private final String reasonIfInvalid;
	}
}
