package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.AttractionOfTraffic;
import su.reddot.domain.model.user.User;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * @author Vitaliy Khludeev on 09.10.17.
 */
public interface AttractionOfTrafficRepository extends JpaRepository<AttractionOfTraffic, Long> {

	/**
	 * Получает первое привлечение, у которое еще не просрочено
	 * @param user
	 * @param dateTime
	 * @return
	 */
	Optional<AttractionOfTraffic> findFirstByUserAndExpireTimeGreaterThanOrderByExpireTime(User user, ZonedDateTime dateTime);

	/**
	 * Проверка на то что такое привлечение уже есть в БД
	 * @param cookie
	 * @param type
	 * @return
	 */
	boolean existsByCookieAndType(String cookie, AttractionOfTraffic.Type type);

	/**
	 * Проверяет использован ли какой-либо источник привлечения или нет
	 * @param user покупатель
	 * @return результат
	 */
	boolean existsByUserAndUseTimeIsNotNull(User user);
}
