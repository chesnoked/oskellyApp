package su.reddot.domain.service.following.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.following.FolloweeProjection;
import su.reddot.domain.dao.following.FollowerProjection;
import su.reddot.domain.dao.following.FollowingRepository;
import su.reddot.domain.model.Following;
import su.reddot.domain.model.notification.FollowingNotification;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.notification.NotificationPackage;
import su.reddot.domain.service.product.ProductService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 22.09.17.
 */
@Service
@RequiredArgsConstructor
public class DefaultFollowingService implements FollowingService {

	private final FollowingRepository repo;

	private final ApplicationEventPublisher publisher;

	@Value("${resources.images.urlPrefix}")
	private String imagesRoot;

	private ProductService productService;

	@Autowired /* Циклическая зависимость */
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}


	@Override
	public List<User> getFollowers(User u) {
		return repo.findByFollowing(u);
	}

	@Override
	public int getFollowersCount(User u) {
		return repo.countByFollowing(u);
	}

	@Override
	public List<User> getFollowings(User u) {
		return repo.findByFollower(u);
	}

	@Override
	public int getFollowingsCount(User u) {
		return repo.countByFollower(u);
	}

	@Override
	public boolean isFollowingExists(User follower, User following) {
		return repo.existsByFollowerAndFollowing(follower, following);
	}

	@Override
	public boolean follow(User follower, User following) {

		if (repo.existsByFollowerAndFollowing(follower, following)) return true;

		Following f = new Following()
				.setFollower(follower)
				.setFollowing(following);

		repo.save(f);

		publisher.publishEvent(
				new FollowingNotification()
						.setFollowing(f)
						.setUser(following)
		);

		publisher.publishEvent(
				new FollowingNotification()
						.setFollowing(f)
						.setUser(follower)
		);

		FollowingNotification notification = new FollowingNotification().setFollowing(f);
		NotificationPackage<FollowingNotification> notificationPackage = new NotificationPackage<>(f.getFollower(), notification);
		publisher.publishEvent(notificationPackage);

		return true;
	}

	@Override
	public boolean unfollow(User follower, User following) {
		Following existed = repo.findByFollowerAndFollowing(follower, following);
		if (existed == null) {
			return true;
		}
		repo.delete(existed);

		return true;
	}

	@Override
	public void toggle(User follower, User following) {
		Following existed = repo.findByFollowerAndFollowing(follower, following);
		if (existed == null) {
			follow(follower, following);
		}
		else {
			repo.delete(existed);
		}
	}

	@Override
	public GetBuilder getFor(User u) {
		return new GetBuilder(u);
	}

	@RequiredArgsConstructor
	class GetBuilder implements FollowingService.GetBuilder {

		private final User u;

		private boolean doGetFollowers;
		private boolean doGetFollowees;
		private User    arbitraryUser;

		@Override
		public int count() {

			return doGetFollowers ? repo.countFollowersByFollowing(u)
					: doGetFollowees ? repo.countFolloweesByFollower(u)
					: 0;
		}

		@Override
		public List<FollowRelated> build() {

			/* Можно получить либо подиски, либо подписчиков,
			 * но не обоих одновременно. */
			List<User> followeesOrFollowers = Collections.emptyList();

			if (doGetFollowers) {
			    followeesOrFollowers = repo.findFollowers(u).stream()
					.map(FollowerProjection::getFollower)
					.collect(Collectors.toList());
			}
			else if (doGetFollowees) {
				followeesOrFollowers = repo.findFollowees(u).stream()
						.map(FolloweeProjection::getFollowing)
						.collect(Collectors.toList());
			}

			List<FollowRelated> cooked = new ArrayList<>();

			for (User user : followeesOrFollowers) {

				FollowRelated composed = new FollowRelated()
						.setId(user.getId())
						.setNickname(user.getNickname())
						.setAvatarPath(user.getAvatarPath() != null? imagesRoot + user.getAvatarPath() : null)
						.setFollowersCount(getFor(user).followers().count())
						.setAvailableProductsCount(productService.getAvailableProductsCount(user));

				if (arbitraryUser == null) { cooked.add(composed); continue; }

				if (doGetFollowers) {
					/* на твоих подписчиков ты можешь как подписаться,
					так и отписаться, если уже на них подписан */
					boolean alreadyFollows = repo.existsByFollowerAndFollowing(arbitraryUser, user);
					if (alreadyFollows) {
						composed.setFollowingAvailable(false)
								.setUnfollowingAvailable(true);
					}
					else {
						composed.setFollowingAvailable(true)
								.setUnfollowingAvailable(false);
					}
				}
				else {
					/* от подписок можно только отписаться */
					composed.setFollowingAvailable(false)
							.setUnfollowingAvailable(true);
				}

				cooked.add(composed);
			}

			return cooked;
		}

		@Override
		public GetBuilder followers() { doGetFollowers = true; return this; }

		@Override
		public GetBuilder followees() { doGetFollowees = true; return this; }

		@Override
		public GetBuilder withFollowAvailability(User u) {
		    arbitraryUser = u; return this;
		}

	}
}
