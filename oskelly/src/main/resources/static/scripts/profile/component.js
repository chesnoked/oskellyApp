let Profile = function () {

    const $followComponent = $("[data-follow]");

    const emptyList = "<h3 class='h5 text-serif'>Подписки</h3>" +
        "<p class='follow-list empty text-center text-secondary' style='margin-top: 2rem'>Список пуст</p>";

    const profileClient = new ProfileClient();

    $followComponent.on("click", "[data-submit-follow], [data-submit-unfollow]", handle);

    function handle(e) {
        $eventTarget = $(this);

        const userToFollow = $eventTarget.attr("data-submit-follow");

        if (userToFollow) {
            profileClient.follow(userToFollow)
                .done(function () {

                    $eventTarget.addClass("hide")
                        .siblings("[data-submit-unfollow]").removeClass("hide");

                    $("[data-followee-aware]").each(function () {
                        const $this = $(this);

                        if (!isNaN(parseInt($this.text()))) {
                            $this.text(+$this.text() + 1);
                        }
                    });

                })
                .fail(function(response) {
                    if (response.responseJSON.message) alert(response.responseJSON.message); });
        }

        const userToUnfollow = $eventTarget.attr("data-submit-unfollow");
        if (userToUnfollow) {
            profileClient.stopFollowing(userToUnfollow)
                .done(function () {

                    const listIsActuallyFolloweesList = $followComponent.attr("data-followees") !== undefined;
                    if (listIsActuallyFolloweesList) {
                        $eventTarget.closest("tr").remove();

                        const lastFolloweeHasBeenDeleted = $followComponent.find("tr").length === 0;
                        if (lastFolloweeHasBeenDeleted) {
                            $followComponent.closest("div[role=main]")
                                .empty()
                                .html(emptyList);
                        }
                    }
                    else {
                        $eventTarget.addClass("hide")
                            .siblings("[data-submit-follow]").removeClass("hide");
                    }

                    $("[data-followee-aware]").each(function() {
                        const $this = $(this);

                        if (!isNaN(parseInt($this.text()))) {
                            $this.text(+$this.text() - 1);
                        }
                    });
                })
                .fail(function (response) {
                    if (response.responseJSON.message) alert(response.responseJSON.message); });
        }
    }

    // --

    const $priceSubscriptionsComponent = $("[data-price-subscriptions]");
    const priceSubscriptionsEmptyList =
        "<p class='follow-list empty text-center text-secondary' style='margin-top: 2rem'>Список пуст</p>";

    $priceSubscriptionsComponent.on("click", "[data-remove-sub]", removeSub);

    function removeSub() {
        const $eventTarget = $(this);
        const productIdToUnsubFrom = $eventTarget.attr("data-product-id");
        if (!productIdToUnsubFrom) { return; }

        const $priceSubscriptionToRemove = $eventTarget.closest("[data-price-subscription]");
        $priceSubscriptionToRemove.fadeTo(0, 0.5).css("pointer-events", "none");


        profileClient.dropPriceTracking(productIdToUnsubFrom)
            .done(function() {
                $priceSubscriptionToRemove.remove();

                const noPriceSubscriptionsLeft = $priceSubscriptionsComponent.find("[data-price-subscription]").length === 0;
                if (noPriceSubscriptionsLeft) {
                    $priceSubscriptionsComponent.html(priceSubscriptionsEmptyList);
                }
            })
            .fail(function(response) {
                    if (response.responseJSON.message) alert(response.responseJSON.message);
                    $priceSubscriptionToRemove.fadeTo(0, 1).css("pointer-events", "");
            });
    }

    // --

    const $offersComponent = $("[data-offers]");
    const productClient = new ProductClient();

    const offerConfirmed = "<strong>Вы подтвердили предложение</strong>";
    const offerRejected = "<strong>Вы отказались от предложения</strong>";

    $offersComponent.on("click", "[data-confirm-offer], [data-reject-offer]", confirmOrRejectOffer);

    function confirmOrRejectOffer() {

        const $eventTarget = $(this);
        const $offer = $eventTarget.closest("[data-offer]");
        const offerId = $eventTarget.attr("data-offer-id");
        if (!offerId) {return; }

        $offer.fadeTo(0, 0.5).css("pointer-events", "none");

        const doConfirmOffer = $eventTarget.attr("data-confirm-offer") !== undefined;
        productClient.confirmOffer(doConfirmOffer, offerId)
            .done(function() {
                $offer.replaceWith(doConfirmOffer? offerConfirmed : offerRejected);
            })
            .fail(function(response) {
                if (response.responseJSON.message) alert(response.responseJSON.message);
                $offer.fadeTo(0, 1).css("pointer-events", "");
            });
    }

    //--

    const $salesComponent = $("[data-sales-type-selector]");

    $salesComponent.on("change", function() {
        location.href = location.pathname + "?type=" + $(this).val(); })

    // --
    const $productSubscriptionComponent = $("[data-product-subscriptions]");
    const productSubscriptionsEmptyList =
        "<div class='column small-12 text-center text-secondary'>Список пуст</div>"

    $productSubscriptionComponent.on("click", "[data-remove]", removeProductSub);

    const subscriptionClient = new SubscriptionClient();

    function removeProductSub() {

        const $eventTarget = $(this);
        const $singleSub = $eventTarget.closest("[data-element]");
        const subId = $eventTarget.attr("data-id");
        if (!subId) {return; }

        $singleSub.fadeTo(0, 0.5).css("pointer-events", "none");
        subscriptionClient.remove(subId)
            .done(function() {
                $singleSub.remove();

                const noSubsLeftInList = $productSubscriptionComponent.find("[data-element]").length === 0;
                if (noSubsLeftInList) {
                    $productSubscriptionComponent.html(productSubscriptionsEmptyList);
                }
            })
            .fail(function(response) {
                if (response.responseJSON.message) alert(response.responseJSON.message);
                $singleSub.fadeTo(0, 1).css("pointer-events", "");
            });
    }
};

let ProfileScroll = function () {
    let currentPage = 1;
    let totalPages = $("[data-total-pages]").attr("data-total-pages");
    let currentState = $("[data-current-state]").attr("data-current-state");
    let currentSeller = $("[data-seller-id]").attr("data-seller-id");
    let productsContainer = $("#products_container");
    let productClient = new ProductClient();
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

    function buildRequestParameters() {
        let cookedRequestParameters = {};
        cookedRequestParameters.seller = currentSeller;
        cookedRequestParameters.page = currentPage;
        cookedRequestParameters.state = currentState;
        return cookedRequestParameters;

    }


};

$(function () {
    let scroller = new ProfileScroll();
});

$(function () {
    new Profile();
});