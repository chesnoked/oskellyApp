package su.reddot.domain.service.profile;

import com.google.common.base.Strings;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.dao.WishListRepository;
import su.reddot.domain.dao.product.ProductItemRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.dao.subscription.PriceUpdateSubscriptionRepository;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.order.OrderState;
import su.reddot.domain.model.product.*;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.subscription.ProductAlertAttributeValueBinding;
import su.reddot.domain.model.subscription.ProductAlertSubscription;
import su.reddot.domain.model.user.PaymentRequisite;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.commission.CommissionService;
import su.reddot.domain.service.following.FollowingService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.like.LikeService;
import su.reddot.domain.service.offer.OfferService;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.domain.service.product.ItemStateChange;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductService.FilterSpecification;
import su.reddot.domain.service.product.ProductService.SortAttribute;
import su.reddot.domain.service.product.ProductService.ViewQualification;
import su.reddot.domain.service.product.view.ProductCard;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.domain.service.profile.view.OffersByProduct;
import su.reddot.domain.service.profile.view.ProductSubscription;
import su.reddot.domain.service.profile.view.SoldProduct;
import su.reddot.domain.service.subscription.SubscriptionService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.util.FileUtils;
import su.reddot.presentation.Utils;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultProfileService implements ProfileService {

	private final UserService         userService;
	private final ProductService      productService;
	private final LikeService         likeService;
	private final FollowingService    followingService;
	private final OfferService        offerService;
	private final OrderService        orderService;
	private final CommissionService   commissionService;
	private final SubscriptionService subscriptionService;

	private final ProductRepository productRepository;
	private final ProductItemRepository productItemRepository;
	private final WishListRepository wishListRepository;
	private final PriceUpdateSubscriptionRepository priceUpdateSubscriptionRepository;

	@Value("${resources.images.pathToDir}")
	@Setter
	private String imageBaseDirPath;

	@Value("${resources.images.urlPrefix}")
	@Setter
	private String urlPrefix;

	@Override
	public ProfileView getProfileView(Long userId) {
		User user = userService.getUserById(userId).get();
		return new ProfileView(
				user,
				urlPrefix,
				//FIXME  Сейчас не верно! надо перенести в сервис и сдлеать метод расчета по всем сосояниям (от продажа подтверждена, до доставленно пользователю)
				productItemRepository.countAllByProductSellerAndState(user, ProductItem.State.SALE_CONFIRMED),
				productRepository.countProductBySellerAndProductState(user, ProductState.PUBLISHED),
				followingService.getFollowersCount(user),
				followingService.getFollowingsCount(user),
				likeService.countByUser(user)
		);
	}

	@Override
	public ProductsList getCatalogProductPage(Long sellerId, ProductState productState, Long nullableViewingUserId) {

		//noinspection ConstantConditions если token не null, то скорее всего и пользователь с таким токеном есть
		User viewingUser = nullableViewingUserId != null?
				userService.getUserById(nullableViewingUserId).get()
				: null;

		FilterSpecification spec = new FilterSpecification()
				.state(productState).sellerId(sellerId);

		return productService.getProductsList(spec, 1, SortAttribute.PUBLISH_TIME_DESC,
				new ViewQualification().interestingUser(viewingUser));
	}

	@Override
	public List<ProductCard> getMyWishList(Long userId) {
		Optional<User> nullablelUser = userService.getUserById(userId);
		if(! nullablelUser.isPresent()) {return null; }
		User user = nullablelUser.get();

		List<Product> rawProducts = wishListRepository.findAllProductByUser(user).stream()
				.map(wishList -> wishList.getProduct())
				.collect(toList());

		return productService.getProductCardsByProducts(rawProducts, user);
	}

	@Override
	public List<ProductCard> getMyFavorites(Long userId) {
		Optional<User> nullableUser = userService.getUserById(userId);
		if(! nullableUser.isPresent()) {return null; }

		User user = nullableUser.get();

		List<Product> rawProducts = (List<Product>) likeService.getLikeablesWhichUserLiked(user, Product.class);
		return productService.getProductCardsByProducts(rawProducts, user);
	}

	@Override
	public List<ProductCard> getMyPriceSubscriptions(Long userId) {
		Optional<User> nullablelUser = userService.getUserById(userId);
		if(! nullablelUser.isPresent()) {return null; }
		User user = nullablelUser.get();

		/* FIXME среди товаров могут быть те, которые на данный момент недоступны для покупки.
		 * Такие товары нужно отмечать отдельно от остальных. */
		List<Product> rawProducts = priceUpdateSubscriptionRepository.getAllBySubscriber(user).stream()
				.map(sub -> sub.getProduct())
				.collect(toList());
		return  productService.getProductCardsByProducts(rawProducts, user);

	}

	@Override
	public boolean readingUserFollowProfile(Long profileId, Long nullableUserId) {
		Optional<User> profile = userService.getUserById(profileId);
		Optional<User> follower = (nullableUserId != null ? userService.getUserById(nullableUserId) : Optional.empty());
		if(profile.isPresent() && follower.isPresent()) {
			return followingService.isFollowingExists(follower.get(), profile.get());
		} else return false;
	}

	@Override
	public List<OffersByProduct> getOffersFrom(User buyer) {

		List<Offer> rawOffers = offerService.get().fromBuyer(buyer).build();

		List<OffersByProduct> cookedOffers = new ArrayList<>();
		rawOffers.stream()

		   		/* группировать по товару */
				.collect(Collectors.groupingBy(Offer::getProduct))
				.entrySet().stream()

                /* товар, у которого первый оффер - самый новый, идет первее других товаров */
				.sorted(Map.Entry.comparingByValue(Comparator.comparing(
						(List<Offer> offers) -> offers.get(0).getCreatedAt()).reversed()))

				.forEach(entry -> cookedOffers.add(from(entry.getKey(), entry.getValue())));

		return cookedOffers;
	}

	@Override
	public List<OffersByProduct> getOffersTo(User seller) {

		List<OffersByProduct> unorderedOffersByProduct = new ArrayList<>();

		offerService.get().forSeller(seller).build().stream()

                /* офферы, сгруппированные по товару и покупателю:
                *  {
                *   	товар_1 => {
                *  			 покупатель_1 => [ оффер_1, оффер_2 ... ],
                *  			 покупатель_2 => [оффер_3, оффер_4 ... ]
                *   	},
                *
                *  		товар_2 =>	 {
                *  			 покупатель_1 => [ оффер_5, оффер_6 ... ],
                *  			 покупатель_2 => [оффер_7, оффер_8 ... ]
                *  		},
                *  }
                **/
				.collect(Collectors.groupingBy(Offer::getProduct, Collectors.groupingBy(Offer::getOfferor)))

                /* Преобразовать вышеуказанное дерево в список, который можно
                будет относительно просто (по сравнению с деревом ) вывести на странице офферов.
                	[
                	{ товар_1, покупатель_1, [офферы покупателя 1 по товару 1] },
                	{ товар_2, покупатель_1, [офферы покупателя 1 по товару 2] },
                	{ товар_1, покупатель_2, [офферы покупателя 2 по товару 1] },
                	{ товар_2, покупатель_2, [офферы покупателя 2 по товару 2] },
                	]

                Такая трансформация неоптимальна с точки зрения потребления памяти,
                так как приводит к дублированию элементов товаров и покупателей.
                */
				.forEach((product, offersByUser) -> offersByUser.values()
						.forEach(offers -> unorderedOffersByProduct.add(from(product, offers))));

				/* упорядочить элементы вышеуказанного списка согласно требованиям задачи:
				* нужно, чтобы первым в списке шел торг с тем покупателем и по тому товару,
				* первый оффер на который этот покупатель прислал позже остальных первых офферов по этому товару
				* от других покупателей.
				**/
		unorderedOffersByProduct.sort(Comparator.comparing((OffersByProduct offersByProduct) ->
				offersByProduct.getOffers().get(0).getCreatedAt()).reversed());

		/* офферы упорядочены, несмотря на название переменной */
		return unorderedOffersByProduct;
	}

	@Override
	public Optional<OffersByProduct> getOffersTo(User seller, Long id) {

		List<Offer> offerIfAny = offerService.get()
				.forSeller(seller)
				.withId(id)
				.build();

		if (offerIfAny.isEmpty()) return Optional.empty();

		return Optional.of(
				from(offerIfAny.get(0).getProduct(), offerIfAny));
	}

	@Override @Transactional
	public void dropPriceTracking(Long productId, User u) {
		priceUpdateSubscriptionRepository.deleteByProductIdAndSubscriber(productId, u);
	}

    @Override
    public List<SoldProduct> getProductsToConfirm(User u) {

	    List<SoldProduct> cookedProducts = new ArrayList<>();

		QProductItem item = QProductItem.productItem;

		BooleanExpression predicate = item.product.seller.eq(u)
				.and(item.state.eq(ProductItem.State.PURCHASE_REQUEST));

		for (ProductItem itemToSell : productItemRepository.findAll(predicate)) {

			Product p = itemToSell.getProduct();

			SoldProduct cooked = new SoldProduct()
					.setId(p.getId())
					.setItemId(itemToSell.getId())
					.setOrderId(itemToSell.getEffectiveOrder().getId())
					.setBrand(p.getBrand().getName())
					.setDescription(p.getDisplayName())
					.setPrimaryImage(productService.getPrimaryImage(p)
							.map(ProductImage::getThumbnailUrl)
							.orElse(null))
			    	.setSize(itemToSell.getConcreteSizePretty().orElse(null));

			Optional<OrderPosition> orderPositionWithItemToSale
					= orderService.findOrderPositionWithGivenItem(itemToSell.getEffectiveOrder(), itemToSell);
			OrderPosition effectiveOrderPosition = orderPositionWithItemToSale
					.orElseThrow(() -> new IllegalStateException(
							"Вещь " + itemToSell.getId()
									+ ", у которой эффективный заказ установлен в "
									+ itemToSell.getEffectiveOrder().getId()
									+ " не найдена в этом заказе"
					));

			BigDecimal priceWithCommission = effectiveOrderPosition.getAmount();
			cooked.setPriceWithCommission(Utils.prettyRoundToCents(effectiveOrderPosition.getAmount()));

			BigDecimal commission = effectiveOrderPosition.getCommission();

			BigDecimal priceWithoutCommission = commission != null?
					commissionService.calculatePriceWithoutCommission(priceWithCommission, commission)
					: priceWithCommission;

			cooked.setPriceWithoutCommission(Utils.prettyRoundToCents(priceWithoutCommission));

			cookedProducts.add(cooked);
		}

		return cookedProducts;
	}

	@Override
	public List<SoldProduct> getSoldProducts(User u) {

		List<SoldProduct> cookedProducts = new ArrayList<>();

		QProductItem item = QProductItem.productItem;

		BooleanExpression predicate = item.product.seller.eq(u)
				.and(item.state.in(soldStates.keySet()));

		for (ProductItem soldItem : productItemRepository.findAll(predicate)) {

			Product p = soldItem.getProduct();

			SoldProduct cooked = new SoldProduct()
					.setId(p.getId())
					.setItemId(soldItem.getId())
					.setOrderId(soldItem.getEffectiveOrder().getId())
					.setBrand(p.getBrand().getName())
					.setDescription(p.getDisplayName())
					.setPrimaryImage(productService.getPrimaryImage(p)
							.map(ProductImage::getThumbnailUrl)
							.orElse(null))
					.setSize(soldItem.getConcreteSizePretty().orElse(null));

			/* Цены, с комиссией и без. */
			Optional<OrderPosition> orderPositionWithItemToSale
					= orderService.findOrderPositionWithGivenItem(soldItem.getEffectiveOrder(), soldItem);
			OrderPosition effectiveOrderPosition = orderPositionWithItemToSale
					.orElseThrow(() -> new IllegalStateException(
							"Вещь " + soldItem.getId()
									+ ", у которой эффективный заказ установлен в "
									+ soldItem.getEffectiveOrder().getId()
									+ " не найдена в этом заказе"
					));

			BigDecimal priceWithCommission = effectiveOrderPosition.getAmount();
			cooked.setPriceWithCommission(Utils.prettyRoundToCents(effectiveOrderPosition.getAmount()));

			BigDecimal commission = effectiveOrderPosition.getCommission();

			BigDecimal priceWithoutCommission = commission != null?
					commissionService.calculatePriceWithoutCommission(priceWithCommission, commission)
					: priceWithCommission;

			cooked.setPriceWithoutCommission(Utils.prettyRoundToCents(priceWithoutCommission));
			//

			/* История состояний товара - от новых к старым. */
			List<ItemStateChange> stateHistory = productService.getStateHistory(soldItem);

			List<SoldProduct.State> states = stateHistory.stream()

					.filter(s -> soldStates.containsKey(s.getState()))

					.map(s -> new SoldProduct.State()
							.setFormattedAt(s.getAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
							.setName(soldStates.get(s.getState())))

					.collect(toList());

			Collections.reverse(states);
			cooked.setStates(states);
			//

			cookedProducts.add(cooked);
		}

		return cookedProducts;
	}

	@Override
	public List<SoldProduct> getSalePendingProducts(User u) {

		List<SoldProduct> cookedProducts = new ArrayList<>();

		QProductItem item = QProductItem.productItem;

		BooleanExpression predicate = item.product.seller.eq(u)
				.and(item.state.in(salePendingStates.keySet()));

		for (ProductItem pendingItem : productItemRepository.findAll(predicate)) {

			Product p = pendingItem.getProduct();

			SoldProduct cooked = new SoldProduct()
					.setId(p.getId())
					.setItemId(pendingItem.getId())
                    .setOrderId(pendingItem.getEffectiveOrder().getId())
					.setBrand(p.getBrand().getName())
					.setDescription(p.getDisplayName())
					.setPrimaryImage(productService.getPrimaryImage(p)
							.map(ProductImage::getThumbnailUrl)
							.orElse(null))
					.setSize(pendingItem.getConcreteSizePretty().orElse(null));

			/* Цены, с комиссией и без. */
			Optional<OrderPosition> orderPositionWithItemToSale
					= orderService.findOrderPositionWithGivenItem(pendingItem.getEffectiveOrder(), pendingItem);
			OrderPosition effectiveOrderPosition = orderPositionWithItemToSale
					.orElseThrow(() -> new IllegalStateException(
							"Вещь " + pendingItem.getId()
									+ ", у которой эффективный заказ установлен в "
									+ pendingItem.getEffectiveOrder().getId()
									+ " не найдена в этом заказе"
					));

			BigDecimal priceWithCommission = effectiveOrderPosition.getAmount();
			cooked.setPriceWithCommission(Utils.prettyRoundToCents(effectiveOrderPosition.getAmount()));

			BigDecimal commission = effectiveOrderPosition.getCommission();

			BigDecimal priceWithoutCommission = commission != null?
					commissionService.calculatePriceWithoutCommission(priceWithCommission, commission)
					: priceWithCommission;

			cooked.setPriceWithoutCommission(Utils.prettyRoundToCents(priceWithoutCommission));
			//

			/* История состояний товара - от новых к старым. */
			List<ItemStateChange> stateHistory = productService.getStateHistory(pendingItem);

			List<SoldProduct.State> states = stateHistory.stream()

					.filter(s -> salePendingStates.containsKey(s.getState()))

					.map(s -> new SoldProduct.State()
							.setFormattedAt(s.getAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
							.setName(salePendingStates.get(s.getState())))

					.collect(toList());

			Collections.reverse(states);
			cooked.setStates(states);
			//

			cookedProducts.add(cooked);
		}

		return cookedProducts;
	}

    @Override
    public List<OrderView> getOrders(User user) {
        return orderService.getOrders(user, Arrays.asList(
        		OrderState.HOLD, OrderState.HOLD_COMPLETED,
				OrderState.HOLD_ERROR, OrderState.REFUND));
    }

    @Override
    public List<ProductSubscription> getProductSubscriptions(User user) {
		List<ProductAlertSubscription> subs =
                subscriptionService.getProductAlertSubscriptions(user);

		return subs.stream().map(this::from).collect(toList());
    }

    private ProductSubscription from(ProductAlertSubscription raw) {
		ProductSubscription cooked = new ProductSubscription()
				.setId(raw.getId())
				.setCreatedAt(raw.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

		if (raw.getCategory() != null) {
			List<Category> categories = raw.getCategory().getParents();
			categories.add(raw.getCategory());

			List<String> cookedCategories = categories.stream()
					.map(Category::getNameForProduct).collect(toList());

			cooked.setCategories(cookedCategories);
		}


		List<ProductAlertAttributeValueBinding> rawAttributes = raw.getAttributeValueBindings();
		if (!rawAttributes.isEmpty()) {
			List<String> attributes = rawAttributes.stream()
					.map(b -> b.getAttributeValue().getValue()).collect(toList());
			cooked.setAttributes(attributes);
		}

		Brand brand = raw.getBrand();
		if (brand != null) { cooked.setBrand(brand.getName()); }

		ProductCondition condition = raw.getProductCondition();
		if (condition != null) { cooked.setCondition(condition.getName()); }

		Size size = raw.getSize();
		if (size != null) {
			ProductSubscription.Size s = new ProductSubscription.Size();
			s.setType(raw.getViewSizeType().getAbbreviation())
					.setValue(size.getBySizeType(raw.getViewSizeType()));
			cooked.setSize(s);
		}

		return cooked;
	}

    /** Подготовить данные по офферам для отображения их в личном кабинете. */
	private OffersByProduct from(Product product, List<Offer> offers) {

		boolean productIsAvailable = productService.productIsAvailable(product);

		OffersByProduct.Product cookedProduct = new OffersByProduct.Product()
				.setId(product.getId())
				.setName(product.getDisplayName())
				.setBrand(product.getBrand().getName())
				.setNotUsedYet(product.isNotUsedYet())

				/* если товар невоможно купить, то не отображать его текущую цену */
				.setCurrentPrice(productIsAvailable?
						Utils.prettyRoundToCents(productService.getItemsSinglePriceIfAny(product).orElseThrow(
                            () -> new IllegalStateException(product.getId().toString())))
						: null)

				.setImageUrl(productService.getPrimaryImage(product)
						/* у товара нет основного изображения? */
						.orElseThrow(() -> new IllegalStateException(product.getId().toString()))
						.getThumbnailUrl());

        /* список размеров доступных для покупки вещей ... */
		List<String> availableSizes = productService.getAvailableSizes(product).stream()
               /* ... в той сетке, которая указана в товаре. */
				.map(size -> size.getBySizeType(product.getSizeType()))
				.filter(Objects::nonNull)
				.collect(toList());

		if (!availableSizes.isEmpty()) {
			cookedProduct.setSize(new OffersByProduct.Size()
					.setType(product.getSizeType().getAbbreviation())
					.setValues(String.join(" / ", availableSizes))
			);
		}

        OffersByProduct.Negotiability negotiability = new OffersByProduct.Negotiability()
                .setNegotiated(productIsAvailable && Boolean.TRUE.equals(offers.get(offers.size() - 1).getIsAccepted()))
                .setNegotiable(productIsAvailable && offers.size() < 3 && Boolean.FALSE.equals(offers.get(offers.size() - 1).getIsAccepted()))
                .setOptionalFailMessage(!productIsAvailable ? "Товара нет в продаже" : null);

		return new OffersByProduct()
				.setProduct(cookedProduct)
				.setOffers(offers.stream().map(this::from).collect(toList()))
				.setNegotiability(negotiability);
	}

	/** Выделить из оффера только те данные, которые нужны в личном кабинете. */
	private OffersByProduct.Offer from(Offer o) {

		return new OffersByProduct.Offer()
				.setId(o.getId())
				.setOfferedPrice(Utils.prettyRoundToCents(o.getPrice()))
				.setIsAccepted(o.getIsAccepted())
				.setCreatedAt(o.getCreatedAt());
	}

	@Override
	public void updateAvatar(MultipartFile image, Long userId) {
		User user = userService.getUserById(userId).get();
		String avatarDir = String.format("profile/%s/", user.getId());
		String fullAvatarDir = imageBaseDirPath + avatarDir;
		File savedFile = FileUtils.saveMultipartFileWithGeneratedName(fullAvatarDir, image);
		if(user.getAvatarPath() != null) {
			String oldAvatarPath = imageBaseDirPath + user.getAvatarPath();
			FileUtils.deleteFile(oldAvatarPath);
		}
		user.setAvatarPath(avatarDir + savedFile.getName());
		userService.save(user);
	}


	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public void updatePersonalInfo(PersonalInfo personalInfo, User user) {

		DeliveryRequisite deliveryRequisite = user.getDeliveryRequisite();
		/* в случае если у пользователя в профиле вообще не задан ни один параметр данных о доставке */
		if (deliveryRequisite == null) {
			deliveryRequisite = new DeliveryRequisite();
		}

        deliveryRequisite.setDeliveryName(!Strings.isNullOrEmpty(personalInfo.getName())?
                personalInfo.getName()
                : null);

		deliveryRequisite.setDeliveryPhone(!Strings.isNullOrEmpty(personalInfo.getPhone())?
				personalInfo.getPhone()
				: null);

		String rawBirthDate = personalInfo.getBirthDate();
		if (!Strings.isNullOrEmpty(rawBirthDate)) {
			LocalDate birthDate = LocalDate.parse(rawBirthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("ru")));
			user.setBirthDate(birthDate.atStartOfDay());
		}
		else {
			user.setBirthDate(null);
		}

		user.setDeliveryRequisite(deliveryRequisite);

		userService.save(user);
	}

	@Override
	public void updateDeliveryRequisite(Delivery delivery, User user) {

		DeliveryRequisite deliveryRequisite = user.getDeliveryRequisite();
		/* в случае если у пользователя в профиле вообще не задан ни один параметр данных о доставке */
		if (deliveryRequisite == null) {
			deliveryRequisite = new DeliveryRequisite();
		}

		deliveryRequisite.setDeliveryCity(!Strings.isNullOrEmpty(delivery.getCity())?
				delivery.getCity()
				: null);

		deliveryRequisite.setDeliveryAddress(!Strings.isNullOrEmpty(delivery.getAddress())?
				delivery.getAddress()
				: null);

		deliveryRequisite.setDeliveryZipCode(!Strings.isNullOrEmpty(delivery.getZipCode())?
				delivery.getZipCode()
				: null);

		deliveryRequisite.setDeliveryCountry(!Strings.isNullOrEmpty(delivery.getCountry())?
				delivery.getCountry()
				: null);
		deliveryRequisite.setDeliveryExtensiveAddress(delivery.getExtensiveAddress());

		user.setDeliveryRequisite(deliveryRequisite);

		userService.save(user);
	}

	@Override
    @Transactional
	public void updateSellerInfo(ProfileSellerRequest request, Long userId) {

		User user = userService.getUserById(userId).get();

		user.setPassport(request.getPassport());
		user.setINN(request.getINN());
		user.setOGRNIP(request.getOGRNIP());
		user.setOGRN(request.getOGRN());
		user.setKPP(request.getKPP());

		SellerRequisite sellerRequisite = user.getSellerRequisite();
		if(sellerRequisite == null) {
			sellerRequisite = new SellerRequisite();
			user.setSellerRequisite(sellerRequisite);
		}
		sellerRequisite
				.setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setCompanyName(request.getCompanyName())
                .setPhone(request.getPhone())
                .setZipCode(request.getZipCode())
                .setCity(request.getCity())
                .setAddress(request.getAddress());

		if (request.getExtensiveAddress() != null) {
			sellerRequisite.setExtensiveAddress(request.getExtensiveAddress());
		}

		PaymentRequisite paymentRequisite = user.getPaymentRequisite();
		if(paymentRequisite == null) {
			paymentRequisite = new PaymentRequisite();
			user.setPaymentRequisite(paymentRequisite);
		}
		paymentRequisite.setBIK(request.getBIK());
		paymentRequisite.setCorrespondentAccount(request.getCorrespondentAccount());
		paymentRequisite.setPaymentAccount(request.getPaymentAccount());
	}

	public boolean profileIsExist(Long userId) {
		return userService.getUserById(userId).isPresent();
	}

	private EnumMap<ProductItem.State, String> salePendingStates = new EnumMap<>(ProductItem.State.class);
	{
		salePendingStates.put(ProductItem.State.SALE_CONFIRMED, "Продажа товара подтверждена");
		salePendingStates.put(ProductItem.State.HQ_WAREHOUSE, "Товар прибыл на склад. Ожидает экспертизы OSKELLY");
		salePendingStates.put(ProductItem.State.ON_VERIFICATION, "Товар проходит экспертизу OSKELLY");
		salePendingStates.put(ProductItem.State.VERIFICATION_NEED_CLEANING, "Товар прошел экспертизу OSKELLY и отправлен на химчистку");
		salePendingStates.put(ProductItem.State.VERIFICATION_BAD_STATE_NEED_CONFIRMATION, "?");
		salePendingStates.put(ProductItem.State.VERIFICATION_BAD_STATE_BUYER_CONFIRMED, "?");
		salePendingStates.put(ProductItem.State.REJECTED_AFTER_VERIFICATION, "?");
		salePendingStates.put(ProductItem.State.READY_TO_SHIP, "Товар прошел экспертизу OSKELLY и готов к отправке");
		salePendingStates.put(ProductItem.State.CREATE_WAYBILL_TO_BUYER, "Товар ожидает прибытия курьера");
		salePendingStates.put(ProductItem.State.SHIPPED_TO_CLIENT, "Товар отправлен покупателю");
	}

	private EnumMap<ProductItem.State, String> soldStates = new EnumMap<>(ProductItem.State.class);
	{
		soldStates.put(ProductItem.State.SALE_REJECTED, "Продажа товара отклонена");
	}
}
