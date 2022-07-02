package su.reddot.domain.service.product.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.dao.product.ProductAttributeValueBindingRepository;
import su.reddot.domain.dao.product.ProductItemRepository;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.order.Order;
import su.reddot.domain.model.order.OrderPosition;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.event.ProductItemSaleResolved;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.commission.CommissionException;
import su.reddot.domain.service.commission.CommissionService;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.image.ProductImage;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.product.ItemsSummaryBySize;
import su.reddot.domain.service.product.ProductSizeMapping;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.product.item.view.Attribute;
import su.reddot.domain.service.product.item.view.ConfirmationResult;
import su.reddot.domain.service.product.item.view.ProductItemToSell;
import su.reddot.presentation.Utils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 17.06.17.
 */
@Component
@RequiredArgsConstructor
public class DefaultProductItemService implements ProductItemService {

	private final ProductItemRepository productItemRepository;
	private final ProductAttributeValueBindingRepository attributeValueBindingRepository;

	private final ApplicationEventPublisher publisher;

	private final CommissionService commissionService;
	private final ImageService imageService;

	private OrderService orderService;

	@Autowired
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	@Override
	public ProductItem findFirstByProduct(Product p) throws NullPointerException {
		Optional<ProductItem> productItem = productItemRepository.findFirstByProductAndDeleteTimeIsNullOrderById(p);
		if(!productItem.isPresent()) {
			productItem = productItemRepository.findFirstByProductOrderById(p);
		}
		if(!productItem.isPresent()) {
			throw new IllegalStateException("Product items for product with id: " + p.getId() + " not found");
		}
		return productItem.get();
	}

	public Optional<ProductItem> findById(Long itemId){
		ProductItem item = productItemRepository.findOne(itemId);
		if (item == null) { return Optional.empty(); }
		return Optional.ofNullable(item);
	}

	@Override
	public Optional<ProductItem> getForReservationWithLocking(Long productItemId) {
		return productItemRepository.findByIdAndReserveExpireTimeIsNullAndDeleteTimeIsNull(productItemId);
	}

	@Override
    @Transactional
	public void confirmSale(Long itemToSellId, User seller, boolean doConfirmSale, SellerRequisite nullableSellerRequisite) {

		Optional<ProductItem> productItemIfAny = productItemRepository.findByIdAndProductSeller(itemToSellId, seller);

		ProductItem validItem = productItemIfAny.orElseThrow(() -> new IllegalArgumentException(
				String.format("Вещь с идентификатором %s не найдена у продавца %s", itemToSellId, seller.getEmail())));

		if (doConfirmSale && nullableSellerRequisite == null) {
			throw new IllegalArgumentException("Адрес получения вещи курьером не может быть пустым");
		}

		if (validItem.getState() != ProductItem.State.PURCHASE_REQUEST) {
			throw new IllegalArgumentException("Состояние вещи не подразумевает подтверждения или отклонения ее продажи");
		}

		validItem.setState(doConfirmSale? ProductItem.State.SALE_CONFIRMED : ProductItem.State.SALE_REJECTED);

		/* продавец подтвердил сделку - обновить в позиции заказа адрес,
		 * по которому курьер заберет продаваемый товар
		 */
		if (doConfirmSale) {

			Order effectiveOrder = validItem.getEffectiveOrder();
			Optional<OrderPosition> orderPositionWithGivenItemIfAny =
					orderService.findOrderPositionWithGivenItem(effectiveOrder, validItem);

			OrderPosition orderPosition = orderPositionWithGivenItemIfAny
					.orElseThrow(() -> new IllegalStateException(String.format(
							"Вещь %d, у которой эффективный заказ установлен в %d, не найдена в этом заказе",
                            validItem.getId(), effectiveOrder.getId())));

			orderPosition.setPickupRequisite(nullableSellerRequisite);
			orderPosition.setIsEffective(true);
		}

		publisher.publishEvent(new ProductItemSaleResolved(validItem.getId()));
	}

	@Override
	public void save(ProductItem p) throws CommissionException {
		if(p.getProduct().getSeller().isPro()) {
			setupCurrentPriceWithCommission(p);
		}
		else {
			setupCurrentPriceWithoutCommission(p);
		}
		productItemRepository.save(p);
	}

	private void setupCurrentPriceWithCommission(ProductItem p) throws CommissionException {
		if(!p.getState().equals(ProductItem.State.INITIAL)) {
			return;
		}
		if(p.getCurrentPriceWithoutCommission() == null) {
			p.setCurrentPrice(null);
			return;
		}
		BigDecimal updatedPrice = commissionService.calculatePriceWithCommission(
				p.getCurrentPriceWithoutCommission(),
				p.getProduct().getSeller(),
				p.getProduct().getCategory(),
				p.getProduct().isTurbo(),
				p.getProduct().isNewCollection()
		);
		p.setCurrentPrice(updatedPrice);
		//Если цена после обновления вдруг стала больше, то мы меняем startPrice
		if (updatedPrice.compareTo(p.getStartPrice()) > 0){ p.setStartPrice(updatedPrice);}

	}

