$(function () {

    let cartClient = new CartClient();

    let $cartSizeLabel = $("#cart-size");
    let $cartDropdownPane = $("#cart-dropdown");
    $cartDropdownPane.on(
        "show.zf.dropdown",
        function () { updateCartDropdown(cartClient, $cartDropdownPane, $cartSizeLabel);}
    );

    function updateCartDropdown(cartClient, $dropdownPane, $cartSizeLabel) {

        cartClient.get().done(function (data) {

            $dropdownPane.empty();

            data.items.forEach(function (item) {

                let cartItem = `<div class="row align-middle">
                <div class="shrink columns">
                    <img class="thumbnail" src=${item.imageUrl || "http://placehold.it/40x40"} 
                    width="40" height="40">
                </div>
                <div class="small-6 columns">
                    <strong>${item.brandName || ''}</strong>
                    <em>${item.productName}</em>
                </div>
                <div class="small-1 columns">
                    <p>${item.productPrice}</p>
                </div>
                <div class="columns text-right">
                    <a href="#" data-cart-item="${item.itemId}">&#128465;</a>
                </div></div><hr>`;

                const $cartItem = $(cartItem);
                $("[data-cart-item]", $cartItem).click(function() {

                    removeItself.call(this, $cartSizeLabel)

                });

                $dropdownPane.append($cartItem);

                $cartSizeLabel.text(data.items.length);
            });

            let overallPriceAndProceedForOrdering = `
            <div class="row align-justify">
                <div class="small-3 columns">
                    <p>Итого</p>
                </div>
                <div class="small-3 columns">
                    <strong>${data.totalPrice}</strong>
                </div>
            </div>
            <div class="row column">
                <a href="/cart" class="button">Оформить заказ</a>
            </div>
            `;

            if (data.items.length > 0) {
                $dropdownPane.append(overallPriceAndProceedForOrdering);
            }
            else {
                let emptyCartTrait = "<p>Нет товаров</p>";
                $dropdownPane.append(emptyCartTrait);
            }


        });
    }

    function removeItself($cartSizeLabel) {
        const $removeButton = $(this);

        const itemId = $removeButton.attr("data-cart-item");
        cartClient.removeItem(itemId).done(function() {

            let $thisItemRow = $removeButton.closest(".row");
            let $rowSeparator = $thisItemRow.next("hr");

            $thisItemRow.remove();
            $rowSeparator.remove();

            const currentCartSize = parseInt($cartSizeLabel.text());
            if (currentCartSize) { $cartSizeLabel.text(currentCartSize - 1); }
        });


    }

});
