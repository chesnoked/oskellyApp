let Catalog = function () {

    let knownFilters = [];

    let currentPage = 1;
    let totalPages = $("[data-total-pages]").attr("data-total-pages");
    let $productsAmountElement = $("span[data-total-amount]");

    let productClient = new ProductClient();

    let productsContainer = $("#products");

    let currentCategory = $("[data-category-id]").attr("data-category-id");

    let scrollToTopButton = $(".scroll-top");

    let scrolledElement = $(".siteWrapper");

    let locationAwareElements = $("[data-location-aware]");
    locationAwareElements.click(function(event) {
        const actualSelection = $.param(buildRequestParameters(false, false));
        const elementResource = $(this).attr("href");
        if (!(elementResource && actualSelection)) { return; }

        event.preventDefault();
        location.href = [elementResource, actualSelection].join("?");
    });

    let onFilterChange = function() {
        currentPage = 1;

        let requestParameters = buildRequestParameters(true);

        productsContainer.fadeTo(300, 0.2);
        productClient.get(requestParameters)
            .done(function (renderedProducts) {
                productsContainer.html(renderedProducts.content);
                totalPages = renderedProducts.totalPages;

                if (!isNaN(renderedProducts.totalAmount)) {
                    $productsAmountElement.text(renderedProducts.totalAmount);
                }


                // processAvailableFilters(renderedProducts.availableFilters);
                pushRequestParamsToHistory(buildRequestParameters(false));

            })
            .always(function() { productsContainer.fadeTo(300, 1); });
    };

    knownFilters = [
        new SizeFilter(onFilterChange),
        new BrandFilter(onFilterChange),
        new AttributeFilter(onFilterChange),
        new ProductSorting(onFilterChange),
        new ProductConditionFilter(onFilterChange),
        new TagFilter(onFilterChange)
    ];

    (function defineResetFilterButtonBehavior() {
        let resetFilterButtons = $("[data-reset-form]");
        resetFilterButtons.click(onFilterChange);
    })();


    (function initInfiniteScroll() {
        if (totalPages < 2) { return; }

        let scrollIsBlocked = false;
        $(window).scroll(function(){
            if (scrollIsBlocked) { return; }
            let scrollHeight = document.getElementById("scrollContainer").scrollHeight;
            let currentScrollPosition = this.scrollY;
            let screenHeight = document.documentElement.clientHeight;

            if (currentScrollPosition + screenHeight < scrollHeight * 2 / 3) { return; }

            if (currentPage >= totalPages) {
                return;
            }

            ++currentPage;
            let requestParameters = buildRequestParameters(true);

            scrollIsBlocked = true;
            productClient.get(requestParameters)
                .done(function (renderedProducts) {
                    productsContainer.append(renderedProducts.content);
                    totalPages = renderedProducts.totalPages;
                    scrollIsBlocked = false;
                });
            }
        );

    })();

    (function defineScrollToTopButtonBehavior() {
        scrollToTopButton.click(function() {
            scrolledElement.animate({scrollTop: 0}, 250, "linear");
        });
    })();

    function buildRequestParameters(pageAndCategoryIncluded) {
        let requestParameters = knownFilters.map(filter => filter.getActualRequestParameter())
            .filter(parameter => parameter);

        let cookedRequestParameters = {};
        requestParameters.forEach(function (requestParam) {
            Object.assign(cookedRequestParameters, requestParam);
        });

        if (pageAndCategoryIncluded) {
            cookedRequestParameters.page = currentPage;
            cookedRequestParameters.category = currentCategory;
        }

        return cookedRequestParameters;
    }

    function processAvailableFilters(availableFilters) {
        let attributeFilterIdsArr = availableFilters.filter;
        let attributeSizeIdsArr = availableFilters.size;
        let attributeBrandIdsArr = availableFilters.brand;
        let attributeProductConditionIdsArr = availableFilters.productCondition;

        $('form[id^="catalogue-widget-"]').each(function(index) {
            let formSubId=this.id.replace("catalogue-widget-", "");
            switch (formSubId) {
                case 'brand':
                    setFormCheckboxesAvailable(this, attributeBrandIdsArr);
                    break;
                case 'product-condition':
                    setFormCheckboxesAvailable(this, attributeProductConditionIdsArr);
                    break;
                default:
                    if(isNormalInteger(formSubId)){//форма аттрибутов
                        setFormCheckboxesAvailable(this, attributeFilterIdsArr);
                    }
                    break;
            }
        });
        $('form[id="widget-sizes-form"]').each(function(index) {
            let formSubId=this.id.replace("catalogue-widget-", "");
            setFormCheckboxesAvailable(this, attributeSizeIdsArr);
        });

        setCheckboxEnabled($('input[id="ourChoiceTag"]'), availableFilters.hasOurChoice);
        setCheckboxEnabled($('input[id="newCollectionTag"]'), availableFilters.newCollection);
        setCheckboxEnabled($('input[id="vintageTag"]'), availableFilters.vintage);
        setCheckboxEnabled($('input[id="saleTag"]'), availableFilters.onSale);
    }

    function setCheckboxEnabled(checkbox, enabled){
        if(!checkbox) return;
        //чекнутые чекбоксы не трогаем!
        if(checkbox.is(":checked")) return;
        checkbox.attr("disabled", !enabled);
        let className = enabled?"styledCheckbox":"styledCheckboxDisabled";
        checkbox.attr("class", className);
    }

    function setFormCheckboxesAvailable(form, availableValues){
        $(form).find("input[type=checkbox]").each(function(index) {
            let checkboxValue = parseInt($(this).val());
            let enabled = availableValues.indexOf(checkboxValue)>=0;
            setCheckboxEnabled($(this), enabled);
        });
    }

    //Проверка строки на integer'ность
    function isNormalInteger(str) {
        return /^\+?(0|[1-9]\d*)$/.test(str);
    }

    function pushRequestParamsToHistory(requestParams) {
        let newPageRequestParameters = $.param(requestParams);
        let effectivePageRequestParameters
            = newPageRequestParameters? "?" + newPageRequestParameters
            : window.location.pathname;

        window.history.pushState(null, null, effectivePageRequestParameters);
    }
};

$(function() {
    let productCatalog = new Catalog();
});
