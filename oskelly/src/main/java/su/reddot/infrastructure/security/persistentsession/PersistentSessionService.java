package su.reddot.infrastructure.security.persistentsession;

public interface PersistentSessionService {

	/**
	 * Обновить SecurityContext (authorization) пользователя актуальными значениями из БД.
	 * В случае, если securityContext хранится персистентно (в каком-либо хранилище), этот метод необходимо вызывать
	 * после изменения прав пользователя.
	 *
	 * @param userId
	 */
	void updateUserSecurityContext(Long userId);
}
