package su.reddot.presentation.mobile.api.v1;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.user.PaymentRequisite;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.notification.NotificationService;
import su.reddot.domain.service.notification.NotificationView;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.domain.service.orderPosition.OrderPositionService;
import su.reddot.domain.service.orderPosition.SaleGroupView;
import su.reddot.domain.service.orderPosition.SaleView;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.product.item.view.ProductItemToSell;
import su.reddot.domain.service.product.view.ProductCard;
import su.reddot.domain.service.profile.ProfileSellerRequest;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.domain.service.profile.ProfileView;
import su.reddot.domain.service.profile.view.OffersByProduct;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.mobile.api.v1.response.OfferResponse;
import su.reddot.presentation.mobile.api.v1.response.account.Account;
import su.reddot.presentation.validation.ProfileImageValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Контроллер для персонального личного кабинета
 * Не путать с публичным профилем
 * @see ProfileRestControllerV1
 * @author Vitaliy Khludeev on 13.11.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/account")
@RequiredArgsConstructor
public class AccountRestControllerV1 {

	private final ProfileImageValidator profileImageValidator;
	private final ProfileService profileService;
	private final UserService userService;
	private final OrderPositionService orderPositionService;
	private final ProductItemService productItemService;
	private final NotificationService notificationService;
	private final ProductService productService;

	@GetMapping
	public ResponseEntity<Account> getAccount(UserIdAuthenticationToken token) {
		ProfileView v = profileService.getProfileView(token.getUserId());
		SellerRequisite sellerRequisite = v.getUser().getSellerRequisite();
		PaymentRequisite paymentRequisite = v.getUser().getPaymentRequisite();
		ProfileSellerRequest profileSellerRequest = new ProfileSellerRequest()
				.setPassport(v.getUser().getPassport())
				.setINN(v.getUser().getINN())
				.setOGRNIP(v.getUser().getOGRNIP())
				.setOGRN(v.getUser().getOGRN())
				.setKPP(v.getUser().getKPP());
		if (sellerRequisite != null) {
			profileSellerRequest
					.setLastName(sellerRequisite.getLastName())
					.setCompanyName(sellerRequisite.getCompanyName())
					.setPhone(sellerRequisite.getPhone())
					.setZipCode(sellerRequisite.getZipCode())
					.setCity(sellerRequisite.getCity())
					.setAddress(sellerRequisite.getAddress())
					.setFirstName(sellerRequisite.getFirstName());
		}
		if (paymentRequisite != null) {
			profileSellerRequest
					.setBIK(paymentRequisite.getBIK())
					.setCorrespondentAccount(paymentRequisite.getCorrespondentAccount())
					.setPaymentAccount(paymentRequisite.getPaymentAccount());
		}
		return ResponseEntity.ok(
				new Account(
						v.getUser().getId(),
						v.getEmail(),
						v.getAvatarUrl(),
						v.getNickname(),
						v.getCity(),
						v.getIsPro() ? "Профессиональный продавец" : "Продавец",
						v.getPublishedProductsCount(),
						v.getPhone(),
						v.getFirstName(),
						v.getLastName(),
						sellerRequisite,
						v.getUser().getDeliveryRequisite(),
						v.getRegistrationTime(),
						v.getSubscribers(),
						v.getSubscriptions(),
						v.getLikes(),
						v.getUser().getBirthDate() != null ? v.getUser().getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null,
						profileSellerRequest,
						v.getUser().isPro(),
						v.getPublishedProductsCount(),
						0
				)
		);
	}

	@PutMapping(value = "/info")
	public ResponseEntity<?> updatePersonalInfo(ProfileService.PersonalInfo personalInfo, UserIdAuthenticationToken token) {

        /* благодаря PreAuthorize только существующий пользователь может попасть в этот метод */
		//noinspection ConstantConditions
		User user = userService.getUserById(token.getUserId()).get();
		profileService.updatePersonalInfo(personalInfo, user);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/address")
	public ResponseEntity<?> saveAddressInfo(ProfileService.Delivery delivery, UserIdAuthenticationToken token) {

        /* благодаря PreAuthorize только существующий пользователь может попасть в этот метод */
		//noinspection ConstantConditions
		User user = userService.getUserById(token.getUserId()).get();
		profileService.updateDeliveryRequisite(delivery, user);

		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/image")
	public ResponseEntity<?> uploadImage(MultipartFile image, UserIdAuthenticationToken token) {
		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "image");
		profileImageValidator.validate(image, bindingResult);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException(bindingResult.getAllErrors().get(0).getCode());
		}
		profileService.updateAvatar(image, token.getUserId());
		return ResponseEntity.ok().build();
	}

	@PutMapping(value = "/seller")
	public ResponseEntity<?> saveSellerInfo(@ModelAttribute ProfileSellerRequest request, UserIdAuthenticationToken token) {
		profileService.updateSellerInfo(request, token.getUserId());
		return ResponseEntity.ok().build();
	}

