package su.reddot.presentation.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import su.reddot.domain.service.admin.moderation.ProductSizeMappingBaseRequest;
import su.reddot.domain.service.product.ProductSizeMapping;
import su.reddot.domain.service.product.ProductSizeMappingWithoutCount;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 07.07.17.
 */
@Component
public class ProductSizeMappingRequestValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return ProductSizeMappingBaseRequest.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ProductSizeMappingBaseRequest request = (ProductSizeMappingBaseRequest) target;
		request.getProductSizeMappings().forEach(m -> {
			if(m.getCount() == null) {
				errors.rejectValue("productSizeMappings", "Count cannot be null", "В одной из позиций не указано количество товара");
			}
			if(m.getPrice() == null) {
				errors.rejectValue("productSizeMappings", "Price cannot be null", "В одной из позиций не указана цена");
			}
			if(m.getSize() == null || m.getSize().getId() == null) {
				errors.rejectValue("productSizeMappings", "Size cannot be null", "В одной из позиций не выбран размер");
			}
		});
		if(errors.hasErrors()) {
			return;
		}
		request.getProductSizeMappings().forEach(m -> {
			if(m.getCount() < 0) {
				errors.rejectValue("productSizeMappings", "Negative count value existed", "Указано отрицательное количество товара: " + m.getCount().toString());
			}
			if(m.getCount() > 1000) {
				errors.rejectValue("productSizeMappings", "Too large count value", "Не больше 1000 позиций, пожалуйста");
			}
			if(m.getPrice().compareTo(BigDecimal.ZERO) == -1) {
				errors.rejectValue("productSizeMappings", "Negative price value existed", "Указана отрицательная цена: " + m.getPrice().toString());
			}
		});
		List<ProductSizeMappingWithoutCount> list = request.getProductSizeMappings().stream()
				.map(mapping -> new ProductSizeMappingWithoutCount(mapping.getSize(), mapping.getPrice()))
				.collect(Collectors.toList());
		if(!findDuplicates(list).isEmpty()) {
			errors.rejectValue("productSizeMappings", "Duplicates found", "Имеются повторяющиеся позиции");
		}
		if(!errors.hasErrors()) { // если нет ошибок, делаем самую последнюю проверку
			Long count = request.getProductSizeMappings().stream()
					.map(ProductSizeMapping::getCount)
					.reduce(0L, Long::sum);
			if (!request.isAllowNonePositions() && count <= 0) {
				errors.rejectValue("productSizeMappings", "Product must have at least one ProductItem", "Нет ни одной позиции товара");
			}
		}
	}

	private Set<ProductSizeMappingWithoutCount> findDuplicates(List<ProductSizeMappingWithoutCount> listContainingDuplicates) {
		final Set<ProductSizeMappingWithoutCount> setToReturn = new HashSet<>();
		final Set<ProductSizeMappingWithoutCount> setForChecking = new HashSet<>();
		for (ProductSizeMappingWithoutCount mapping : listContainingDuplicates) {
			if (!setForChecking.add(mapping)) {
				setToReturn.add(mapping);
			}
		}
		return setToReturn;
	}
}
