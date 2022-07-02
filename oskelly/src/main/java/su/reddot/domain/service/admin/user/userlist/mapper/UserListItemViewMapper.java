package su.reddot.domain.service.admin.user.userlist.mapper;

import org.springframework.stereotype.Component;
import su.reddot.domain.dao.admin.UserProductsAndOrdersProjection;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.service.admin.user.userlist.view.UserListItemView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserListItemViewMapper {

	public UserListItemView mapUserProjection(List<UserProductsAndOrdersProjection> projections) {
		/*
		 * Все проекции одинаковы, за исключением числа товаров и статуса товара,
		 * поэтому для заполнения большинства полей ответа нам хватит и первой проекции
		 */
		UserProductsAndOrdersProjection firstProjection = projections.get(0);

		UserListItemView userListItemView = new UserListItemView();
		userListItemView.setId(firstProjection.getUserid().longValue());
		userListItemView.setNickname(firstProjection.getNickname());
		userListItemView.setEmail(firstProjection.getEmail());
		userListItemView.setPhone(firstProjection.getPhone());
		userListItemView.setOrders(firstProjection.getOrderscount().intValue());
		userListItemView.setOrderItems(firstProjection.getOrderitems().intValue());

		String firstName = firstProjection.getFirstname() != null ? firstProjection.getFirstname() : "%ИМЯ%";
		String lastName = firstProjection.getLastname() != null ? firstProjection.getLastname() : "%ФАМИЛИЯ%";
		userListItemView.setName(firstName + " " + lastName);


		// извлекает статусы пользователя
		List<String> statuses = new ArrayList<>();
		if (firstProjection.getIsPro() != null && firstProjection.getIsPro()) {
			statuses.add("PRO");
		}
		if (firstProjection.getIsVip() != null && firstProjection.getIsVip()) {
			statuses.add("VIP");
		}
		if (firstProjection.getIsTrusted() != null && firstProjection.getIsTrusted()) {
			statuses.add("Trusted");
		}
		userListItemView.setStatuses(statuses.stream().collect(Collectors.joining(", ")));

		//получаем списки товаров пользователя: опубликованные и на модерации
		int published = getProductsForUserProjection(projections, ProductState.PUBLISHED);
		userListItemView.setPublishedProducts(published);

		int moderated = getProductsForUserProjection(projections, ProductState.NEED_MODERATION);
		userListItemView.setModeratedProducts(moderated);
		return userListItemView;
	}

	private int getProductsForUserProjection(List<UserProductsAndOrdersProjection> projections, ProductState productState) {
		return projections.stream()
				.filter(p -> productState.name().equals(p.getProductstate()))
				.map(UserProductsAndOrdersProjection::getProducts)
				.findFirst().map(BigInteger::intValue).orElseGet(() -> 0);
	}
}
