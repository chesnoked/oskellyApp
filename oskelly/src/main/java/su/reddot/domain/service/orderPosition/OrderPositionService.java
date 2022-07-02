package su.reddot.domain.service.orderPosition;

import su.reddot.domain.model.order.OrderPosition;

import java.util.List;
import java.util.Optional;

/**
 * Сервис моих сделок (моих продаж)
 * @author Vitaliy Khludeev on 15.12.17.
 */
public interface OrderPositionService {

	/**
	 * Получить все продажи пользователя, доступные для отображения на сайте
	 * или в мобильном приложении
	 * @param sellerId идентификатор продавца
	 * @return список продаж
	 */
	List<SaleView> getUserSales(Long sellerId);

	/**
	 * Получить все продажи пользователя, доступные для отображения на сайте
	 * или в мобильном приложении, сгруппированные по статусам
	 * @param sellerId идентификатор продавца
	 * @return список продаж в сгруппированном виде
	 */
	List<SaleGroupView> getGroupedUserSales(Long sellerId);

	Optional<OrderPosition> getById(Long id);
}