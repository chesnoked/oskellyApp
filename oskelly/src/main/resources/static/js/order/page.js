$(function() {

    const orderClient = new OrderClient();

    const $order = $("[data-order]");
    const $submitPayment = $order.find("[data-submit-payment]");
    const orderId = $order.attr("data-order");

    $submitPayment.click(function() {

        const availableOrderPositions = $order.find("[data-available-order-position]")
            .map(function() {
                return this.getAttribute("data-available-order-position")})
            .get();

        const unavailableOrderPositions = $order.find("[data-unavailable-order-position]")
            .map(function() {
                return this.getAttribute("data-unavailable-order-position")})
            .get();

        orderClient.initHold(orderId, availableOrderPositions, unavailableOrderPositions)
            .done(function (data) {
                $submitPayment.after(data.holdSubmitForm);
                $("#send-hold-request").submit();
            })
            // TODO
            .fail(function(jqXHR) {
                alert(jqXHR.responseJSON.message);
                location.reload();
            });
    });

    (function discountBehavior() {

        const $discountComponent = $("[data-discount]");

        const $discountApplier = $discountComponent.find("[data-discount-apply]");
        const $discountRemover = $discountComponent.find("[data-discount-remove]");

        const $errorElement = $discountComponent.find("[data-discount-error]");
        const $codeElement = $discountComponent.find("input[name=code]");

        $discountApplier.on("click", function(event) {

            event.preventDefault();

            const orderId = this.getAttribute("data-order-id");
            if (!orderId) { return; }

            if (!$codeElement.val()) { return; }

            orderClient.applyDiscount(orderId, $codeElement.val())
                .done(function() { location.reload(); })
                .fail(function(error) {
                    $errorElement.html(error.responseJSON.message).addClass("is-visible");
                    $codeElement.addClass("is-invalid-input");
                });

        });

        $discountRemover.on("click", function(event) {

            event.preventDefault();

            const orderId = this.getAttribute("data-order-id");
             if (!orderId) { return; }

             orderClient.removeDiscount(orderId)
                 .done(function() { location.reload(); })
                 .fail(function(error) {
                     $errorElement.html(error.responseJSON.message).addClass("is-visible");
                 });

        });
    })();




    (function showUnavailableItemsIfTheCase() {

        if ($("#unavailableItems-popup").length === 0) { return; }

        $.magnificPopup.open({
            items: {
                src: '#unavailableItems-popup',
                type: 'inline'
            }
        });

    })();

});