	private void setupCurrentPriceWithoutCommission(ProductItem p) throws CommissionException {
		if(!p.getState().equals(ProductItem.State.INITIAL)) {
			return;
		}
		if(p.getCurrentPrice() == null) {
			p.setCurrentPriceWithoutCommission(null);
			return;
		}
		p.setCurrentPriceWithoutCommission(commissionService.calculatePriceWithoutCommission(
				p.getCurrentPrice(),
				p.getProduct().getSeller(),
				p.getProduct().getCategory(),
				p.getProduct().isTurbo(),
				p.getProduct().isNewCollection()
		));
	}

	@Override
	public List<ProductItem> findByProduct(Product p) {
		return productItemRepository.findByProductAndDeleteTimeIsNull(p);
	}

	@Override
	public void save(Iterable<ProductItem> productItems) throws CommissionException {
		for (ProductItem p : productItems) {
			this.save(p);
		}
	}

	@Override
	public List<ProductItem> getItemsWithLowestPrice(Product p, List<Long> sizes) {
	    if (sizes.isEmpty()) {
	    	return Collections.singletonList(
	    			productItemRepository.findFirstByProductAndDeleteTimeIsNullOrderByCurrentPriceAsc(p));
		}

	    List<ProductItem> itemsWithSizesAndLowestPrices = new ArrayList<>();

		List<ProductItem> items = productItemRepository.findBySizeIdInAndProductAndDeleteTimeIsNullOrderBySizeId(sizes, p);

		items.stream().collect(Collectors.groupingBy(ProductItem::getSize)).forEach(
				(size, productItems) -> itemsWithSizesAndLowestPrices.add(
						productItems.stream().min(Comparator.comparing(ProductItem::getCurrentPrice)).get()));

		return itemsWithSizesAndLowestPrices;
	}

	@Override
	public Set<ProductSizeMapping> getProductSizeMappings(Product p) {
		return productItemRepository.getProductSizeMappings(p, ProductItem.State.INITIAL);
	}

	@Override
	public List<ItemsSummaryBySize> getAvailableItemsSummary(Product p) {
		return productItemRepository.getAvailableItemsSummaryGroupedBySize( p.getId(), ProductItem.State.INITIAL, null);
	}

	@Override
	public List<ItemsSummaryBySize> getAvailableItemsSummary(Product p, List<Long> interestingSizes) {

		return productItemRepository.getAvailableItemsSummaryGroupedBySize(
				p.getId(), ProductItem.State.INITIAL,
				interestingSizes != null && interestingSizes.size() > 0? interestingSizes : null);
	}

    @Override
    public Optional<ProductItem> findFirstAvailable(Long productId, Long sizeId, BigDecimal price) {
        return productItemRepository.getFirstAvailable(productId, sizeId, price);
    }

	@Override
	public Optional<ProductItem> findFirstAvailable(Long productId, Long sizeId) {
		return productItemRepository.getFirstAvailable(productId, sizeId);
	}

    @Override
	public Long countByProductAndSizeAndPrice(Product p, Size s, BigDecimal price) {
		return productItemRepository.countByProductAndSizeAndCurrentPriceWithoutCommissionAndDeleteTimeIsNull(p, s, price);
	}

	@Override
	public void deleteByProductAndSizeAndPrice(Product p, Size s, BigDecimal price, Integer limit) {
		/*
		  TODO: много дублирования, надо понять как в запрос подставлять значение NULL и делать проверку на IS NULL 
		 */
		if(price != null && s != null) {
			productItemRepository.setDeleteTimeByProductAndSizeAndPrice(new Date(), p.getId(), s.getId(), price, limit);
		}
		else if(price == null && s != null) {
			productItemRepository.setDeleteTimeByProductAndSizeAndPriceIsNull(new Date(), p.getId(), s.getId(), limit);
		}
		else if(price != null) {
			productItemRepository.setDeleteTimeByProductAndSizeIsNullAndPrice(new Date(), p.getId(), price, limit);
		}
		else {
			productItemRepository.setDeleteTimeByProductAndSizeIsNullAndPriceIsNull(new Date(), p.getId(), limit);
		}
	}

	@Override
	public Optional<ProductItem> getItemLikeThisThatCanBeOrdered(ProductItem item) {
		return productItemRepository.getItemLikeThisThatCanBeOrdered(item);
	}

	@Override
	public Optional<ProductItem> getItemForSale(ProductItem item, BigDecimal interestingPrice) {
		return productItemRepository.getItemForSale(item, interestingPrice);
	}

