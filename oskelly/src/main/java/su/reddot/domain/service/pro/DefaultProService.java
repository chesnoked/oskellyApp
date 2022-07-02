package su.reddot.domain.service.pro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.product.CatalogProductPage;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductService.ViewQualification;
import su.reddot.presentation.view.Pageable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 31.07.17.
 */
@Service
@RequiredArgsConstructor
public class DefaultProService implements ProService {

	private final ProductService productService;
	private final ImageService imageService;

	@Override
	public SellerInfoResponse getSellerInfo(User user, Integer page) {

		SellerInfoResponse sellerInfoResponse = new SellerInfoResponse()
				.setUserId(user.getId())
				.setNick(user.getNickname())
				.setFullName(user.getFullName().orElse(null))
				.setStatus(user.isPro() ? "PRO" : null);

		CatalogProductPage catalogProductPage = productService.getProducts(
				new ProductService.FilterSpecification().sellerId(user.getId()),
				page,
				ProductService.SortAttribute.ID,
				new ViewQualification().interestingUser(user)
		);

		List<SellerInfoResponse.Product> products = catalogProductPage.getProducts().stream()
				.map(product -> {

					Product p = product.getProduct();
					List<SellerInfoResponse.Image> images = imageService.getProductImages(p).stream()
							.map(i -> new SellerInfoResponse.Image()
									.setId(i.getId())
									.setOrder(i.getPhotoOrder()))
							.collect(Collectors.toList());

					return new SellerInfoResponse.Product()
							.setProductId(p.getId())
							.setVendorCode(p.getVendorCode())
							.setDescription(p.getDescription())

							.setBrand(new SellerInfoResponse.Brand()
									.setBrandId(p.getBrand().getId())
									.setBrandName(p.getBrand().getName()))

							.setCategory(new SellerInfoResponse.Category()
									.setCategoryId(p.getCategory().getId())
									.setCategoryName(p.getCategory().getDisplayName()))

							.setSizeType(p.getSizeType() != null?
									p.getSizeType().getDescription()
									: "Не задан")

							.setImages(images);
				})
				.collect(Collectors.toList());

		sellerInfoResponse.setProducts(new Pageable<SellerInfoResponse.Product>()
				.setTotalPagesCount(catalogProductPage.getTotalPages())
				.setTotalItemsCount(catalogProductPage.getProductsTotalAmount())
				.setItems(products));

		return sellerInfoResponse;
	}
}
