package su.reddot.domain.service.product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import su.reddot.domain.dao.SizeRepository;
import su.reddot.domain.dao.attribute.AttributeValueRepository;
import su.reddot.domain.dao.product.*;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.cart.ShoppingCartService;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.image.ImageService;
import su.reddot.domain.service.product.item.ProductItemService;
import su.reddot.domain.service.wishlist.WishListService;
import su.reddot.presentation.view.product.ProductView;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductItemRepository productItemRepository;

    @Mock
    private SizeRepository sizeRepository;

    @Mock
    private ProductStatusBindingRepository productStatusBindingRepository;

    @Mock
    private ProductAttributeValueBindingRepository productAttributeValueBindingRepository;

    @Mock
    private AttributeValueRepository attributeValueRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private ProductItemService productItemService;

    @Mock
    private WishListService wishListService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ShoppingCartService cartService;

    @Mock
    private ProductConditionRepository productConditionRepository;

    private ProductService productService;

    @Before
    public void init() {
        productService = new DefaultProductService(
                productRepository,
                productItemRepository,
                sizeRepository,
                productStatusBindingRepository,
                productAttributeValueBindingRepository,
                attributeValueRepository,
                productConditionRepository,
                imageService,
                productItemService,
                wishListService,
                categoryService,
                cartService
        );
    }

    /**
     * Товара с запрашиваемым идентификатором физически нет в системе
     */
    @Test
    public void getProductView_productNotFound_returnEmpty() {

        Long nonExistingProductId = 42L;
        given(productRepository.findOne(nonExistingProductId)).willReturn(null);

        Optional<ProductView> productView = productService.getProductView(nonExistingProductId, null);

        assertTrue(!productView.isPresent());
    }

    /**
     * Товара есть, но недоступен для отображения
     */
    @Test
    public void getProductView_productUnavailableForViewing_returnEmpty() {

        Long existingProductId = 42L;
        Product nonViewableProduct = mock(Product.class);
        given(nonViewableProduct.isAvailable()).willReturn(false);
        given(productRepository.findOne(existingProductId)).willReturn(nonViewableProduct);

        Optional<ProductView> productView = productService.getProductView(existingProductId, null);

        assertTrue(!productView.isPresent());
    }

    /**
     * Товар уже продан ИЛИ у товара нет вещей доступных для продажи =>
     *
     * 1) не отображать доступные размеры, текущую и рекомендованную (RRP) цены
     * 2) товар нельзя добавить в корзину
     */
    @Test
    public void getProductView_productIsAlreadySold_returnPartialProductView() {

        Long soldOutProductId = 1L;
        Product soldOutProduct = defaultProduct();
        soldOutProduct.setProductState(ProductState.SOLD);

        given(productRepository.findOne(soldOutProductId)).willReturn(soldOutProduct);
        given(imageService.getPrimaryImage(soldOutProduct)).willReturn(Optional.empty());
        given(imageService.getProductImages(soldOutProduct)).willReturn(new ArrayList<>());

        /* Даже наличие доступных вещей у товара, статус которого не позволяет его продавать,
        не должно влиять на факт невозможности покупки этого товара */
        given(productItemService.getAvailableItemsSummary(soldOutProduct))
                .willReturn(new ArrayList<>());

        Optional<ProductView> productView = productService.getProductView(soldOutProductId, null);

        boolean productViewHasNoPriceAndSizeInformationAndProductCantBeAddedToCart
                = productView.isPresent() && productView.get().getCurrentPrice() == null
                && productView.get().getRrp() == null
                && !productView.get().isCanBeAddedToCart()
                && "Нет в продаже".equals(productView.get().getReasonWhyItCannotBeAddedToCart())
                && productView.get().getSizes().size() == 0;
        assertTrue(productViewHasNoPriceAndSizeInformationAndProductCantBeAddedToCart);
    }

    @Test
    public void getProductView_productHasNoItemsAvailableForSale_returnPartialProductView() {

        Long id = 1L;
        Product productWithNoItemsAvailble = defaultProduct();
        productWithNoItemsAvailble.setProductState(ProductState.PUBLISHED);

        given(productRepository.findOne(id)).willReturn(productWithNoItemsAvailble);
        given(imageService.getPrimaryImage(productWithNoItemsAvailble)).willReturn(Optional.empty());
        given(imageService.getProductImages(productWithNoItemsAvailble)).willReturn(new ArrayList<>());
        given(productItemService.getAvailableItemsSummary(productWithNoItemsAvailble)).willReturn(new ArrayList<>());

        Optional<ProductView> productView = productService.getProductView(id, null);

        boolean productViewHasNoPriceAndSizeInformationAndProductCantBeAddedToCart
                = productView.isPresent() && productView.get().getCurrentPrice() == null
                && productView.get().getRrp() == null
                && !productView.get().isCanBeAddedToCart()
                && "Нет в продаже".equals(productView.get().getReasonWhyItCannotBeAddedToCart())
                && productView.get().getSizes().size() == 0;
        assertTrue(productViewHasNoPriceAndSizeInformationAndProductCantBeAddedToCart);
    }



    private Product defaultProduct() {
        Product p = new Product();
        p.setId(1L);
        p.setCategory(category());
        p.setProductCondition(productCondition());
        p.setBrand(brand());
        p.setDeliveryDescription("Delivery description");
        p.setDescription("Description");
        p.setModel("Model");
        p.setName("Name");
        p.setOrigin("Origin");
        p.setPaymentDescription("Payment description");
        p.setSeller(seller());

        return p;
    }

    private Brand brand() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand");

        return brand;
    }

    private Category category() {
        Category category = new Category();
        category.setDisplayName("Category");
        return category;
    }

    private ProductCondition productCondition() {
        ProductCondition condition = new ProductCondition();
        condition.setId(1L);
        condition.setName("Condition name");
        condition.setDescription("Condition description");
        return condition;
    }

    private User seller() {
        User seller = new User();
        seller.setEmail("user@acme.com");
        seller.setRegistrationTime(ZonedDateTime.now());
        return seller;
    }

    private List<AttributeValue> attributeValues() {

        ArrayList<AttributeValue> values = new ArrayList<>();
        for (Long attrId: new long[] {1, 2}) {
            Attribute attribute = new Attribute();
            attribute.setId(attrId);
            attribute.setName("Attribute " + attrId);

            for (Long valueId: new long[] {1, 2}) {
                AttributeValue value = new AttributeValue();
                value.setId(valueId);
                value.setAttribute(attribute);
                value.setValue("Attribute " + attrId + " Value " + valueId);
            }
        }

        return values;
    }
}
