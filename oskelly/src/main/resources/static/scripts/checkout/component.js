$(function() {

    const checkoutClient = new CheckoutClient();

    var $component = $(".js-checkout");
    var $cartSizeBadge = $(".headerPanel-cart .headerPanel-badge");

    $component.on("click", ".js-remove", remove);
    $component.on("change", ".js-sizes", changeSize);

    $component.on("click", ".js-discount-change", changeDiscount);
    $component.on("focus blur", ".js-discount-input", toggleFocus);
    $component.on("keypress", ".js-discount-input", submitDiscount);

    function toggleFocus(event) {
        $(".checkout_discount_input:visible")
            .toggleClass("focus")
            .find(".input-group-label")
                .toggleClass("focus");

        if (event.type === "focusout"
            && $(".js-discount:visible").attr("data-discount-is-set") === "true") {
            $(".js-discount-input").prop("disabled",  true);
        }

        if ($(".js-discount:visible").attr("data-discount-is-set") === "true") {
            $(".js-input-prompt").toggleClass("hide");
            $(".js-input-state").toggleClass("hide");
        }
    }

    function submitDiscount(event) {
        if (event.keyCode !== 13) return;

        $component.fadeTo(0, .5).css("pointer-events", "none");

        $input = $(event.target);
        var discountCode = $input.val().trim();

        if (discountCode !== "") {
            checkoutClient.updateDiscount(discountCode)
                .done(function(data) {
                    updateState(data);
                })
                .fail(function(response) {
                    $(".checkout_discount_input:visible + .form-error")
                        .addClass("is-visible")
                        .html(response.responseJSON.message);
                });
        }
        else if ($(".js-discount:visible").attr("data-discount-is-set") === "true") {
            checkoutClient.updateDiscount(null)
                .done(function(data) {
                    updateState(data);
                })
                .fail(function(response) {
                    $(".checkout_discount_input:visible + .form-error")
                        .addClass("is-visible")
                        .html(response.responseJSON.message);
                });
        }

        $component.fadeTo(0, 1).css("pointer-events", "");

        event.preventDefault();
    }

    function changeDiscount(event) {
        $(event.target)
            .closest(".js-discount")
            .find(".js-discount-input")
            .prop("disabled", false)
            .trigger("focus");
    }

    function remove(event) {

        var $itemToRemove = $(event.target).closest(".js-item");
        var $itemId = $itemToRemove.attr("data-id");
        if (!$itemId) { return; }

        $component.fadeTo(0, 0.5).css("pointer-events", "none");
        checkoutClient.removeItem($itemId)
            .done(function(data) {
                updateState(data);
            })
            .always(function() {
                $component.fadeTo(0, 1).css("pointer-events", "");
            });
    }

    function updateState(newState) {
        $component.html(newState.renderedContent);

        $cartSizeBadge.html(newState.size);

        if (!newState.size) {
            $component.addClass("align-center");
            $cartSizeBadge.addClass("hide");
        }
    }

    function changeSize(event) {
        var $target = $(event.target);
        var itemId  = $target.closest(".js-item").attr("data-id");
        var newSize = $target.val();
        if (!itemId || !newSize) { return; }

        $component.fadeTo(0, 0.5).css("pointer-events", "none");
        checkoutClient.changeSize(itemId, newSize)

            .done(function(data) {
                updateState(data);
            })
            .fail(function(response) {
                alert(response.responseJSON.message);
            })
            .always(function(data) {
                $component.fadeTo(0, 1).css("pointer-events", "");
            });
    }
});