let Catalog = function () {

    let knownFilters = [];

    let currentPage = 1;
    let totalPages = $("[data-total-pages]").attr("data-total-pages");

    let productClient = new ProductClient();

    let productsContainer = $("#products");

    let currentSeller = $("[data-profile-id]").attr("data-profile-id");

    let scrollToTopButton = $(".scroll-top");

    let scrolledElement = $(".siteWrapper");

    let onFilterChange = function() {
        currentPage = 1;

        let requestParameters = buildRequestParameters(true);

        productsContainer.fadeTo(300, 0.2);
        productClient.get(requestParameters)
            .done(function (renderedProducts) {
                productsContainer.html(renderedProducts.content);
                totalPages = renderedProducts.totalPages;

                pushRequestParamsToHistory(buildRequestParameters(false));

            })
            .always(function() { productsContainer.fadeTo(300, 1); });
    };

    knownFilters = [
        new ProductSorting(onFilterChange),
    ];

    var $component = $(".js-followers");
    $component.on("click", ".js-follow, .js-unfollow", toggleFollow);

    var profileClient = new ProfileClient();

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

        cookedRequestParameters.seller = currentSeller;

        if (pageAndCategoryIncluded) {
            cookedRequestParameters.page = currentPage;
        }

        return cookedRequestParameters;
    }

    function pushRequestParamsToHistory(requestParams) {
        let newPageRequestParameters = $.param(requestParams);
        let effectivePageRequestParameters
            = newPageRequestParameters? "?" + newPageRequestParameters
            : "";

        window.history.pushState(null, null, effectivePageRequestParameters);
    }

    function toggleFollow(event) {
        console.log("clicked");
        var $target = $(event.target);
        var follower =  $target.closest(".js-follower").attr("data-follower-id");
        if (!follower) return;

        var toggleFollow = $target.hasClass("js-follow")?
            profileClient.follow
            : profileClient.stopFollowing;

        $target.fadeTo(0, 0.5).css("pointer-events", "none");
        toggleFollow.call(profileClient, follower)
            .done(function() {

                $target.addClass("hide")
                    .siblings().removeClass("hide");

            })

            .always(function() {
                $target.fadeTo(0, 1).css("pointer-events", "");
            });

    }
};

$(function() {
    let productCatalog = new Catalog();
});