	@PutMapping
	public ResponseEntity<?> saveFullAccount(@RequestBody Account account, UserIdAuthenticationToken t) {
		User user = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		if (!Strings.isNullOrEmpty(account.getBirthDate())) {
			LocalDate birthDate = LocalDate.parse(account.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("ru")));
			user.setBirthDate(birthDate.atStartOfDay());
		}
		else {
			user.setBirthDate(null);
		}
		user.setDeliveryRequisite(account.getDeliveryRequisite());
		profileService.updateSellerInfo(account.getSeller(), t.getUserId());
		userService.save(user);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/orders")
	public ResponseEntity<List<OrderView>> getOrders(UserIdAuthenticationToken t) {
		User user = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		return ResponseEntity.ok(profileService.getOrders(user));
	}

	@GetMapping("/orders/{id}")
	public ResponseEntity<OrderView> getOrder(@PathVariable Long id, UserIdAuthenticationToken t) {
		User user = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		List<OrderView> orders = profileService.getOrders(user);
		OrderView order = orders.stream().filter(o -> o.getId().equals(id)).findFirst().orElseThrow(IllegalArgumentException::new);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping(value = "/sales")
	public ResponseEntity<List<SaleGroupView>> getSales(UserIdAuthenticationToken t) {
		return ResponseEntity.ok(orderPositionService.getGroupedUserSales(t.getUserId()));
	}

	@GetMapping("/sales/{id}")
	public ResponseEntity<SaleView> getSale(@PathVariable Long id, UserIdAuthenticationToken t) {
		List<SaleView> sales = orderPositionService.getUserSales(t.getUserId());
		SaleView sale = sales.stream().filter(s -> s.getProductItemId().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Вещь с ID " + id + " не найдена"));
		return ResponseEntity.ok(sale);
	}

	@GetMapping(value = "/sales/address/{orderPositionId}")
	public ResponseEntity<SellerRequisite> getSaleAddress(@PathVariable Long orderPositionId, UserIdAuthenticationToken t) {
		User user = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		OrderPosition orderPosition = orderPositionService.getById(orderPositionId).orElseThrow(IllegalArgumentException::new);
		ProductItemToSell itemToSale = productItemService.getForSaleConfirmation(orderPosition.getProductItem().getId(), user).orElseThrow(IllegalArgumentException::new);
		return ResponseEntity.ok(itemToSale.getPickupRequisite());
	}

	@PutMapping(value = "/sales/confirm/{orderPositionId}")
	public ResponseEntity<Void> confirmProductItemSale(@PathVariable Long orderPositionId,
													   @RequestBody(required = false) SellerRequisite pickupRequisite,
													   @RequestParam boolean doConfirmSale,
													   UserIdAuthenticationToken t) {
		User seller = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		OrderPosition orderPosition = orderPositionService.getById(orderPositionId).orElseThrow(IllegalArgumentException::new);
		productItemService.confirmSale(orderPosition.getProductItem().getId(), seller, doConfirmSale, pickupRequisite);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/news")
	public ResponseEntity<List<NotificationView>> getNews(UserIdAuthenticationToken t, TimeZone timezone) {
		return ResponseEntity.ok(notificationService.findNewsByUserAndPresentAsView(t.getUserId(), true, timezone));
	}

	@GetMapping(value = "/notifications")
	public ResponseEntity<List<NotificationView>> getNotifications(UserIdAuthenticationToken t, TimeZone timezone) {
		return ResponseEntity.ok(notificationService.findNotificationsByUserAndPresentAsView(t.getUserId(), timezone));
	}

	@GetMapping(value = "/notifications/unread")
	public ResponseEntity<Long> getUnreadNotifications(UserIdAuthenticationToken t) {
		return ResponseEntity.ok(notificationService.countUnreadNotificationsByUser(t.getUserId()));
	}

	@PutMapping(value = "/notifications")
	public ResponseEntity<Void> readAll(UserIdAuthenticationToken t) {
		notificationService.readAll(userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new));
		return ResponseEntity.ok().build();
	}

	@GetMapping("/pricesubscriptions")
	public List<ProductCard> getMyPriceSubscriptions(UserIdAuthenticationToken token){
		return profileService.getMyPriceSubscriptions(token.getUserId());
	}

	/** Предложения по снижению цены на товары других пользователей. */
	@GetMapping("/offers")
	public List<OffersByProduct> offers(UserIdAuthenticationToken t){

		User u = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		return profileService.getOffersFrom(u);
	}

	/** Предложения по снижению цены на товары других пользователей. */
	@GetMapping("/offers-to-me")
	public List<OffersByProduct> offersToMe(UserIdAuthenticationToken t){

		User u = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		return profileService.getOffersTo(u);
	}

	@GetMapping("/offers-to-me/{id}")
	public OffersByProduct offersToMeById(@PathVariable Long id, UserIdAuthenticationToken t) {
		User u = userService.getUserById(t.getUserId()).orElseThrow(IllegalStateException::new);
		return profileService.getOffersTo(u, id).orElseThrow(IllegalArgumentException::new);
	}

	@PostMapping("/offers/{id}")
	public OffersByProduct confirmOffer(@PathVariable Long id, @RequestParam boolean confirm,
										  UserIdAuthenticationToken t) {

		User seller = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		productService.confirmOffer(id, seller, confirm);

		return profileService.getOffersTo(seller, id).orElseThrow(IllegalArgumentException::new);
	}

	/** Предложения по снижению цены на товары других пользователей. */
	@GetMapping("/offers2")
	public List<OfferResponse> offers2(UserIdAuthenticationToken t) {

		User u = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		List<OffersByProduct> offers = profileService.getOffersFrom(u);

		return offers.stream().map(o -> {
			List<OfferResponse.OfferHistory> history = new ArrayList<>();
			history.add(new OfferResponse.OfferHistory("Начальная цена: ", o.getProduct().getCurrentPrice(), false));
			boolean repeatedOffer = false;
			for (OffersByProduct.Offer offer : o.getOffers()) {
				history.add(new OfferResponse.OfferHistory(
						repeatedOffer ? "Повторное предложение: ": "Ваше предложение: ",
						offer.getOfferedPrice(),
						false
				));
				history.add(new OfferResponse.OfferHistory(
						Optional.ofNullable(offer.getIsAccepted()).map(b -> b ? "Продавец подтвердил предложение" : "Продавец отклонил предложение").orElse("Ожидаем ответа продавца"),
						null,
						true
				));
				repeatedOffer = true;
			}
			return new OfferResponse(
					o.getProduct().getId(),
					o.getProduct().getBrand(),
					o.getProduct().getName(),
					Optional.ofNullable(o.getProduct().getSize()).map(s -> s.getType() + ": " + s.getValues()).orElse(null),
					o.getProduct().getImageUrl(),
					o.getNegotiability().isNegotiated(),
//					o.getOffers().stream().map(OffersByProduct.Offer::getIsAccepted).anyMatch(Objects::isNull),
					false,
					o.getOffers().stream().filter(oo -> oo.getIsAccepted() == null).findFirst().map(OffersByProduct.Offer::getId).orElse(null),
					history,
					o.getNegotiability().getOptionalFailMessage()
			);
		}).collect(Collectors.toList());
	}

	/** Предложения по снижению цены на товары других пользователей. */
	@GetMapping("/offers-to-me2")
	public List<OfferResponse> offersToMe2(UserIdAuthenticationToken t) {

		User u = userService.getUserById(t.getUserId())
				.orElseThrow(IllegalStateException::new);
		List<OffersByProduct> offers = profileService.getOffersTo(u);
		return offers.stream().map(this::of).collect(Collectors.toList());
	}

	@GetMapping("/offers-to-me2/{id}")
	public OfferResponse offersToMeById2(@PathVariable Long id, UserIdAuthenticationToken t) {
		User u = userService.getUserById(t.getUserId()).orElseThrow(IllegalStateException::new);
		return of(profileService.getOffersTo(u, id).orElseThrow(IllegalArgumentException::new));
	}

	@PostMapping("/offers2/{id}")
	public OfferResponse confirmOffer2(@PathVariable Long id, @RequestParam boolean confirm,
										UserIdAuthenticationToken t) {
		User seller = userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new);
		productService.confirmOffer(id, seller, confirm);
		return of(profileService.getOffersTo(seller, id).orElseThrow(IllegalArgumentException::new));
	}

	private OfferResponse of(OffersByProduct o) {
		List<OfferResponse.OfferHistory> history = new ArrayList<>();
		history.add(new OfferResponse.OfferHistory("Начальная цена: ", o.getProduct().getCurrentPrice(), false));
		boolean repeatedOffer = false;
		for (OffersByProduct.Offer offer : o.getOffers()) {
			history.add(new OfferResponse.OfferHistory(
					repeatedOffer ? "Вам повторно предложили: ": "Вам предложили: ",
					offer.getOfferedPrice(),
					false
			));
			if(offer.getIsAccepted() != null) {
				history.add(new OfferResponse.OfferHistory(
						offer.getIsAccepted() ? "Вы подтвердили предложение" : "Вы отказались от предложения",
						null,
						true
				));
			}
			repeatedOffer = true;
		}
		return new OfferResponse(
				o.getProduct().getId(),
				o.getProduct().getBrand(),
				o.getProduct().getName(),
				Optional.ofNullable(o.getProduct().getSize()).map(s -> s.getType() + ": " + s.getValues()).orElse(null),
				o.getProduct().getImageUrl(),
//					o.getNegotiability().isNegotiated(),
				false,
				o.getOffers().stream().map(OffersByProduct.Offer::getIsAccepted).anyMatch(Objects::isNull),
				o.getOffers().stream().filter(oo -> oo.getIsAccepted() == null).findFirst().map(OffersByProduct.Offer::getId).orElse(null),
				history,
				o.getNegotiability().getOptionalFailMessage()
		);
	}
}