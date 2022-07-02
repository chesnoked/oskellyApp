package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su.reddot.domain.model.Authority;
import su.reddot.domain.model.user.User;
import su.reddot.domain.model.user.UserAuthorityBinding;

import java.util.List;

public interface UserAuthorityBindingRepository extends JpaRepository<UserAuthorityBinding, Long> {

    @Query("select u.authority from UserAuthorityBinding u where u.user = :user")
    List<Authority> findByUser(@Param("user") User user);
}
