package su.reddot.presentation.api.v1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.catalog.AvailableFilters;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.comment.CommentService;
import su.reddot.domain.service.comment.CommentView;
import su.reddot.domain.service.like.LikeService;
import su.reddot.domain.service.like.type.ToggleResult;
import su.reddot.domain.service.product.DetailedProduct;
import su.reddot.domain.service.product.PriceNegotiationException;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductService.FilterSpecification;
import su.reddot.domain.service.product.ProductService.SortAttribute;
import su.reddot.domain.service.product.ProductService.ViewQualification;
import su.reddot.domain.service.product.api.ProductRequest;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.product.view.ProductCard;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.domain.service.profile.ProfileService;
import su.reddot.domain.service.profile.ProfileView;
import su.reddot.domain.service.publication.PublicationService;
import su.reddot.domain.service.publication.exception.PublicationException;
import su.reddot.domain.service.subscription.SubscriptionService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductApi {

    private final ProductService     productService;
    private final ProductItemService productItemService;
    private final UserService        userService;
    private final CategoryService    categoryService;
    private final CommentService     commentService;
    private final ProfileService     profileService;
    private final LikeService        likeService;
    private final SubscriptionService subscriptionService;
    private final PublicationService publicationService;

    private final SpringTemplateEngine      templateEngine;
    private final ServletContext            servletContext;
    private final ApplicationEventPublisher pub;

    /**
     * @return товары, которые удовлетворяют
     * критериям фильтрации.
     */
    @GetMapping
    public Products getProducts(ProductRequest request, UserIdAuthenticationToken token) {

        Optional<SortAttribute> currentSort
                = SortAttribute.of(request.getSort());

        List<Long> productCategories = request.getCategory() != null?
                categoryService.getLeafCategoriesIds(request.getCategory())
                : Collections.emptyList();

        FilterSpecification spec
                = new FilterSpecification()
                .categoriesIds(productCategories)
                .sellerId(request.getSeller())
                //TODO реализовать логику при запросе всех товаров так, чтобы не сломать актуальную работу
                .state(request.getState() == null? ProductState.PUBLISHED : request.getState())
                .itemState(ProductItem.State.INITIAL)
                .interestingAttributeValues(request.getFilter())
                .interestingSizes(request.getSize())
                .interestingBrands(request.getBrand())
                .interestingConditions(request.getProductCondition())

                .isVintage(request.getVintage())
                .isOnSale(request.getOnSale())
                .hasOurChoice(request.getOurChoice())
                .isNewCollection(request.getNewCollection());

        SortAttribute sortAttribute = currentSort.orElse(SortAttribute.PUBLISH_TIME_DESC);

        User nullableUser = token != null? userService.getUserById(token.getUserId()).get() : null;
        ViewQualification viewSettings = new ViewQualification()
                .interestingUser(nullableUser)
                .interestingSizeType(request.getSizeType())
                .withSavings(true);

        ProductsList productsList = productService.getProductsList(spec,
                request.getPage() == null ? 1 : request.getPage(),
                sortAttribute, viewSettings);

        AvailableFilters availableFilters = new AvailableFilters(); //productService.getAvailableFilters(spec, request.getCategory());

        return new Products(
                renderProductsToHtml(productsList.getProducts()),
                productsList.getTotalPages(),
                productsList.getProductsTotalAmount(),
                availableFilters);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MODERATION')")
    //TODO: вынести метод в ModerationController
    //TODO: не менять состояние, если старое состояние не на модерации
    public ResponseEntity<Void> updateProductState(
            @PathVariable Long id,
            @RequestParam ProductState state) {

        productService.updateState(id, state);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/discount")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> changePrice(@PathVariable Long id,
                                         @RequestParam BigDecimal priceWithDiscount,
                                         UserIdAuthenticationToken t)
            throws IllegalAccessException, PublicationException {

        User viewingUser = userService.getUserById(t.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Product product = productService.getRawProduct(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар с id " + id + " не найден"));

        if (!product.getSeller().equals(viewingUser)) {
            throw new IllegalAccessException("Отказано в доступе");
        }

        if (product.isDeleted()) {
            throw new IllegalArgumentException("Невозможно изменить цену товара: товар удален");
        }

        if (product.getProductState() == ProductState.NEED_MODERATION) {
            publicationService.updatePrice(product, priceWithDiscount);
            return ResponseEntity.ok().build();
        }

        ProductItem productItem = productItemService.findFirstByProduct(product);
        BigDecimal currentPrice = productItem.getCurrentPrice();
        BigDecimal acceptPrice = currentPrice.subtract(currentPrice.multiply(BigDecimal.valueOf(0.05)));

        if (priceWithDiscount.compareTo(acceptPrice) > 0) {
            throw new IllegalArgumentException("Вы можете снизить цену не менее 5% от текущей цены");
        }

        publicationService.setDiscount(product, priceWithDiscount);


        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, UserIdAuthenticationToken token) {
        Long userId = token.getUserId();

        Optional<DetailedProduct> detailedProduct = productService.getProduct(id);

        if (!detailedProduct.isPresent()) {
            throw new IllegalArgumentException("Товар не найден!");
        }

        su.reddot.domain.model.product.Product product = detailedProduct.get().getProduct();

        Long sellerId = product.getSeller().getId();

        if (!sellerId.equals(userId)) {
            throw new IllegalArgumentException("Отказано в доступе!");
        }

        if (product.isDeleted()) {
            throw new IllegalArgumentException("Товар уже удален!");
        }

        productService.delete(product);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> confirmProductItemSale(@PathVariable Long id,
                                                       @RequestBody(required = false) SellerRequisite pickupRequisite,
                                                       @RequestParam boolean doConfirmSale,
                                                       UserIdAuthenticationToken t) {

        User seller = userService.getUserById(t.getUserId()).get();
        productItemService.confirmSale(id, seller, doConfirmSale, pickupRequisite);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}/comments")
    public CommentsList getComments(@PathVariable Long productId, TimeZone timezone,
                                    HttpServletResponse response, HttpServletRequest request,
                                    UserIdAuthenticationToken token) {

        List<CommentView> comments = commentService.getComments(productId, timezone);
        return new CommentsList(
                renderCommentsToHtml(comments, request, response, token, "product/comments"),
                comments.size());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/comment")
    public CommentsList publishComment(
            @PathVariable Long id,
            @RequestParam String text,
            UserIdAuthenticationToken token,
            HttpServletResponse response,
            HttpServletRequest request,
            TimeZone timezone
    ) {
        CommentView comment = commentService.publishComment(text, token.getUserId(), id, timezone);

        List<CommentView> comments = Collections.singletonList(comment);

        return new CommentsList(
                renderCommentsToHtml(comments, request, response, token, "product/comment"),
                comments.size());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/like")
    public boolean like(
            @PathVariable Long id,
            UserIdAuthenticationToken token
    ) {
        DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
        User user = userService.getUserById(token.getUserId()).get();
        return likeService.like(detailedProduct.getProduct(), user);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/dislike")
    public boolean dislike(
            @PathVariable Long id,
            UserIdAuthenticationToken token
    ) {
        DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
        User user = userService.getUserById(token.getUserId()).get();
        return likeService.dislike(detailedProduct.getProduct(), user);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/like")
    public ToggleResult toggle(@PathVariable Long id, UserIdAuthenticationToken t) {

        User u = userService.getUserById(t.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Пользователь " + t.getUserId() + "не существует"));

        su.reddot.domain.model.product.Product product = productService.getRawProduct(id).orElseThrow(
                () -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));

        return likeService.toggle(product, u);
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping("/{id}/pricesubscription")
    public boolean subscribeOnPrice(
            @PathVariable Long id,
            UserIdAuthenticationToken token
    ) {
        DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
        User user = userService.getUserById(token.getUserId()).get();
        return subscriptionService.subscribeOnPrice(user, detailedProduct.getProduct());
    }

    @PreAuthorize("isFullyAuthenticated()")
    @DeleteMapping("/{id}/pricesubscription")
    public boolean unsubscribeOnPrice(
            @PathVariable Long id,
            UserIdAuthenticationToken token
    ) {
        DetailedProduct detailedProduct = productService.getProduct(id).orElseThrow(() -> new IllegalArgumentException("Товар c идентификатором: " + id + " не существует"));
        User user = userService.getUserById(token.getUserId()).get();
        return subscriptionService.unsubscribeOnPrice(user, detailedProduct.getProduct());
    }

    /** Предложить новую цену в рамках торга. */
    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping("/{id}/offer")
    public ResponseEntity<?> makeOffer(@PathVariable Long id, @RequestBody OfferRequest offerRequest, UserIdAuthenticationToken t)
            throws NotFoundException, PriceNegotiationException {

        //noinspection ConstantConditions
        User user = userService.getUserById(t.getUserId()).get();

        BigDecimal validatedNewPrice;
        try { validatedNewPrice = new BigDecimal(offerRequest.getPrice()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Цена должна иметь числовое значение (например, 100 или 200,70)"); }

        productService.makeAnOffer(id, validatedNewPrice, user);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PutMapping("/offers/{id}")
    public ResponseEntity<?> confirmOffer(@PathVariable Long id, @RequestParam boolean confirm,
                                          UserIdAuthenticationToken t) {

        User seller = userService.getUserById(t.getUserId()).get();
        productService.confirmOffer(id, seller, confirm);

        return ResponseEntity.ok().build();
    }

    private String renderProductsToHtml(List<ProductCard> products) {

        // Правильнее возвращать специально подготовленную для этого случая страницу
        if (products.isEmpty()) { return "<h4>Нет товаров</h4>"; }

        Context context = new Context();

        context.setVariable("products", products);
        return templateEngine.process("catalog/product_card",
                new HashSet<>(Collections.singletonList("[th:fragment='product_card (products, additionalContainerClass)']")),
                context);
    }

    private String renderCommentsToHtml(List<CommentView> comments,
                                        HttpServletRequest request, HttpServletResponse response,
                                        UserIdAuthenticationToken token,
                                        String template) {

        WebContext context = new WebContext(request, response, servletContext);
        context.setVariable("comments", comments);
        context.setVariable("total", comments.size());
        if (token != null) {
            ProfileView profileView = profileService.getProfileView(token.getUserId());
            context.setVariable("profile", profileView);
        }

        return templateEngine.process(template,
                new HashSet<>(Collections.singletonList("[th:fragment='comments']")),
                context);
    }

    @AllArgsConstructor @Getter @Setter
    private static class Products {

        private final String content;

        private final int totalPages;

        /** Общее число товаров в выборке. */
        private final long totalAmount;

        /** Информация по доступным фильтрам **/
        private final AvailableFilters availableFilters;


    }

    @AllArgsConstructor @Getter @Setter
    private static class CommentsList {
        private final String content;
        private final int total;
    }

    @Getter @Setter
    private static class OfferRequest {
        private String price;
    }
}
