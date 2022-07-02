package su.reddot.domain.service.product.status;

import org.springframework.stereotype.Service;
import su.reddot.domain.dao.product.ProductStatusBindingRepository;
import su.reddot.domain.dao.product.ProductStatusRepository;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductStatus;
import su.reddot.domain.model.product.ProductStatusBinding;

import java.util.Optional;

@Service
public class DefaultProductStatusService implements ProductStatusService {
	private final ProductStatusBindingRepository productStatusBindingRepository;
	private final ProductStatusRepository productStatusRepository;

	public DefaultProductStatusService(ProductStatusBindingRepository productStatusBindingRepository, ProductStatusRepository productStatusRepository) {
		this.productStatusBindingRepository = productStatusBindingRepository;
		this.productStatusRepository = productStatusRepository;
	}

	@Override
	public void setStatusForProduct(Product product, ProductStatus productStatus) {
		Optional<ProductStatusBinding> binding = productStatusBindingRepository.findByProductAndProductStatus(product, productStatus);
		if (!binding.isPresent()) {
			ProductStatusBinding newBinding = new ProductStatusBinding();
			newBinding.setProduct(product);
			newBinding.setProductStatus(productStatus);
			productStatusBindingRepository.save(newBinding);
		}
	}

	@Override
	public void removeStatusForProduct(Product product, ProductStatus productStatus) {
		Optional<ProductStatusBinding> binding = productStatusBindingRepository.findByProductAndProductStatus(product, productStatus);
		if (binding.isPresent()) {
			productStatusBindingRepository.delete(binding.get());
		}
	}

	@Override
	public Optional<ProductStatus> getByName(String name) {
		return productStatusRepository.findByName(name);
	}
}