    @Override
    public Optional<ProductItemToSell> getForSaleConfirmation(Long id, User seller) {

		List<ProductItem.State> relevantStates = Arrays.asList(
			ProductItem.State.PURCHASE_REQUEST,
				ProductItem.State.SALE_CONFIRMED,
				ProductItem.State.SALE_REJECTED);

		Optional<ProductItem> availableItemIfAny = productItemRepository.getAvailableItemBySellerAndState(id, seller, relevantStates);
		if (!availableItemIfAny.isPresent()) { return Optional.empty(); }

		ProductItem itemToSell = availableItemIfAny.get();
		Product product = itemToSell.getProduct();

		ProductItemToSell view = new ProductItemToSell();
		view.setOrderId(itemToSell.getEffectiveOrder().getId())
				.setItemId(itemToSell.getId())
				.setBrand(product.getBrand().getName())
				.setCategory(product.getCategory().getNameForProduct())
				.setCondition(product.getProductCondition().getName());

		/* Изображения */
		imageService.getPrimaryImage(product).ifPresent(view::setPrimaryImage);

		List<ProductImage> additionalImages = imageService.getProductImages(product).stream()
				.filter((i) -> !i.isPrimary())
				.collect(Collectors.toList());
		view.setAdditionalImages(additionalImages);


		/* Атрибуты */
		List<Attribute> attributes = new ArrayList<>();
		List<AttributeValue> productAttributeValues
				= attributeValueBindingRepository
				.findAttributeValuesByProductWithLimit(product, 100);

		for (AttributeValue attributeValue : productAttributeValues) {
			attributes.add(new Attribute(
					attributeValue.getAttribute().getName(),
					attributeValue.getValue()));
		}
        attributes.add(new Attribute("Описание", product.getDescription()));
		attributes.add(new Attribute("Артикул", product.getVendorCode() != null?
				product.getVendorCode()
				: "Не указан"));

		view.setAttributes(attributes);

		itemToSell.getConcreteSizePretty().ifPresent(view::setSize);

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
		view.setPriceWithCommission(Utils.prettyRoundToCents(effectiveOrderPosition.getAmount()));

		BigDecimal commission = effectiveOrderPosition.getCommission();

		/* FIXME реальна ли ситуация, когда в позиции заказа не указано значение комиссии? */
		BigDecimal priceWithoutCommission = commission != null?
				commissionService.calculatePriceWithoutCommission(priceWithCommission, commission)
				: priceWithCommission;

		view.setPriceWithoutCommission(Utils.prettyRoundToCents(priceWithoutCommission));

		/*
		 * Ты можешь отредактировать адрес, по которому курьер забирает вещь,
		 * если только ты еще не подтвердил / отменил продажу вещи.
		 */
		if (itemToSell.getState() == ProductItem.State.PURCHASE_REQUEST) {
			SellerRequisite sellerRequisite = seller.getSellerRequisite();
			/* продавец еще не заполнил адрес в своем профиле */
			if (sellerRequisite == null) { sellerRequisite = new SellerRequisite(); }

			view.setPickupRequisite(sellerRequisite)
		    	.setRequisiteIsEditable(true);
		}
		else if (itemToSell.getState() == ProductItem.State.SALE_CONFIRMED) {
			view.setPickupRequisite(effectiveOrderPosition.getPickupRequisite());
			view.setRequisiteIsEditable(false);
			view.setConfirmationResult(ConfirmationResult.CONFIRMED);
		}
		else {
			view.setRequisiteIsEditable(false);
			view.setConfirmationResult(ConfirmationResult.REJECTED);
			/* если пользователь ранее отменил продажу вещи, не отображать информацию о доставке */
		}

		return Optional.of(view);
	}

	@Override
	public List<ProductItem> getItemsByState(ProductItem.State state){

		return productItemRepository.getAllByStateAndDeleteTimeIsNull(state);
	}

	@Override
	public List<ProductItem> getItemsByStateAndProduct(ProductItem.State state, Product product){

		return productItemRepository.getAllByStateAndProductAndDeleteTimeIsNull(state, product);
	}

	@Override
	public List<ProductItem> getItemsBySomeStates(List<ProductItem.State> states){

		return productItemRepository.getAllByStateInAndDeleteTimeIsNull(states);
	}

	@Override
	public void setStateFirstOnWarehouse(ProductItem item){
		item.setState(ProductItem.State.HQ_WAREHOUSE);
		productItemRepository.save(item);
	}

	@Override
	public void setStateOnVerification(ProductItem item){
		item.setState(ProductItem.State.ON_VERIFICATION);
		productItemRepository.save(item);
	}

	@Override
	public void setStateAfterVerification(ProductItem item, ProductItem.State state) {
		item.setState(state);
		productItemRepository.save(item);
	}

	@Override
	public Optional<OrderPosition> getEffectiveOrderPosition(Long itemId) {

		Optional<ProductItem> nullableProductItem = this.findById(itemId);

		if (nullableProductItem.isPresent()){
			ProductItem productItem = nullableProductItem.get();
			Order effectiveOrder =  productItem.getEffectiveOrder();
			if (effectiveOrder != null){
				OrderPosition effectiveOrderPosition = effectiveOrder
						.getOrderPositions()
						.stream()
						.filter(op -> op.getProductItem().getId().equals(itemId))
						.findFirst().orElse(null);
				if (effectiveOrderPosition != null) {
					return Optional.ofNullable(effectiveOrderPosition);
				}
			}

		}

		return Optional.empty();
	}

	@Override
	public BigDecimal getMaxStartPriceByProduct(Product product) {
		return productItemRepository.getAllByProduct(product).stream()
				.map(ProductItem::getStartPrice).max(Comparator.naturalOrder()).get();
	}
}
