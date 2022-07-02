package su.reddot.domain.service.commission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.CommissionRepository;
import su.reddot.domain.dao.category.CategoryRepository;
import su.reddot.domain.model.Commission;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.user.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 06.08.17.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultCommissionService implements CommissionService {

	private final static int SCALE = 5;

	private final CommissionRepository commissionRepository;

	private final UserService userService;

	private final CategoryRepository categoryRepository;

	private Optional<Commission> findUserCommission(User user, Category category) throws CommissionException {
		return commissionRepository.findFirstByUserAndCategoryAndType(user, category, Commission.Type.USER);
	}

	private Optional<Commission> findCategoryCommission(Category category) throws CommissionException {
		return commissionRepository.findFirstByCategoryAndType(category, Commission.Type.CATEGORY);
	}

	private Commission findStandardCommission(BigDecimal price, boolean isPro) throws CommissionException {
		Optional<Commission> commission;
		Commission.Type commissionType = isPro ? Commission.Type.PRO_STANDARD : Commission.Type.STANDARD;
		if(price != null) {
			commission = commissionRepository.findByTypeAndPriceBetween(commissionType, price);
		}
		else {
			commission = commissionRepository.findFirstByType(commissionType);
		}
		return commission.orElseThrow(
				() -> new CommissionException(commissionType.name() + " commission not found"));
	}

	private Commission findTurboCommission() throws CommissionException {
		Optional<Commission> commission = commissionRepository.findFirstByType(Commission.Type.TURBO);
		return commission.orElseThrow(
				() -> new CommissionException(Commission.Type.TURBO.name() + " commission not found"));
	}

	private Commission findNewCollectionCommission() throws CommissionException {
		Optional<Commission> commission = commissionRepository.findFirstByType(Commission.Type.NEW_COLLECTION);
		return commission.orElseThrow(
				() -> new CommissionException(Commission.Type.NEW_COLLECTION.name() + " commission not found"));
	}

	private BigDecimal calculateCommission(User seller, Category category, boolean isTurbo, boolean isNewCollection, BigDecimal price) throws CommissionException {

//		if(seller.isPro() && isNewCollection) {
//			return findNewCollectionCommission().getValue();
//		}
//		else
		if(!seller.isPro() && isNewCollection) {
			throw new CommissionException("Комиссия NEW_COLLECTION недоступна для обычных пользователей");
		}

		if(!seller.isPro() && isTurbo) {
			return findTurboCommission().getValue();
		}
		else if(seller.isPro() && isTurbo) {
			throw new CommissionException("Комиссия TURBO недоступна для PRO-пользователей");
		}

		Optional<Commission> userCommission = findUserCommission(seller, category);
		if(userCommission.isPresent()) {
			return userCommission.get().getValue();
		}

		Optional<Commission> categoryCommission = findCategoryCommission(category);
		if(categoryCommission.isPresent()) {
			return categoryCommission.get().getValue();
		}

		return findStandardCommission(price, seller.isPro()).getValue();
	}

	@Override
	public BigDecimal calculatePriceWithCommission(BigDecimal priceWithoutCommission, User seller, Category category, boolean isTurbo, boolean isNewCollection) throws CommissionException {
		if(!seller.isPro()) {
			throw new CommissionException("Для обычного пользователя цена с комиссией не может быть расчитана");
		}
		BigDecimal commission = calculateCommission(seller, category, isTurbo, isNewCollection, priceWithoutCommission);
		return priceWithoutCommission
				.divide(
						BigDecimal.ONE.subtract(commission),
						SCALE,
						RoundingMode.UP
				);
	}

	@Override
	public BigDecimal calculatePriceWithCommission(BigDecimal priceWithoutCommission, Long sellerId, Long categoryId, boolean isTurbo, boolean isNewCollection) throws CommissionException {
		User seller = getSeller(sellerId);
		Category category = getCategory(categoryId);
		return calculatePriceWithCommission(priceWithoutCommission, seller, category, isTurbo, isNewCollection);
	}

	@Override
	public BigDecimal calculatePriceWithoutCommission(BigDecimal priceWithCommission, User seller, Category category, boolean isTurbo, boolean isNewCollection) throws CommissionException {
		BigDecimal commission = calculateCommission(seller, category, isTurbo, isNewCollection, priceWithCommission);
		return calculatePriceWithoutCommission(priceWithCommission, commission);
	}

	@Override
    public BigDecimal calculatePriceWithoutCommission(BigDecimal priceWithCommission,
													  BigDecimal notNullCommission) {

		return priceWithCommission.multiply(BigDecimal.ONE.subtract(notNullCommission))
				.setScale(SCALE, RoundingMode.DOWN);
    }

	@Override
	public BigDecimal calculateCommission(ProductItem productItem) throws CommissionException {
		User seller = productItem.getProduct().getSeller();
		Category category = productItem.getProduct().getCategory();
		Boolean isNewCollection = productItem.getProduct().isNewCollection();
		Boolean isTurbo = productItem.getProduct().isTurbo();
		/*
		  	обычный физик никогда не вводит цену без комиссии
		  	прошник никогда не вводит цену с комиссией
		 */
		return calculateCommission(seller, category, isTurbo, isNewCollection, seller.isPro() ? productItem.getCurrentPriceWithoutCommission() : productItem.getCurrentPrice());
	}

	private Category getCategory(Long categoryId) throws CommissionException {
		Category category = categoryRepository.findOne(categoryId);
		if(category == null) {
			throw new CommissionException("Category with id: " + categoryId + " not found");
		}
		return category;
	}

	private User getSeller(Long sellerId) throws CommissionException {
		Optional<User> seller = userService.getUserById(sellerId);
		if(!seller.isPresent()) {
			throw new CommissionException("User with id: " + sellerId + " not found");
		}
		return seller.get();
	}
}
