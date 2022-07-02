const SaleConfirmation = function() {

    const $form = $("#sale-confirmation");

    const $requiredFormElements = $form.find("input[required]");

    /* При щелчке по кнопке пометить ее атрибутом clicked.
     * Чтобы при отправке формы можно было понять, какую именно кнопку нажали.
     */
    $form.find("[type=submit]").click(function() {
        $("[type=submit]", $(this).parents("form")).removeAttr("clicked");
        $(this).attr("clicked", true);
    });

    const itemToSellId = $form.attr("data-item-id");

    const productClient = new ProductClient();

    $form.submit(function(event) {

        if (!itemToSellId) { return; }

        const saleDecision = $("[type=submit][clicked=true]", this).val();

        let doConfirmSale;
        if (saleDecision === "confirm") {
            doConfirmSale = true;
        }
        else if (saleDecision === "reject") {
            doConfirmSale = false;
        }
        else { /* неизвестный выбор */ return; }

        const requiredElementsWithInvalidState
            = $requiredFormElements.filter(function () { return !this.value; }).length;

        if (doConfirmSale && requiredElementsWithInvalidState > 0) { return; }

        const pickupRequisite = serializePickupRequisite();

        productClient.confirmProductItemSale(doConfirmSale, itemToSellId, pickupRequisite)
            .done(function() { location.reload(); })
            .fail(function(data) { alert(data.responseJSON.message); });

        event.preventDefault();
    });

    const $postCode = $form.find("[name=zipCode]");
    const $city = $form.find("[name=city]");
    const $address = $form.find("[name=address]");

    let lastSuggest;

    $city.suggestions({
        token: "abb1e065856a2c6747958a4a21c2ce8fd27ceacc",
        type: "ADDRESS",
        bounds: "city-settlement",
        onSelect: handleSuggestedAddress,
        onSelectNothing: function() { $(this).val(""); $postCode.val(""); },
        formatSelected: formatCity
    });

    $address.suggestions({
        token: "abb1e065856a2c6747958a4a21c2ce8fd27ceacc",
        type: "ADDRESS",
        constraints: $city,
        bounds: "street-house",
        onSelect: handleSuggestedAddress,
        onSelectNothing: function() { $(this).val("") }
    });

    function serializePickupRequisite() {
        let serializedRequiredElements = {};
        $requiredFormElements
            .filter(function() { return this.name !== null; })
            .each(function() { serializedRequiredElements[this.name] = this.value });

        serializedRequiredElements.extensiveAddress = JSON.stringify(lastSuggest);

        return serializedRequiredElements;
    }

    function handleSuggestedAddress(suggest) {
        $postCode.val(suggest.data.postal_code);

        lastSuggest = suggest.data;
    }

    function formatCity(suggestion) {
        const address = suggestion.data;
        if (address.city_with_type === address.region_with_type) {
            return address.settlement_with_type || address.city_with_type;
        } else {
            return join([
                address.city,
                join([address.settlement_type_full, address.settlement]," ")]);
        }
    }

    function join(strings, optionalSeparator) {
        const sep = optionalSeparator || ", ";
        return strings.filter(function(s) {return s}).join(sep);
    }


};

$(function() { new SaleConfirmation(); } );

