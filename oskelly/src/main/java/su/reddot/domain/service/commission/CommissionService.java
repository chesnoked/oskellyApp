package su.reddot.domain.service.commission;

import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.user.User;

import java.math.BigDecimal;

/**
 * @author Vitaliy Khludeev on 06.08.17.
 */
public interface CommissionService {


    /**
     * Расчет цены с комиссией для продавца и категории
     * Формула priceWithCommission = priceWithoutCommission / (1 - commission)
     *
     * @param priceWithoutCommission цена без комиссии
     * @param seller                 продавец
     * @param category               категория товара
     * @return цена с комиссией
     * @see CommissionService#calculateCommission(ProductItem)
     */
    BigDecimal calculatePriceWithCommission(BigDecimal priceWithoutCommission, User seller, Category category, boolean isTurbo, boolean isNewCollection) throws CommissionException;

    /**
     * Расчет цены с комиссией для продавца и категории
     * Формула priceWithCommission = priceWithoutCommission / (1 - commission)
     *
     * @param priceWithoutCommission цена без комиссии
     * @param sellerId               ID продавца
     * @param categoryId             ID категории товара
     * @return цена с комиссией
     * @see CommissionService#calculateCommission(ProductItem)
     */
    BigDecimal calculatePriceWithCommission(BigDecimal priceWithoutCommission, Long sellerId, Long categoryId, boolean isTurbo, boolean isNewCollection) throws CommissionException;


    /**
     * Расчет цены без комиссии для продавца и категории
     * Формула priceWithCommission = priceWithoutCommission / (1 - commission)
     *
     * @param priceWithCommission цена с космссией
     * @param seller               продавец
     * @param category             категория
     * @return цена с комиссией
     * @see CommissionService#calculateCommission(ProductItem)
     */
    BigDecimal calculatePriceWithoutCommission(BigDecimal priceWithCommission, User seller, Category category, boolean isTurbo, boolean isNewCollection) throws CommissionException;

    /**
     * Для заданных цены и комиссии получить цену без учета комиссии
     */
    BigDecimal calculatePriceWithoutCommission(BigDecimal priceWithCommission, BigDecimal notNullCommission);

    /**
     * Расчет комиссии для вещи
     *
     * @param productItem   вещь
     * @return комиссия в пределах от 0 до 1
     */
    BigDecimal calculateCommission(ProductItem productItem) throws CommissionException;
}
