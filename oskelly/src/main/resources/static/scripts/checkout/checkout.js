$(function() {

    const checkoutClient = new CheckoutClient();

    var $component = $(".js-checkout");
    var $cartSizeBadge = $(".headerPanel-cart .headerPanel-badge");

    $component.on("click", ".js-remove", remove);
    $component.on("click", ".js-discount-change", changeDiscount);
    $component.on("focus blur", ".js-discount-input", toggleFocus);
    $component.on("keypress", ".js-discount-input", submitDiscount);
    $component.on("click", ".js-complete", completeCheckout);

    initAddressSuggestions();

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

        var $input = $(event.target);
        var discountCode = $input.val().trim();

        $component.fadeTo(0, 0.5).css("pointer-events", "none");
        if (discountCode !== "") {
            checkoutClient.updateDiscount(discountCode, true)
                .done(function(data) {
                    updateState(data);
                })
                .fail(function(response) {
                    $(".checkout_discount_input:visible + .form-error")
                        .addClass("is-visible")
                        .html(response.responseJSON.message);
                })
                .always(function() {
                    $component.fadeTo(0, 1).css("pointer-events", "");
                });
        }
        else if ($(".js-discount:visible").attr("data-discount-is-set") === "true") {
            checkoutClient.updateDiscount(null, true)
                .done(function(data) {
                    updateState(data);
                })
                .fail(function(response) {
                    $(".checkout_discount_input:visible + .form-error")
                        .addClass("is-visible")
                        .html(response.responseJSON.message);
                })
                .always(function() {
                    $component.fadeTo(0, 1).css("pointer-events", "");
                });
        }


        event.preventDefault();
    }

    function changeDiscount(event) {
        $(event.target)
            .closest(".js-discount")
            .find(".js-discount-input")
            .prop("disabled", false)
            .trigger("focus");
    }

    function completeCheckout(event) {

        if (!userAgreementIsValid()) return;

        checkoutClient.complete($("#checkout-name").val(), $("#checkout-phone").val(),
            $("#checkout-address").val(), $("#checkout-city").val(), $("#checkout-nickname").val(),
            $("#checkout-email").val(), $("#checkout-zipcode").val(), $("#checkout-comment").val(),
            JSON.stringify($("#checkout-extensive-address").val())
            )
            .done(function(data) {
                $(event.target).after(data.holdSubmitForm);
                $("#send-hold-request").submit();
            })
            .fail(function(response) {
                alert("Ошибка: " + response.responseJSON.message);
            });
    }

    function updateState(newState) {
        $component.html(newState.renderedContent);

        $cartSizeBadge.html(newState.size);

        if (!newState.size) {
            $component.addClass("align-center");
            $cartSizeBadge.addClass("hide");
        }

        $component.find("[data-mfp-src='#login-popup']").magnificPopup();

        initAddressSuggestions();
    }

    function remove(event) {

        var $itemToRemove = $(event.target).closest(".js-item");
        var $itemId = $itemToRemove.attr("data-id");
        if (!$itemId) { return; }

        $component.fadeTo(0, 0.5).css("pointer-events", "none");
        checkoutClient.removeItem($itemId, "removeFromCheckout")
            .done(function(data) {
                updateState(data);
            })
            .always(function() {
                $component.fadeTo(0, 1).css("pointer-events", "");
            });
    }

    function userAgreementIsValid() {

        var $userAgreement = $(".checkout_user_agreement");

        if ($userAgreement.find("input").prop("checked")) { return true; }

        $userAgreement.find(".form-error").addClass("is-visible");

        $("html, body").animate({
            scrollTop: $(window).width() < 1280 ?
                /* на мобиле шапка сайта позиционируется абсолютно и перекрывает флажок */
                $userAgreement.offset().top - $(".headerPanel").outerHeight()
                : $userAgreement.offset().top
        }, 200);

        return false;
    }

    function initAddressSuggestions() {

        var $city = $("#checkout-city");
        var $address = $("#checkout-address");
        var $zipcode = $("#checkout-zipcode");

        $city.suggestions({
            token: "abb1e065856a2c6747958a4a21c2ce8fd27ceacc",
            type: "ADDRESS",
            bounds: "city-settlement",
            onSelect: function(suggest) {
                handleSuggestedAddress(suggest, $city, $address, $zipcode);
            },
            onSelectNothing: function () {
                $(this).val("");
                $zipcode.val("");
            },

            formatSelected: formatCity,
            hint: false
        });

        $address.suggestions({
            token: "abb1e065856a2c6747958a4a21c2ce8fd27ceacc",
            type: "ADDRESS",
            hint: false,

            constraints: $city,

            onSelect: function(suggest) {
                handleSuggestedAddress(suggest, $city, $address, $zipcode);
            },
            onSelectNothing: function () {
                $(this).val("")
            }
        });
    }

    function handleSuggestedAddress(suggest, $city, $address, $zipcode) {
        $zipcode.val(suggest.data.postal_code);
        $("#checkout-extensive-address").val(JSON.stringify(suggest));

        $component.fadeTo(0, 0.5).css("pointer-events", "none");
        checkoutClient.updateDelivery($city.val(), $address.val(), $zipcode.val(), suggest.data)
            .always(function() {
                $component.fadeTo(0, 1).css("pointer-events", "");
            });
    }

    function formatCity(suggestion) {
        const address = suggestion.data;
        if (address.city_with_type === address.region_with_type) {
            return address.settlement_with_type || address.city_with_type;
        } else {
            return join([
                address.city,
                join([address.settlement_type_full, address.settlement], " ")]);
        }
    }

    function join(strings, optionalSeparator) {
        const sep = optionalSeparator || ", ";
        return strings.filter(function (s) {
            return s
        }).join(sep);
    }

});