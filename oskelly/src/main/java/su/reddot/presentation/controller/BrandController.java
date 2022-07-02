package su.reddot.presentation.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.brand.BrandService;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductService.FilterSpecification;
import su.reddot.domain.service.product.ProductService.SortAttribute;
import su.reddot.domain.service.product.ProductService.ViewQualification;
import su.reddot.domain.service.product.view.ProductsList;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class BrandController {
    private final BrandService    brandService;
    private final ProductService  productService;
    private final CategoryService categoryService
            ;

    @GetMapping("/brands")
    public String getBrandsIndexPage(Model model){

        HashMap<String, List<Brand>> groupsWithBrands = brandService.getBrandsInGroups();
        model.addAttribute("groupsWithBrands",groupsWithBrands);

        return "brand/index";
    }

    @GetMapping("/brands/{url}")
    public String getBrandPage(@PathVariable("url") String url,
                               @ModelAttribute("request") BrandPageRequest request,
                               Model model) throws NotFoundException {

        //FIXME пробросить ошибку в шаблон страницы бренда
        Brand brand = brandService.getByUrl(url)
                    .orElseThrow(() -> new NotFoundException("404:  /brands/"+ url + " not found"));

        model.addAttribute("brand", brand);

        SortAttribute sortAttribute = SortAttribute.of(request.getSort())
                .orElse(SortAttribute.PUBLISH_TIME_DESC);

        request.setSort(sortAttribute.getParameterName());

        FilterSpecification spec = new FilterSpecification()
                .state(ProductState.PUBLISHED)
                .itemState(ProductItem.State.INITIAL)
                .interestingBrands(Collections.singletonList(brand.getId()))

                .interestingAttributeValues(request.getFilter())
                .interestingConditions(request.getProductCondition())
                .interestingSizes(request.getSize())

                .isVintage(request.getVintage())
                .isOnSale(request.getOnSale())
                .hasOurChoice(request.getOurChoice())
                .isNewCollection(request.getNewCollection());

        Long interestingCategoryId = request.getCategory();
        if (interestingCategoryId != null) {
            Optional<CatalogCategory> optionalInterestingCategory = categoryService.findById(interestingCategoryId);

            if (optionalInterestingCategory.isPresent()) {
                CatalogCategory interestingCategory = optionalInterestingCategory.get();
                List<CatalogCategory> parentCategories = categoryService.getAllParentCategories(interestingCategoryId);

                List<Long> productCategories = categoryService.getLeafCategoriesIds(interestingCategoryId);
                spec.categoriesIds(productCategories);

                model.addAttribute("selectedCategory", interestingCategory);
                model.addAttribute("parentCategories", parentCategories);

                /* Атрибуты, по которым можно фильтровать выбранные товары
                * доступны, только если ты выбрал конкретную категорию */
                Map<Attribute, List<AttributeValue>> actualAttributeValues
                        = brandService.getActualAttributeValues(brand, request.getCategory());
                model.addAttribute("attributes", actualAttributeValues);

                /* Фильтрация товаров по размерам возвожна только если ты выбрал конкретную категорию */
                List<CatalogSize> sizes = brandService.getActualSizes(brand, interestingCategoryId);
                if (!sizes.isEmpty() && !(sizes.get(0).getSizeType() == SizeType.NO_SIZE)) {

                    List<SizeType> actualSizeTypes = sizes.stream().map(CatalogSize::getSizeType).collect(Collectors.toList());
                    model.addAttribute("sizeTypes", actualSizeTypes);

                    /* Размеры в той сетке, которую выбрал пользователь.
                    Если пользователь сетку не выбрал, использовать ту, которая объявлена первой в перечислении SizeType.
                    Аналогично вести себя, когда пользователь выбрал несуществующую в системе сетку.
                    */
                    Optional<CatalogSize> sizesBySizeType = sizes.stream().filter(s -> s.getSizeType() == request.getSizeType()).findFirst();
                    if (sizesBySizeType.isPresent()) {
                        model.addAttribute("sizesBySizeType", sizesBySizeType.get().getValues());
                        model.addAttribute("requestedSizeType", request.getSizeType());
                    }
                    else {
                        model.addAttribute("sizesBySizeType", sizes.get(0).getValues());
                        model.addAttribute("requestedSizeType", sizes.get(0).getSizeType());
                    }
                }
            }
        }

        ProductsList productsList = productService.getProductsList(spec, 1, sortAttribute,
                new ViewQualification().withSavings(true));

        if (!productsList.getProducts().isEmpty()) {
            model.addAttribute("productsList", productsList);
        }

        List<Category> subcategories = brandService.getActualCategories(brand, request.getCategory());
        model.addAttribute("subcategories", subcategories);

        List<ProductCondition> actualConditions = brandService.getActualConditions(brand, request.getCategory());
        model.addAttribute("conditions", actualConditions);


        boolean vintageProductsArePresent = brandService.getVintageProductsPresence(brand, request.getCategory());
        boolean newCollectionProductsArePresent = brandService.getNewCollectionProductsPresence(brand, request.getCategory());
        boolean productsWithOurChoiceArePresent = brandService.getProductsWithOurChoicePresence(brand, request.getCategory());
        boolean saleProductsArePresent = brandService.getSaleProductsPresence(brand, request.getCategory());

        if (vintageProductsArePresent || newCollectionProductsArePresent
                || productsWithOurChoiceArePresent || saleProductsArePresent) {

            /* отображать блок отметок: передача в модель готового решения упрощает шаблон, в отличие от вычисления в самом шаблоне */
            model.addAttribute("tags", true);

            if (vintageProductsArePresent) {
                model.addAttribute("vintageTag", true);
            }
            if (newCollectionProductsArePresent) {
                model.addAttribute("newCollectionTag", true);
            }
            if (productsWithOurChoiceArePresent) {
                model.addAttribute("ourChoiceTag", true);
            }
            if (saleProductsArePresent) {
                model.addAttribute("saleTag", true);
            }
        }

        return "brand/page";
    }

    @Getter @Setter
    private static class BrandPageRequest {

        private String sort;

        private List<Long> filter = Collections.emptyList();

        private List<Long> size = Collections.emptyList();

        private SizeType sizeType;

        private List<Long> productCondition = Collections.emptyList();

        private Long category;

        /* Фильтрация товаров по булевым атрибутам (тегам / отметкам) */
        private Boolean vintage;
        private Boolean ourChoice;
        private Boolean onSale;
        private Boolean newCollection;

    }
}
