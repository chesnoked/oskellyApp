package su.reddot.infrastructure.tilda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TildaPageRepository extends JpaRepository<TildaPage, Long> {

    Optional<TildaPage> findFirstByUrl(String url);

    Optional<TildaPage> findFirstByIsMainPageEquals(Boolean mainPage);

    Optional<TildaPage> findOneByTildaPageId(Long tildaPageId);
}
