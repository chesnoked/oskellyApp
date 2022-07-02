package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.Authority;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	List<Authority> findByType(Authority.AuthorityType type);
}
