package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import su.reddot.domain.model.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {

	User findByEmail(String email);

	User findByFacebookId(String facebookId);

	User findByNickname(String nickname);

	Optional<User> findByPasswordResetTokenValue(String t);
}
