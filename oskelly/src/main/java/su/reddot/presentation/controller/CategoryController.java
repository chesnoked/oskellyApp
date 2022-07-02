package su.reddot.presentation.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import su.reddot.domain.dao.BrandRepository;
import su.reddot.domain.dao.attribute.AttributeRepository;
import su.reddot.domain.dao.attribute.AttributeValueRepository;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.Brand;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.product.ProductCondition;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.catalog.CatalogAttribute;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.product.ProductService;
import su.reddot.domain.service.product.ProductService.FilterSpecification;
import su.reddot.domain.service.product.ProductService.SortAttribute;
import su.reddot.domain.service.product.view.ProductsList;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static su.reddot.domain.service.product.ProductService.ViewQualification;

@Controller
@RequiredArgsConstructor
@RequestMapping("/catalog")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService  productService;
    private final UserService     userService;
    private final BrandRepository brandRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final AttributeRepository attributeRepository;

    /** Получить товары в категории, которая представлена в читаемом виде. */
    @GetMapping("/**")
    public String getCategory(@ModelAttribute("request") CategoryRequest request,
                              Model m, UserIdAuthenticationToken token, HttpServletRequest r)
            throws NotFoundException {

        /**
         * Прелсиавляем весь URL в виде Строки L = D/C/B/A
         * Проверяем, задает ли подстрока A бренда, цвет или материал.
         * Если нет - проверяем, задает ли вся строка L категорию.
         * Если Да - отдаем без фильтров, если нет - то 404.
         * При проверке A,B,С учитываем, что относительный порядок сохраняется всегда Если А - это материал, то B-
         */
        String url = (String) r.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String categoryUrl =  url.replaceFirst("/catalog/", "");

        CatalogCategory thisCategory;
        Brand brandFromPath;
        Optional<AttributeValue> colourAttributeValue = Optional.empty();
        Optional<List<AttributeValue>> foundAttValues = Optional.empty();
        Optional<AttributeValue> someMaterialAttributeOrElseAttributeValue = Optional.empty();

        Pattern basePattern = Pattern.compile("([a-z,0-9,\\&,-,',-]+)$", Pattern.CASE_INSENSITIVE);
        Matcher aPathMatcher = basePattern.matcher(url);

        String a_substring = aPathMatcher.find() ? aPathMatcher.group(0) : null;

        MatchInDictonariesResult aString = new MatchInDictonariesResult(a_substring);
        aString.Match(brandRepository, attributeValueRepository);

        if (aString.isBrand) {
            brandFromPath = aString.optionalBrand.get();
            request.brand = Collections.singletonList(brandFromPath.getId());
            categoryUrl =  url.replaceFirst("/catalog/", "").replaceFirst("/" + aString.value +"$","");
        }

        if(aString.isColour || aString.isMaterial){
            if(aString.isColour){ colourAttributeValue = aString.colourValue; }
            if(aString.isMaterial){ foundAttValues = aString.materialValues; }
            categoryUrl = url.replaceFirst("/catalog/", "").replaceFirst("/" + aString.value +"$","");

            Matcher bPathMatcher = basePattern.matcher(url.replaceFirst("\\/([a-z,0-9,\\&,-,',-]+)$",""));
            String b_substring = bPathMatcher.find() ? bPathMatcher.group(0) : null;

            MatchInDictonariesResult bString = new MatchInDictonariesResult(b_substring);
            bString.Match(brandRepository, attributeValueRepository);

            if(bString.isBrand){
                brandFromPath = bString.optionalBrand.get();
                request.brand = Collections.singletonList(brandFromPath.getId());
                categoryUrl =  url.replaceFirst("/catalog/", "")
                        .replaceFirst("/" + aString.value + "$","")
                        .replaceFirst("/" + bString.value + "$","");
            }

            if (bString.isColour || bString.isMaterial){
                if(bString.isColour){ colourAttributeValue = bString.colourValue;}
                if(bString.isMaterial) { foundAttValues = bString.materialValues;}
                categoryUrl =  url.replaceFirst("/catalog/", "")
                        .replaceFirst("/" + aString.value + "$","")
                        .replaceFirst("/" + bString.value + "$","");

                Matcher cPathMatcher = basePattern.matcher(url.replaceFirst("\\/([a-z,0-9,\\&,-,',-]+)$","")
                                                                .replaceFirst("\\/([a-z,0-9,\\&,-,',-]+)$",""));
                String c_substring = cPathMatcher.find() ? cPathMatcher.group(0) : null;

                MatchInDictonariesResult cString = new MatchInDictonariesResult(c_substring);
                cString.Match(brandRepository, attributeValueRepository);

                if(cString.isBrand){
                    brandFromPath = cString.optionalBrand.get();
                    request.brand = Collections.singletonList(brandFromPath.getId());
                    categoryUrl =  url.replaceFirst("/catalog/", "")
                            .replaceFirst("/" + aString.value + "$","")
                            .replaceFirst("/" + bString.value + "$","")
                            .replaceFirst("/" + cString.value + "$","");
                }

                if(cString.isColour || cString.isMaterial){
                    if(cString.isColour) { colourAttributeValue = cString.colourValue; }
                    if(cString.isMaterial) { foundAttValues = cString.materialValues; }
                    categoryUrl =  url.replaceFirst("/catalog/", "")
                            .replaceFirst("/" + aString.value + "$","")
                            .replaceFirst("/" + bString.value + "$","")
                            .replaceFirst("/" + cString.value + "$","");
                }



            }

        }


        String finalCategoryUrl = categoryUrl;

        thisCategory = categoryService.findByUrl(categoryUrl)
                .orElseThrow(() -> new NotFoundException("404: " + finalCategoryUrl + " not found"));




        List<CatalogAttribute> thisCatAttributes = categoryService.getAllAttributes(thisCategory.getId());

        //10 - это магическое число, id атрибута цвет в боевой базе.
        Optional<CatalogAttribute> colourAttribute = thisCatAttributes.stream().filter(a -> a.getAttribute().getId().equals(10L)).findFirst();
        //FIXME очень навино ожидать что тут может быть именно материал(один из) но пока так пойдет
        Optional<CatalogAttribute> someMaterialAttributeOrElse = thisCatAttributes.stream().filter(a -> !a.getAttribute().getId().equals(10L)).findFirst();



        if(colourAttributeValue.isPresent()){
            if(colourAttribute.isPresent()){
                colourAttributeValue = attributeValueRepository.findFirstByAttributeAndTransliterateValue(colourAttribute.get().getAttribute(), colourAttributeValue.get().getTransliterateValue());
            }
        }

        if (foundAttValues.isPresent()){
            if(someMaterialAttributeOrElse.isPresent()){
                someMaterialAttributeOrElseAttributeValue = attributeValueRepository.findFirstByAttributeAndTransliterateValue(
                                                                                        someMaterialAttributeOrElse.get().getAttribute(),
                                                                                        foundAttValues.get().stream().findFirst().get().getTransliterateValue());
            }
        }
        List<Long> attributesId = new ArrayList<>();
        colourAttributeValue.ifPresent(attributeValue -> attributesId.add(attributeValue.getId()));
        someMaterialAttributeOrElseAttributeValue.ifPresent(attributeValue -> attributesId.add(attributeValue.getId()));

        request.filter = attributesId;
        if((request.filter.size() > 0) || (!request.brand.isEmpty())){
            r.setAttribute("filteredRequest", request);
        }



        return "forward:/catalog/" + thisCategory.getId();
    }

    /**
     * Получить постранично список <b>опубликованных</b> товаров
     * в заданной категории каталога и ее подкатегориях.
     */
    @GetMapping("/{id:\\d+}")
    public String getCategory(Model m, UserIdAuthenticationToken token,
                              @PathVariable String id,
                              @ModelAttribute("request") CategoryRequest request, HttpServletRequest r) throws NotFoundException {

        CategoryRequest modifiedRequest = (CategoryRequest) r.getAttribute("filteredRequest");
        if(modifiedRequest != null){
            request = (CategoryRequest) r.getAttribute("filteredRequest");
        }

        Long categoryId = Long.valueOf(id);

        CatalogCategory thisCategory = categoryService.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("404:  /catalog/" + categoryId + " not found"));

        SortAttribute sortAttribute
                = SortAttribute.of(request.getSort()).orElse(SortAttribute.PUBLISH_TIME_DESC);

        request.setSort(sortAttribute.getParameterName());

        /* Навигационная цепочка */
        List<CatalogCategory> parentCategories
                = categoryService.getAllParentCategories(categoryId);
        m.addAttribute("parentCategories", parentCategories);
        m.addAttribute("thisCategory", thisCategory);

        /* Фильтрация товаров по размерам */
        List<CatalogSize> sizes = productService.getActualSizes(categoryId);
		if (!sizes.isEmpty() && !(sizes.get(0).getSizeType() == SizeType.NO_SIZE)) {

            List<SizeType> actualSizeTypes = sizes.stream().map(CatalogSize::getSizeType).collect(Collectors.toList());
            m.addAttribute("sizeTypes", actualSizeTypes);

            /* Размеры в той сетке, которую выбрал пользователь.
            Если пользователь сетку не выбрал, использовать ту, которая объявлена первой в перечислении SizeType.
            Аналогично вести себя, когда пользователь выбрал несуществующую в системе сетку.
            */
            CategoryRequest finalRequest = request;
            Optional<CatalogSize> sizesBySizeType = sizes.stream().filter(s -> s.getSizeType() == finalRequest.getSizeType()).findFirst();
            if (sizesBySizeType.isPresent()) {
                m.addAttribute("sizesBySizeType", sizesBySizeType.get().getValues());
                m.addAttribute("requestedSizeType", request.getSizeType());
            }
            else {
                m.addAttribute("sizesBySizeType", sizes.get(0).getValues());
                m.addAttribute("requestedSizeType", sizes.get(0).getSizeType());
            }
        }

        List<CatalogCategory> subcategories
                = categoryService.getDirectChildrenCategories(categoryId, true);
        if (subcategories.size() > 0) {
            m.addAttribute("subcategories", subcategories);
        }

        /* Список товаров либо в выбранной категории (если она является конечной),
         * либо в ее дочерних категориях - листьях
         */
        List<Long> productCategories = categoryService.getLeafCategoriesIds(categoryId);

        FilterSpecification spec = new FilterSpecification()
                .categoriesIds(productCategories)
                .state(ProductState.PUBLISHED)
                .itemState(ProductItem.State.INITIAL)
                .interestingAttributeValues(request.getFilter())
                .interestingSizes(request.getSize())
                .interestingBrands(request.getBrand())
                .interestingConditions(request.getProductCondition())

                .isVintage(request.getVintage())
                .isOnSale(request.getOnSale())
                .hasOurChoice(request.getOurChoice())
                .isNewCollection(request.getNewCollection());

        User user = token != null? userService.getUserById(token.getUserId()).get() : null;
        ViewQualification viewSettings = new ViewQualification()
                .interestingUser(user)
                .interestingSizeType(request.getSizeType())
                .withSavings(true);

        int currentPage = request.getPage();
        ProductsList productsList = productService.getProductsList(spec, currentPage, sortAttribute, viewSettings);

        if (productsList.getProducts().size() > 0) {
            m.addAttribute("productsList", productsList);
        }

        int totalPages = productsList.getTotalPages();
        if (currentPage < totalPages) {
            m.addAttribute("nextPage", currentPage + 1);
        }
        if (currentPage > 1 && currentPage <= totalPages) {
            m.addAttribute("prevPage", currentPage - 1);
        }

        List<Brand> brands = productService.getActualBrands(productCategories);
        if (!brands.isEmpty()) { m.addAttribute("brands", brands); }

        List<ProductCondition> actualConditions = productService.getActualConditions(productCategories);
        if (actualConditions.size() > 1) {
            m.addAttribute("productConditions", actualConditions);
        }

        /* Атрибуты, по которым можно фильтровать выбранные товары */
        Map<Attribute, List<AttributeValue>> actualAttributeValues = productService.getActualAttributeValues(categoryId);
        m.addAttribute("thisCategoryAttributes", actualAttributeValues);

        boolean vintageProductsArePresent = productService.getVintageProductsPresence(productCategories);
        boolean newCollectionProductsArePresent = productService.getNewCollectionProductsPresence(productCategories);
        boolean productsWithOurChoiceArePresent = productService.getProductsWithOurChoicePresence(productCategories);
        boolean saleProductsArePresent = productService.getSaleProductsPresence(productCategories);
        if (vintageProductsArePresent || newCollectionProductsArePresent || productsWithOurChoiceArePresent || saleProductsArePresent) {

            /* отображать блок отметок: передача в модель готового решения упрощает шаблон, в отличие от вычисления в самом шаблоне */
            m.addAttribute("tags", true);

            if (vintageProductsArePresent) {
                m.addAttribute("vintageTag", true);
            }
            if (newCollectionProductsArePresent) {
                m.addAttribute("newCollectionTag", true);
            }
            if (productsWithOurChoiceArePresent) {
                m.addAttribute("ourChoiceTag", true);
            }
            if (saleProductsArePresent) {
                m.addAttribute("saleTag", true);
            }
        }

        return "catalog/page";
    }

    /**
     * @return раздел "Новинки"
     */
    @GetMapping("/just-in")
    public String justInCategory(Model m, @ModelAttribute("request") CategoryRequest request) {

        /* FIXME отвратительный копипаст, нормальное апи необходимо */

        FilterSpecification spec = new FilterSpecification()
                .state(ProductState.PUBLISHED)
                .itemState(ProductItem.State.INITIAL)
                .interestingAttributeValues(request.getFilter())
                .interestingConditions(request.getProductCondition())
                .interestingBrands(request.getBrand())
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
                List<CatalogCategory> subcategories = categoryService.getDirectChildrenCategories(interestingCategoryId, true);

                List<Long> productCategories = categoryService.getLeafCategoriesIds(interestingCategoryId);
                spec.categoriesIds(productCategories);

                m.addAttribute("thisCategory", interestingCategory);
                m.addAttribute("parentCategories", parentCategories);
                m.addAttribute("subcategories", subcategories);

                /* Атрибуты, по которым можно фильтровать выбранные товары */
                Map<Attribute, List<AttributeValue>> actualAttributeValues = productService.getActualAttributeValues(interestingCategoryId);
                m.addAttribute("thisCategoryAttributes", actualAttributeValues);

                /* Фильтрация товаров по размерам */
                List<CatalogSize> sizes = productService.getActualSizes(interestingCategoryId);
                if (!sizes.isEmpty() && !(sizes.get(0).getSizeType() == SizeType.NO_SIZE)) {

                    List<SizeType> actualSizeTypes = sizes.stream().map(CatalogSize::getSizeType).collect(Collectors.toList());
                    m.addAttribute("sizeTypes", actualSizeTypes);

                    /* Размеры в той сетке, которую выбрал пользователь.
                    Если пользователь сетку не выбрал, использовать ту, которая объявлена первой в перечислении SizeType.
                    Аналогично вести себя, когда пользователь выбрал несуществующую в системе сетку.
                    */
                    Optional<CatalogSize> sizesBySizeType = sizes.stream().filter(s -> s.getSizeType() == request.getSizeType()).findFirst();
                    if (sizesBySizeType.isPresent()) {
                        m.addAttribute("sizesBySizeType", sizesBySizeType.get().getValues());
                        m.addAttribute("requestedSizeType", request.getSizeType());
                    }
                    else {
                        m.addAttribute("sizesBySizeType", sizes.get(0).getValues());
                        m.addAttribute("requestedSizeType", sizes.get(0).getSizeType());
                    }
                }
            }
        }
        else {
            /* Если пользователь не выбрал категорию, показывать товары во всех категориях.
               Атрибуты фильтрации и размеры не показывать. Их можно выбрать только для конкретной категории.
            * */
            List<CatalogCategory> entireCatalog = categoryService.getEntireCatalog();
            m.addAttribute("subcategories", entireCatalog);
        }

        ViewQualification viewSettings = new ViewQualification()
                .interestingSizeType(request.getSizeType())
                .withSavings(true);

        ProductsList productsList = productService.getProductsList(spec, 1, SortAttribute.PUBLISH_TIME_DESC, viewSettings);
        m.addAttribute("productsList", productsList);

        List<Brand> brands = productService.getActualBrands(spec.categoriesIds());
        if (!brands.isEmpty()) { m.addAttribute("brands", brands); }

        List<ProductCondition> actualConditions = productService.getActualConditions(spec.categoriesIds());
        if (actualConditions.size() > 1) {
            m.addAttribute("productConditions", actualConditions);
        }

        boolean vintageProductsArePresent = productService.getVintageProductsPresence(spec.categoriesIds());
        boolean newCollectionProductsArePresent = productService.getNewCollectionProductsPresence(spec.categoriesIds());
        boolean productsWithOurChoiceArePresent = productService.getProductsWithOurChoicePresence(spec.categoriesIds());
        boolean saleProductsArePresent = productService.getSaleProductsPresence(spec.categoriesIds());
        if (vintageProductsArePresent || newCollectionProductsArePresent || productsWithOurChoiceArePresent || saleProductsArePresent) {

            /* отображать блок отметок: передача в модель готового решения упрощает шаблон, в отличие от вычисления в самом шаблоне */
            m.addAttribute("tags", true);

            if (vintageProductsArePresent) {
                m.addAttribute("vintageTag", true);
            }
            if (newCollectionProductsArePresent) {
                m.addAttribute("newCollectionTag", true);
            }
            if (productsWithOurChoiceArePresent) {
                m.addAttribute("ourChoiceTag", true);
            }
            if (saleProductsArePresent) {
                m.addAttribute("saleTag", true);
            }
        }

        return "catalog/just-in";
    }

    /**
     * @return раздел новых товаров ("С биркой")
     */
    @GetMapping("/new")
    public String newCategory(Model m, @ModelAttribute("request") CategoryRequest request) {

        FilterSpecification spec = new FilterSpecification()
                .state(ProductState.PUBLISHED)
                .itemState(ProductItem.State.INITIAL)
                .interestingAttributeValues(request.getFilter())
                .interestingBrands(request.getBrand())
                .interestingSizes(request.getSize())

                .isVintage(request.getVintage())
                .isOnSale(request.getOnSale())
                .hasOurChoice(request.getOurChoice())
                .isNewCollection(request.getNewCollection());

        SortAttribute sortAttribute
                = SortAttribute.of(request.getSort()).orElse(SortAttribute.PUBLISH_TIME_DESC);

        request.setSort(sortAttribute.getParameterName());

        Optional<ProductCondition> newConditionIfAny = productService.getNewCondition();
        if (newConditionIfAny.isPresent()) {
            ProductCondition newCondition = newConditionIfAny.get();
            spec.interestingConditions(Collections.singletonList(newCondition.getId()));
            m.addAttribute("conditionId", newCondition.getId());
        }

        Long interestingCategoryId = request.getCategory();
        if (interestingCategoryId != null) {
            Optional<CatalogCategory> optionalInterestingCategory = categoryService.findById(interestingCategoryId);

            if (optionalInterestingCategory.isPresent()) {
                CatalogCategory interestingCategory = optionalInterestingCategory.get();
                List<CatalogCategory> parentCategories = categoryService.getAllParentCategories(interestingCategoryId);
                List<CatalogCategory> subcategories = categoryService.getDirectChildrenCategories(interestingCategoryId, true);

                List<Long> productCategories = categoryService.getLeafCategoriesIds(interestingCategoryId);
                spec.categoriesIds(productCategories);

                m.addAttribute("thisCategory", interestingCategory);
                m.addAttribute("parentCategories", parentCategories);
                m.addAttribute("subcategories", subcategories);

                /* Атрибуты, по которым можно фильтровать выбранные товары */
                Map<Attribute, List<AttributeValue>> actualAttributeValues = productService.getActualAttributeValues(interestingCategoryId);
                m.addAttribute("thisCategoryAttributes", actualAttributeValues);

                /* Фильтрация товаров по размерам */
                List<CatalogSize> sizes = productService.getActualSizes(interestingCategoryId);
                if (!sizes.isEmpty() && !(sizes.get(0).getSizeType() == SizeType.NO_SIZE)) {
                    List<SizeType> actualSizeTypes = sizes.stream().map(CatalogSize::getSizeType).collect(Collectors.toList());
                    m.addAttribute("sizeTypes", actualSizeTypes);

                    /* Размеры в той сетке, которую выбрал пользователь.
                    Если пользователь сетку не выбрал, использовать ту, которая объявлена первой в перечислении SizeType.
                    Аналогично вести себя, когда пользователь выбрал несуществующую в системе сетку.
                    */
                    Optional<CatalogSize> sizesBySizeType = sizes.stream().filter(s -> s.getSizeType() == request.getSizeType()).findFirst();
                    if (sizesBySizeType.isPresent()) {
                        m.addAttribute("sizesBySizeType", sizesBySizeType.get().getValues());
                        m.addAttribute("requestedSizeType", request.getSizeType());
                    }
                    else {
                        m.addAttribute("sizesBySizeType", sizes.get(0).getValues());
                        m.addAttribute("requestedSizeType", sizes.get(0).getSizeType());
                    }
                }
            }
        }
        else {
            /* Если пользователь не выбрал категорию, показывать товары во всех категориях.
               Атрибуты фильтрации и размеры не показывать. Их можно выбрать только для конкретной категории.
            * */
            List<CatalogCategory> entireCatalog = categoryService.getEntireCatalog();
            m.addAttribute("subcategories", entireCatalog);
        }

        ViewQualification viewSettings = new ViewQualification()
                .interestingSizeType(request.getSizeType())
                .withSavings(true);

        ProductsList productsList = productService.getProductsList(spec, 1, sortAttribute, viewSettings);
        m.addAttribute("productsList", productsList);

        List<Brand> brands = productService.getActualBrands(spec.categoriesIds());
        if (!brands.isEmpty()) { m.addAttribute("brands", brands); }

        boolean vintageProductsArePresent = productService.getVintageProductsPresence(spec.categoriesIds());
        boolean newCollectionProductsArePresent = productService.getNewCollectionProductsPresence(spec.categoriesIds());
        boolean productsWithOurChoiceArePresent = productService.getProductsWithOurChoicePresence(spec.categoriesIds());
        boolean saleProductsArePresent = productService.getSaleProductsPresence(spec.categoriesIds());
        if (vintageProductsArePresent || newCollectionProductsArePresent || productsWithOurChoiceArePresent || saleProductsArePresent) {

            /* отображать блок отметок: передача в модель готового решения упрощает шаблон, в отличие от вычисления в самом шаблоне */
            m.addAttribute("tags", true);

            if (vintageProductsArePresent) {
                m.addAttribute("vintageTag", true);
            }
            if (newCollectionProductsArePresent) {
                m.addAttribute("newCollectionTag", true);
            }
            if (productsWithOurChoiceArePresent) {
                m.addAttribute("ourChoiceTag", true);
            }
            if (saleProductsArePresent) {
                m.addAttribute("saleTag", true);
            }
        }

        return "catalog/new";
    }

    @GetMapping("/sale")
    public String saleCategory(Model m, @ModelAttribute("request") CategoryRequest request) {

        FilterSpecification spec = new FilterSpecification()
                .state(ProductState.PUBLISHED)
                .itemState(ProductItem.State.INITIAL)
                .interestingAttributeValues(request.getFilter())
                .interestingBrands(request.getBrand())
                .interestingSizes(request.getSize())
                .interestingConditions(request.getProductCondition())

                .isVintage(request.getVintage())
                .isOnSale(true)
                .hasOurChoice(request.getOurChoice())
                .isNewCollection(request.getNewCollection());

        SortAttribute sortAttribute
                = SortAttribute.of(request.getSort()).orElse(SortAttribute.PUBLISH_TIME_DESC);

        request.setSort(sortAttribute.getParameterName());

        Long interestingCategoryId = request.getCategory();
        if (interestingCategoryId != null) {
            Optional<CatalogCategory> optionalInterestingCategory = categoryService.findById(interestingCategoryId);

            if (optionalInterestingCategory.isPresent()) {
                CatalogCategory interestingCategory = optionalInterestingCategory.get();
                List<CatalogCategory> parentCategories = categoryService.getAllParentCategories(interestingCategoryId);
                List<CatalogCategory> subcategories = categoryService.getDirectChildrenCategories(interestingCategoryId, true);

                List<Long> productCategories = categoryService.getLeafCategoriesIds(interestingCategoryId);
                spec.categoriesIds(productCategories);

                m.addAttribute("thisCategory", interestingCategory);
                m.addAttribute("parentCategories", parentCategories);
                m.addAttribute("subcategories", subcategories);

                /* Атрибуты, по которым можно фильтровать выбранные товары */
                Map<Attribute, List<AttributeValue>> actualAttributeValues = productService.getActualAttributeValues(interestingCategoryId);
                m.addAttribute("thisCategoryAttributes", actualAttributeValues);

                /* Фильтрация товаров по размерам */
                List<CatalogSize> sizes = productService.getActualSizes(interestingCategoryId);
                if (!sizes.isEmpty() && !(sizes.get(0).getSizeType() == SizeType.NO_SIZE)) {
                    List<SizeType> actualSizeTypes = sizes.stream().map(CatalogSize::getSizeType).collect(Collectors.toList());
                    m.addAttribute("sizeTypes", actualSizeTypes);

                    /* Размеры в той сетке, которую выбрал пользователь.
                    Если пользователь сетку не выбрал, использовать ту, которая объявлена первой в перечислении SizeType.
                    Аналогично вести себя, когда пользователь выбрал несуществующую в системе сетку.
                    */
                    Optional<CatalogSize> sizesBySizeType = sizes.stream().filter(s -> s.getSizeType() == request.getSizeType()).findFirst();
                    if (sizesBySizeType.isPresent()) {
                        m.addAttribute("sizesBySizeType", sizesBySizeType.get().getValues());
                        m.addAttribute("requestedSizeType", request.getSizeType());
                    }
                    else {
                        m.addAttribute("sizesBySizeType", sizes.get(0).getValues());
                        m.addAttribute("requestedSizeType", sizes.get(0).getSizeType());
                    }
                }
            }
        }
        else {
            /* Если пользователь не выбрал категорию, показывать товары во всех категориях.
               Атрибуты фильтрации и размеры не показывать. Их можно выбрать только для конкретной категории.
            * */
            List<CatalogCategory> entireCatalog = categoryService.getEntireCatalog();
            m.addAttribute("subcategories", entireCatalog);
        }

        ViewQualification viewSettings = new ViewQualification()
                .interestingSizeType(request.getSizeType())
                .withSavings(true);

        ProductsList productsList = productService.getProductsList(spec, 1, sortAttribute, viewSettings);
        m.addAttribute("productsList", productsList);

        List<Brand> brands = productService.getActualBrands(spec.categoriesIds());
        if (!brands.isEmpty()) { m.addAttribute("brands", brands); }

        boolean vintageProductsArePresent = productService.getVintageProductsPresence(spec.categoriesIds());
        boolean newCollectionProductsArePresent = productService.getNewCollectionProductsPresence(spec.categoriesIds());
        boolean productsWithOurChoiceArePresent = productService.getProductsWithOurChoicePresence(spec.categoriesIds());

        if (vintageProductsArePresent || newCollectionProductsArePresent || productsWithOurChoiceArePresent) {

            /* отображать блок отметок: передача в модель готового решения упрощает шаблон, в отличие от вычисления в самом шаблоне */
            m.addAttribute("tags", true);

            if (vintageProductsArePresent) {
                m.addAttribute("vintageTag", true);
            }
            if (newCollectionProductsArePresent) {
                m.addAttribute("newCollectionTag", true);
            }
            if (productsWithOurChoiceArePresent) {
                m.addAttribute("ourChoiceTag", true);
            }
        }

        return "catalog/sale";
    }

    @Getter @Setter
    private static class CategoryRequest {

        private String sort;
        private int page = 1;

        private List<Long> filter = Collections.emptyList();

        private List<Long> size = Collections.emptyList();

        private SizeType sizeType;

        private List<Long> brand = Collections.emptyList();

        private List<Long> productCondition = Collections.emptyList();

        private Long category;

        /* Фильтрация товаров по булевым атрибутам (тегам / отметкам) */
        private Boolean vintage;
        private Boolean ourChoice;
        private Boolean onSale;
        private Boolean newCollection;
    }

    @Getter @Setter
    @RequiredArgsConstructor
    private static class MatchInDictonariesResult {

        private String value;

        private Boolean isColour = false;
        private Boolean isMaterial = false;
        private Boolean isBrand = false;

        private Optional<Brand> optionalBrand = Optional.empty();
        private Optional<List<AttributeValue>> materialValues = Optional.empty();
        private Optional<AttributeValue> colourValue = Optional.empty();

        MatchInDictonariesResult(String a_substring) {
            this.value = a_substring;
        }

        private void Match(BrandRepository brandRepository, AttributeValueRepository attributeValueRepository){
            this.optionalBrand = brandRepository.findFirstByUrl(value);
            if(!this.optionalBrand.isPresent()){
                List<AttributeValue> nullableAttValues = attributeValueRepository.findAllByTransliterateValue(value);
                if (nullableAttValues.size() > 0){
                    this.materialValues = Optional.ofNullable(nullableAttValues.stream()
                            .filter(attributeValue -> !attributeValue.getAttribute().getId().equals(10L))
                            .collect(Collectors.toList()));
                    this.colourValue = nullableAttValues.stream().filter(a -> a.getAttribute().getId().equals(10L)).findFirst();
                    this.isColour = nullableAttValues.stream().anyMatch(attributeValue -> attributeValue.getAttribute().getId().equals(10L));
                    this.isMaterial = nullableAttValues.stream().anyMatch(attributeValue -> !attributeValue.getAttribute().getId().equals(10L));
                }
            } else {
                this.isBrand = true;
            }

        }

    }


}
