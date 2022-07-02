package su.reddot.presentation.view.product;

import lombok.*;
import su.reddot.domain.model.Brand;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BreadcrumbsView {

	private String current;
	private List<BreadcrumbCategory> categories;

	public BreadcrumbsView(Brand brand, String productName) {
		this.current = productName + " " + brand.getName();
	}

	public void addCategory(String name, String link) {
		if (categories == null) {
			this.categories = new ArrayList<>();
		}
		this.categories.add(new BreadcrumbCategory(name, link));
	}


	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class BreadcrumbCategory {
		private String name;
		private String link;
	}
}

