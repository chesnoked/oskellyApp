package su.reddot.domain.service.wishlist;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.dao.WishListRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.model.WishList;
import su.reddot.domain.model.notification.AddToWishListNotification;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.notification.NotificationPackage;

@Service
@RequiredArgsConstructor
public class DefaultWishListService implements WishListService {

    private final ProductRepository productRepository;
    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    public void addProduct(Long productId, Long userId) throws WishListException {
        Product product = productRepository.findOne(productId);
        User user = userRepository.findOne(userId);

        boolean productIsAlreadyInWithList = wishListRepository.existsByProductAndUser(product, user);
        if (productIsAlreadyInWithList) {
            throw new WishListException("Товар уже добавлен в WishList");
        }

        addProduct(product, user);
    }

    @Override
    @Transactional
    public void removeProduct(Long productId, Long userId) throws WishListException {
        Product product = productRepository.findOne(productId);
        User user = userRepository.findOne(userId);
        wishListRepository.deleteByProductAndUser(product, user);
    }

    @Override
    @Transactional
    public boolean toggle(Long productId, Long userId) {
        Product product = productRepository.findOne(productId);
        User user = userRepository.findOne(userId);
        boolean productIsAlreadyInWithList = wishListRepository.existsByProductAndUser(product, user);
        if(!productIsAlreadyInWithList) {
            addProduct(product, user);
            return true;
        }
        else {
            wishListRepository.deleteByProductAndUser(product, user);
            return false;
        }
    }

    @Override
    public boolean hasProduct(Product product, User user) {
        return wishListRepository.existsByProductAndUser(product, user);
    }

    @Override
    public Long countByProduct(Product product) {
        return wishListRepository.countAllByProduct(product);
    }

    private void addProduct(Product product, User user) {
        WishList wishList = new WishList();
        wishList.setProduct(product);
        wishList.setUser(user);
        wishListRepository.save(wishList);

        AddToWishListNotification notification = new AddToWishListNotification().setWishList(wishList);
        NotificationPackage<AddToWishListNotification> notificationPackage = new NotificationPackage<>(wishList.getUser(), notification);
        publisher.publishEvent(notificationPackage);

        publisher.publishEvent(new AddToWishListNotification().setWishList(wishList).setUser(wishList.getUser()));
    }
}
