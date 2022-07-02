package su.reddot.domain.service.like;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import su.reddot.domain.dao.like.BaseLikeRepository;
import su.reddot.domain.dao.like.LikeRepository;
import su.reddot.domain.dao.like.ProductLikeRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.model.like.Like;
import su.reddot.domain.model.like.Likeable;
import su.reddot.domain.model.notification.AddToFavouritesNotification;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.like.type.ToggleResult;
import su.reddot.domain.service.notification.NotificationPackage;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 14.09.17.
 */
@Component
@Slf4j
public class DefaultLikeService implements LikeService {

	private final Map<Class<? extends Likeable>, LikeRepository<? extends Like>> repositories = new HashMap<>();
	private final BaseLikeRepository baseLikeRepository;
	private final ProductRepository productRepository;
	private final ApplicationEventPublisher publisher;

	@Autowired
	public DefaultLikeService(ProductLikeRepository productLikeRepository, BaseLikeRepository baseLikeRepository, ProductRepository productRepository, ApplicationEventPublisher publisher) {
		this.baseLikeRepository = baseLikeRepository;
		this.productRepository = productRepository;
		this.publisher = publisher;
		repositories.put(Product.class, productLikeRepository);
	}

	@Override
	public boolean like(Likeable l, User u) {
		LikeRepository likeRepository = repositories.get(l.getClass());
		Like like = likeRepository.findByLikableAndUser(l, u);
		if(like != null) {
			return false;
		}
		like = Like.init(l)
				.setUser(u)
				.setCreateTime(ZonedDateTime.now());

		likeRepository.save(like);
		sendNotifications(like);
		return true;
	}

	private void sendNotifications(Like like) {
		AddToFavouritesNotification notification = new AddToFavouritesNotification().setLike(like);
		NotificationPackage<AddToFavouritesNotification> notificationPackage = new NotificationPackage<>(like.getUser(), notification);
		publisher.publishEvent(notificationPackage);

		publisher.publishEvent(new AddToFavouritesNotification().setLike(like).setUser(like.getUser()));
	}

	@Override
	public boolean dislike(Likeable l, User u) {
		LikeRepository likeRepository = repositories.get(l.getClass());
		Like like = likeRepository.findByLikableAndUser(l, u);
		if(like != null) {
			likeRepository.delete(like);
			return true;
		}
		return false;
	}

	@Override
	public ToggleResult toggle(Likeable l, User u) {
		Class realClassBehindProxyIfAny = HibernateProxyHelper.getClassWithoutInitializingProxy(l);
		LikeRepository likeRepository = repositories.get(realClassBehindProxyIfAny);

		Like usersLikeIfAny = likeRepository.findByLikableAndUser(l, u);
		if (usersLikeIfAny == null) {
			Like newLike = Like.init(l)
					.setUser(u)
					.setCreateTime(ZonedDateTime.now());

			likeRepository.save(newLike);
			sendNotifications(newLike);
		}
		else {
			likeRepository.delete(usersLikeIfAny);
		}

		int actualLikesCount = likeRepository.countByLikable(l);

		return new ToggleResult()
				.setCanBeLiked(usersLikeIfAny != null)
				.setActualLikesCount(actualLikesCount);
	}

	@Override
	public boolean doesUserLike(Likeable l, User u) {
		/* Likeable может быть скрыт за прокси классом (который иногда генерирует hibernate для обеспечения ленивой загрузки)
		* поэтому если не извлечь настоящий класс, то найти подходящий репозиторий по прокси классу не получится */
		Class realClassBehindProxyIfAny = HibernateProxyHelper.getClassWithoutInitializingProxy(l);
		LikeRepository likeRepository = repositories.get(realClassBehindProxyIfAny);
		if (u == null){ return false; }

		Like like = likeRepository.findByLikableAndUser(l, u);
		return like != null;
	}

	@Override
	public int countByLikeable(Likeable l) {
		LikeRepository likeRepository = repositories.get(l.getClass());

		return (likeRepository!= null)? likeRepository.countByLikable(l) : 0;
	}

	@Override
	public int countByAllUserLikeables(User u, Class<? extends Likeable> clazz) {
		LikeRepository likeRepository = repositories.get(clazz);
		return likeRepository.countByAllUserLikeables(u);
	}

	@Override
	public List<Likeable> getLikeablesWhichUserLiked(User u) {
		List<Like> likes = baseLikeRepository.findTop100ByUserOrderByIdDesc(u);
		return likes.stream().map(Like::getLikable).collect(Collectors.toList());
	}
	@Override
	public List<? extends Likeable> getLikeablesWhichUserLiked(User u, Class<? extends Likeable> cls) {
		LikeRepository likeRepository = repositories.get(cls);
        List<Like> likes = likeRepository.findTop100ByUserOrderByIdDesc(u);
        return likes.stream().map(Like::getLikable).collect(Collectors.toList());
	}

	@Override
	public int countByUser(User u) {
		LikeRepository likeRepository = repositories.get(Product.class);
		return likeRepository.countByAllUserLikeables(u);
	}
}
