package su.reddot.domain.dao.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import su.reddot.domain.model.notification.Notification;
import su.reddot.domain.model.user.User;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 18.09.17.
 */
public interface NotificationRepository<T extends Notification> extends JpaRepository<T, Long> {

	List<Notification> findTop100ByUserAndReadTimeIsNullOrderByCreateTimeDesc(User u);

	List<Notification> findTop100ByUserAndReadTimeIsNotNullOrderByCreateTimeDesc(User u);

	List<Notification> findTop100ByUserOrderByCreateTimeDesc(User u);

	Long countByUserAndReadTimeIsNull(User user);

	/**
     * Пометить все уведомления пользователя как прочитанные
	 * @param u
	 */
	@Modifying
	@Query("update #{#entityName} n set n.readTime = now() where user = ?1 and n.readTime is null")
	void readAll(User u);
}
