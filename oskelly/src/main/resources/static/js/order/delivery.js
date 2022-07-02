const Delivery = function() {

    const $form = $("[data-order-delivery]");

    const $requiredFormElements = $form.find("input[required]");
    const $doUpdateProfileDelivery = $("#order-do-update-profile-delivery");
    const currentOrderId = $form.attr("data-order-delivery");

    const client = new OrderClient();

    $form.submit(function(event) {
        event.preventDefault();

        if (!currentOrderId) { return; }

        const requiredElementsWithInvalidState
            = $requiredFormElements.filter(function () { return !this.value; }).length;

        if (requiredElementsWithInvalidState > 0) { return; }

        client.updateDelivery(currentOrderId, serializeRequiredElements(),
            $doUpdateProfileDelivery.length === 0 || $doUpdateProfileDelivery.prop("checked"))
            .always(() => location.reload());
    });

    function serializeRequiredElements() {
        let serializedRequiredElements = {};
        $requiredFormElements
            .filter(function() { return this.name !== null; })
            .each(function() { serializedRequiredElements[this.name] = this.value });

        return serializedRequiredElements;
    }


};

$(function() { new Delivery(); } );