package su.reddot.domain.dao.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.user.User;

import java.util.List;

public interface UserAdminRepository extends JpaRepository<User, Long> {

	/**
	 * <h4>ВАЖНО:</h4>
	 * <b>определение типа возвращаемого значения и столбцов в классе User</b>
	 * <p>
	 * Получаем данные для админки пользователей (см. UserProductsAndOrdersProjection).
	 * Если какой-либо из параметров процедуры TRUE, добавляется дополнительное условие к запросу
	 *
	 * @param isPro    пользователь имеет статус PRO
	 * @param isSeller пользователь в настоящее время имеет товары в состоянии PUBLISHED
	 * @param isNew    пользователь зарегистрировался в течении последней недели
	 * @param offset   отступ от начала выборки
	 * @param limit    ограничение на количество выбранных строк
	 * @return набор данных для админки пользователей
	 */
	List<UserProductsAndOrdersProjection> getUserStats(@Param("isPro") Boolean isPro,
	                                                   @Param("isSeller") Boolean isSeller,
	                                                   @Param("isNew") Boolean isNew,
	                                                   @Param("_offset") int offset,
	                                                   @Param("_limit") int limit);

	UserInfoProjection getUserInfoById(Long id);

	/**
	 * Получаем список всех пользователей с хотя бы одним правом модератора
	 */
	@Query("select u.id as userId,u.email as email, u.nickname as nickname, u.sellerRequisite.firstName as firstName, " +
			"u.sellerRequisite.lastName as lastName, u.avatarPath as avatarPath, a.name as authorityName, a.id as authorityId " +
			"from User u join u.userAuthorityBindings uab " +
			"join uab.authority a where a.type='MODERATOR' order by u.id")
	List<UserAuthorityProjection> getAllModeratorsWithTheirAuthorities();

	@Query("select u.id as id, u.email as email, " +
			"u.sellerRequisite.firstName as firstName, " +
			"u.sellerRequisite.lastName as lastName " +
			"from User u " +
			"left join u.userAuthorityBindings uab " +
			"left join uab.authority a on a.type='MODERATOR' " +
			"WHERE uab.authority IS NULL AND email LIKE :emailPart")
	List<UserEmailProjection> findTop15SimpleUsersByEmailPart(@Param("emailPart") String emailPart);
}
