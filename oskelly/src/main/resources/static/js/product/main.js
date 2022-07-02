$(function () {
    const mainProductData = $("#main[data-product-id]");

    const productId = mainProductData.attr("data-product-id");
    const productState = mainProductData.attr("data-product-state");
    const productClient = new ProductClient();

    /*
     * Показываем скидку для товара с одним размером
     */
    const onlyOneSizeAvailable = $("#onlyOneSizeAvailable");

    if (onlyOneSizeAvailable.length > 0) {
        const startPrice = onlyOneSizeAvailable.attr("data-start-price-by-size");
        const currentPrice = onlyOneSizeAvailable.attr("data-price-by-size");

        if (parseInt(startPrice) > parseInt(currentPrice)) {
            $("[data-updatable-start-price]").text(onlyOneSizeAvailable.attr("data-start-price-by-size-formatted") + " руб.");
            $("#current-product-price").addClass("productPage-productMeta-price-sale-price");
        }

    }

    const decisionButtons = $(".button[data-cart-product]");
    decisionButtons.each(handleDecisionButton);

    const $sizeSelector = $("select[data-size-selector]:visible");
    $sizeSelector.on("change", function() {

        const $selectedOption = $(":selected", this);
        const pickedSize = $selectedOption.attr("data-on-popup-size");

        if (pickedSize) {
            $("[data-updatable-size]").text("Размер: " + pickedSize);
        }
        //запрос в аналитику
        ga_product_sizeCheck();

        const priceForPickedSize = $selectedOption.attr("data-price-by-size-formatted");
        const dataPriceBySize = $selectedOption.attr("data-price-by-size");

        if (priceForPickedSize) {
            $("[data-updatable-price]").text(priceForPickedSize);
        }

        const dataStartPriceForPickedSize = $selectedOption.attr("data-start-price-by-size");
        const startPriceForPickedSize = $selectedOption.attr("data-start-price-by-size-formatted");

        if (dataStartPriceForPickedSize && parseInt(dataStartPriceForPickedSize) > parseInt(dataPriceBySize)) {
            $("[data-updatable-start-price]").text(startPriceForPickedSize + " руб.");
            $("#current-product-price").addClass("productPage-productMeta-price-sale-price");
        } else {
            $("[data-updatable-start-price]").text("");
        }

    });

    const $buttonedSizeSelector = $(".button-group[data-size-selector]:visible");
    $buttonedSizeSelector.on("click", "button", function(e) {

        const $selectedOption = $(e.target);
        $selectedOption.siblings(":not(.hollow)").addClass("hollow");
        const pickedSize = $selectedOption.attr("data-on-popup-size");

        if (pickedSize) {
            $("[data-updatable-size]").text("Размер: " + pickedSize);
        }

        const priceForPickedSize = $selectedOption.attr("data-price-by-size-formatted");
        const dataPriceBySize = $selectedOption.attr("data-price-by-size");

        if (priceForPickedSize) {
            $("[data-updatable-price]").text(priceForPickedSize);
        }

        const dataStartPriceForPickedSize = $selectedOption.attr("data-start-price-by-size");
        const startPriceForPickedSize = $selectedOption.attr("data-start-price-by-size-formatted");

        if (dataStartPriceForPickedSize && dataStartPriceForPickedSize > dataPriceBySize) {
            $("[data-updatable-start-price]").text(startPriceForPickedSize + " руб.");
        } else {
            $("[data-updatable-start-price]").text("");
        }

    });

    const shoppingCart = new CartClient();

    const addToShoppingCartButton = $("#add-to-cart");
    addToShoppingCartButton.click(function () {
        //По нажатию на кнопку вебвизор уходил в карзину.
        //Будем проверять аноним ли пользователь. Если аноним - выводим окно логина и заканчиваем функцию.
        const $button = $(this);
        const $onlyAvailableSizeIfAny = $("[data-only-size-available]:visible");
        const $onlyAvailableSizeId = $onlyAvailableSizeIfAny.attr("data-only-size-available");
        const $priceIfAny = $onlyAvailableSizeIfAny.attr("data-price-by-size");

        const $sizeSelector = $("[data-size-selector]:visible");

        let ableToAddProduct = $onlyAvailableSizeIfAny.length > 0
            || $sizeSelector.find(":selected").length > 0
            || $sizeSelector.find("button:not(.hollow)").length > 0;

        if (!ableToAddProduct) {
            alert("Выберите размер");
            return;
        }

        let sizeId;
        if ($onlyAvailableSizeIfAny.length > 0) {
            sizeId = $onlyAvailableSizeId;
        }
        else {
            if ($sizeSelector.find(":selected").length > 0) {
                sizeId = $sizeSelector.find(":selected").attr("data-size-id");
            }
            else {
                sizeId = $sizeSelector.find("button:not(.hollow)").attr("data-size-id");
            }
        }

        let price;
        if ($priceIfAny) {
            price = $priceIfAny;
        }
        else {
            if ($sizeSelector.find(":selected").length > 0) {
                price = $sizeSelector.find(":selected").attr("data-price-by-size");
            }
            else {
                price = $sizeSelector.find("button:not(.hollow)").attr("data-price-by-size");
            }
        }

        const contractFailed = !(sizeId && price && productId);
        if (contractFailed) { return; }

        shoppingCart.addItem(productId, sizeId, price)
            .done(function(data) {
                const $successfulPopup = $button.attr("data-on-success-mfp-src");
                if ($successfulPopup) {
                    $button.attr("data-mfp-src", $successfulPopup);
                    $button.magnificPopup({type: "inline"});
                    $button.magnificPopup("open");
                }

                $button.attr("disabled", "disabled");
                $button.html("В корзине");

                const $cartElement = $(".headerPanel-cart");
                if (typeof $cartElement.attr("href") === "undefined") {
                    $cartElement.attr("href", "/cart");
                }

                incrementCartSizeBadges();
            })

            .fail(function(data) {
                alert(data.responseJSON.message);
                location.reload();
            });

    });

    /*
     * Возможность добавить товар в корзину только после выбора размера
     */
    var productSizeSelect = $("#product-size-select");
    var productSizeButton = $(".product-size-button");

    if (productSizeSelect.length > 0) {
        addToShoppingCartButton.prop('disabled', true);
        addToShoppingCartButton.text('Выберите размер');
    }

    productSizeSelect.change(function () {
        if (productSizeSelect.val()) {
            addToShoppingCartButton.prop('disabled', false);
            addToShoppingCartButton.text('Добавить в корзину');
        }
    });

    productSizeButton.click(function () {
        addToShoppingCartButton.prop('disabled', false);
        addToShoppingCartButton.text('Добавить в корзину');
    });

    /*
     * Снять товар с продажи
     */
    var withdrawFromSale = $("#withdraw-from-sale");
    var pullOfFromSale = $("#pull-of-from-sale");

    $("#delete-product").click(function () {
        deleteProduct(productId);
    });

    $("#close-pull-of-from-sale").click(function () {
        pullOfFromSale.foundation("close");
    });

    /*
     * Изменение цены товара
     */

    var showCurrentProductPrice = $("#show-current-product-price");
    var editProductPrice = $("#edit-product-price");

    $("#change-product-price").click(function () {
        showCurrentProductPrice.css("display", "none");
        editProductPrice.css("display", "flex");
    });

    $("#set-product-price").click(function () {
        var price = $("#new-price").val();
        changeProductPrice(productId, price);
    });

    $("#cancel-new-price").click(function () {
        editProductPrice.css("display", "none");
        showCurrentProductPrice.css("display", "flex");
    });

    /* закрыть всплывающее окно после добавления товара в корзину */
    $("#addProductToCart-popup-close").click(() => $.magnificPopup.close());

    function handleDecisionButton() {
        const $button = $(this);

        let newProductState;
        if ($button.hasClass("success")) {
            newProductState = "PUBLISHED";
        }
        else if ($button.hasClass("alert")) {
            newProductState = "REJECTED";
        }
        else { /* элемент не содержит информации о состоянии товара */
        }

        if (newProductState) {
            const productId = $button.attr("data-product-id");

            $button.click(function () {
                updateProductState(productId, newProductState)
                    .always(function() { window.location.reload(); });
            });
        }
    }

    (function handleOffer() {

        const offerPopupSelector = "#sendOffer-popup";

        const userWantsToOpenPopupAndItExistsAtTheSameTime =
            window.location.hash === offerPopupSelector && $(offerPopupSelector).length > 0 ;

        if (userWantsToOpenPopupAndItExistsAtTheSameTime) {
            $.magnificPopup.open({
                items: {
                    src: offerPopupSelector, type: "inline"
                }
            })
        }

        const $offer = $("[data-offer]");

        const $offeredPrice = $offer.find("[name=offerPrice]");
        const $offerFailCause = $offeredPrice.next(".form-error");

        $offer.submit(function(event) {
            event.preventDefault();

            const contractFailed = !($offeredPrice.val() && productId);
            if (contractFailed) { return; }


            productClient.makeAnOffer(productId, $offeredPrice.val())
                .done(function() {
                    ga_product_offerSubmit();
                    location.reload(); })
                .fail(function(error) {
                    $offerFailCause.html(error.responseJSON.message).addClass("is-visible");
                    $offeredPrice.addClass("is-invalid-input");
                });
        });
    })();

    (function handleLike() {

        const $likeButton = $("#like");
        if ($likeButton.length === 0) { return; }

        $likeButton.on("click", function () {
            $likeButton.css("pointer-events", "none");

            productClient.toggleLike(productId)
                .done(function (response) {

                    if (response.actualLikesCount > 0) {
                        $("#likes-count").html(response.actualLikesCount);
                    }
                    else {
                        $("#likes-count").html("");
                    }

                    if (response.canBeLiked === true)  {
                        $likeButton.find("img").attr("src", "/images/icons/dntLiked.svg");
                    } else {
                        $likeButton.find("img").attr("src", "/images/icons/Liked.svg");
                    }
                })
                .fail(function () {})
                .always(function () { $likeButton.css("pointer-events", ""); });
        });

    })();

    (function handleAlert() {

        const subscriptionClient = new SubscriptionClient();
        const $component = $("[data-alert]");
        const $category = $component.find("[data-category]");
        const $brand = $component.find("[data-brand]");
        const $condition = $component.find("[data-condition]");
        const $sizeType = $component.find("[data-size-type]");
        const $size = $component.find("[data-size]");
        const $attributes = $component.find("[data-attr]");

        $component.find("[data-submit]").click(createAlert);

        $sizeType.change(showOnlyCorrespondingSizes);

        function showOnlyCorrespondingSizes() {

            $size.prop("selectedIndex", 0);

            console.log("size changed, new size is: " + $(this).val());
            const selectedSizeType = $(this).val();

            $size.find("option[data-size-type]").each(function() {

                const $this = $(this);

                if ($this.attr("data-size-type") === selectedSizeType) {
                    $this.removeClass("hide");
                }
                else {
                    $this.addClass("hide");
                }
            });
        }

        function createAlert() {
            const interestingCategory = $category.attr("data-category");
            const interestingBrand = $brand.val();
            const interestingCondition = $condition.val();
            const interestingSizeType = $sizeType.val();
            const interestingSize = $size.val();
            const interestingAttributes = $attributes.map(function() { return $(this).val(); })
                .get();

            const noneOfAttributesAreDefined = !(interestingCategory || interestingBrand || interestingCondition
                || interestingSizeType || interestingSize || interestingAttributes.length);

            if (noneOfAttributesAreDefined) { return; }

            $component.fadeTo(0, 0.5).css("pointer-events", "none");
            subscriptionClient.create(
               interestingCategory,
               interestingBrand,
               interestingCondition,
               interestingSizeType,
               interestingSize,
               interestingAttributes
            )
                .always(function() {
                    $component.fadeTo(0, 1).css("pointer-events", "none");
                    $component.css("pointer-events", "");
                    ga_product_alertSubmit();
                    $.magnificPopup.close();
                })
        }
    })();

    function updateProductState(productId, state) {
        return jQuery.ajax({
            url: ["/api/v1/products", productId].join("/"),
            type: "PUT",
            data: {state: state}
        });
    }

    function incrementCartSizeBadges() {

        $("[data-cart-size]").each(function() {
            const $cartSizeLabel = $(this);
            const currentCartSize = parseInt($cartSizeLabel.text());
            if (!isNaN(currentCartSize)) { $cartSizeLabel.text(currentCartSize + 1); }
        });
    }

    function deleteProduct(id) {
        jQuery.ajax({
            url: "/api/v1/products/" + id,
            type: "DELETE",
            success: function (result) {
                withdrawFromSale.text("Товар снят с продажи");
                withdrawFromSale.prop('disabled', true);
                pullOfFromSale.foundation("close");
            }
        })
    }

    function changeProductPrice(id, price) {
        price = price.replace(/\D+/g,"");

        /*
         * Если товар находится в состоянии "На модерации" можно установить любую новую цену
         * Если товар находится в любом другом состоянии можно снизить цену товара не менее 5% от текущей цены
         */
        if (productState !== "NEED_MODERATION") {
            var currentPrice = $("#current-product-price").text().replace(/\D+/g,"");

            var acceptPrice = currentPrice - currentPrice/100 * 5;

            if (price > acceptPrice) {

                alert("Вы можете снизить цену не менее 5% от текущей цены");
                return;
            }
        }

        price = (Math.round(price / 10) * 10);

        var changePrice = confirm("Новая цена для товара: " + price);

        if (changePrice) {
            jQuery.ajax({
                url: "/api/v1/products/" + id + "/discount",
                type: "PUT",
                data: {priceWithDiscount: price},
                success: function (result) {
                    $("#current-product-price").text(price + " руб.");
                    $("#current-product-price").addClass("productPage-productMeta-price-sale-price");
                }
            });
        }
        editProductPrice.css("display", "none");
        showCurrentProductPrice.css("display", "flex")
    }

});