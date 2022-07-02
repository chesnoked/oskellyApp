let ProfileProduct = function() {

    $(document).on("click", "[data-product-disposer]", function (e) {

        const $clickedElement = $(e.target);
        const $dropdownPane = $(e.currentTarget);

        if ($clickedElement.attr("data-product-disposer-submit") !== undefined) {

            const productToDispose = $dropdownPane.attr("data-product-disposer");
            if (!productToDispose) {
                return;
            }

            jQuery
                .ajax({
                    url: "/api/v1/products/" + productToDispose,
                    type: "DELETE"
                })
                .done(function () {
                    location.reload();
                })
                .fail(function () {
                    $dropdownPane.foundation("close");
                });

        }
        else if ($clickedElement.attr("data-product-disposer-cancel") !== undefined) {
            $dropdownPane.foundation("close");
        }
        else { /* unknown element clicked, do nothing */
        }
    });
};

$(function() { new ProfileProduct(); });
