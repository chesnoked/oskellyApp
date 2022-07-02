const Offer = function() {

    const $form = $("#offer-confirmation");

    /* При щелчке по кнопке пометить ее атрибутом clicked.
     * Чтобы при отправке формы можно было понять, какую именно кнопку нажали.
     */
    $form.find("[type=submit]").click(function() {
        $("[type=submit]", $(this).parents("form")).removeAttr("clicked");
        $(this).attr("clicked", true);
    });

    const offerId = $form.attr("data-offer-id");

    const productClient = new ProductClient();

    $form.submit(function(event) {

        if (!offerId) { return; }

        const offerDecision = $("[type=submit][clicked=true]", this).val();

        let doConfirmOffer;
        if (offerDecision === "confirm") {
            doConfirmOffer = true;
        }
        else if (offerDecision === "reject") {
            doConfirmOffer = false;
        }
        else { /* неизвестный выбор */ return; }

        productClient.confirmOffer(doConfirmOffer, offerId)
            .done(function() { location.reload(); })
            .fail(function(data) { alert(data.responseJSON.message); });

        event.preventDefault();
    });
};

$(function() { new Offer(); } );

