package su.reddot.domain.service.wishlist;

import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;

public interface WishListService {

    void addProduct(Long productId, Long userId) throws WishListException;

    void removeProduct(Long productId, Long userId) throws WishListException;

    /**
     * Добавить товар если его нет, удалить если он есть
     * @return true - если по завершению метода товар находится в WishList'e
     */
    boolean toggle(Long productId, Long userId);

    boolean hasProduct(Product product, User user);

    Long countByProduct(Product product);
}
