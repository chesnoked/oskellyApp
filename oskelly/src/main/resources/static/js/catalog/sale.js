let Catalog = function () {

    let knownFilters = [];

    let currentPage = 1;
    let totalPages = $("[data-total-pages]").attr("data-total-pages");

    let productClient = new ProductClient();

    let productsContainer = $("#products");

    let currentCategory = $("[data-category-id]").attr("data-category-id");

    let scrollToTopButton = $(".scroll-top");

    let scrolledElement = $(".siteWrapper");

    let $productsAmountElement = $("span[data-total-amount]");

    let locationAwareElements = $("[data-location-aware]");
    locationAwareElements.click(function(event) {
        const actualSelection = $.param(buildRequestParameters(false, false));
        const elementResource = $(this).attr("href");
        if (!(elementResource && actualSelection)) { return; }

        event.preventDefault();
        location.href = [elementResource, actualSelection].join("&");
    });

    let onFilterChange = function() {
        currentPage = 1;

        let requestParameters = buildRequestParameters(true, true);

        productsContainer.fadeTo(300, 0.2);
        productClient.get(requestParameters)
            .done(function (renderedProducts) {
                productsContainer.html(renderedProducts.content);
                totalPages = renderedProducts.totalPages;

                if (!isNaN(renderedProducts.totalAmount)) {
                    $productsAmountElement.text(renderedProducts.totalAmount);
                }

                pushRequestParamsToHistory(buildRequestParameters(true, false));

            })
            .always(function() { productsContainer.fadeTo(300, 1); });
    };

    knownFilters = [
        new SizeFilter(onFilterChange),
        new BrandFilter(onFilterChange),
        new AttributeFilter(onFilterChange),
        new TagFilter(onFilterChange),
        new ProductConditionFilter(onFilterChange),
        new ProductSorting(onFilterChange)
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
            let requestParameters = buildRequestParameters(true, true);

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

    function buildRequestParameters(doIncludeCategory, doIncludePage) {
        let requestParameters = knownFilters.map(filter => filter.getActualRequestParameter())
            .filter(parameter => parameter);

        let cookedRequestParameters = {};
        requestParameters.forEach(function (requestParam) {
            Object.assign(cookedRequestParameters, requestParam);
        });

        if (doIncludeCategory && currentCategory) {
           cookedRequestParameters.category = currentCategory;
        }
        if (doIncludePage) {
            cookedRequestParameters.page = currentPage;
        }
        cookedRequestParameters.onSale = true;

        return cookedRequestParameters;
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
