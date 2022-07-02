$(function () {
    const cartClient = new CartClient();

    const $cartContainer = $("#cart-container");
    $cartContainer.on("click", "[data-remove-cart-item]", removeCartItem);
    $cartContainer.on("click", "#cart-submit-order", submitOrder);

    function removeCartItem() {
        const $removeButton = $(this);
        const $removingItemId = $removeButton.attr("data-remove-cart-item");
        if (!$removingItemId) {
            return;
        }

        cartClient.removeItem($removingItemId).done(function (data) {
            $cartContainer.html(data.renderedContent);

            const $cartSizeBadge = $(".headerPanel-cart .headerPanel-badge");
            $cartSizeBadge.html(data.size);

        })
            .fail((jqXHR, textStatus) => alert("Ошибка " + textStatus));
    }

    function submitOrder() {
        const effectiveProductItems = $("[data-cart-item]")
            .map(function() {return this.getAttribute("data-cart-item")}).get();
        if (effectiveProductItems.length === 0) return;

        cartClient.createOrder(effectiveProductItems)
            .done(function(data, textStatus, response) {
            const orderLocation = response.getResponseHeader("Location");
            if (orderLocation) { location.href = orderLocation; }
        })
            .fail(function(jqXHR) {
                // TODO
                alert(jqXHR.status + " " + jqXHR.responseJSON.message);
                window.location.reload();
            });

    }

});