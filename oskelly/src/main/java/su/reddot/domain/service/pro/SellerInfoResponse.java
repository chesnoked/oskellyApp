package su.reddot.domain.service.pro;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.presentation.view.Pageable;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 30.07.17.
 */
@Getter
@Setter
@Accessors(chain = true)
public class SellerInfoResponse {
	Long userId;
	String nick;
	String fullName;
	String status;
	Pageable<Product> products;

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Product {
		Long productId;
		String vendorCode;
		Category category;
		Brand brand;
		String description;
		String sizeType;
		List<Image> images;
	}

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Category {
		Long categoryId;
		String categoryName;
	}

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Brand {
		Long brandId;
		String brandName;
	}

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Image {
		Long id;
		Integer order;
	}
}
