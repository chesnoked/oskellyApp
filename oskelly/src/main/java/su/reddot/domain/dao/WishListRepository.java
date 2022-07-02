package su.reddot.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.WishList;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    boolean existsByProductAndUser(Product product, User user);

    void deleteByProductAndUser(Product product, User user);

    Long countAllByProduct(Product product);

    List<WishList> findAllProductByUser(User user);
}
