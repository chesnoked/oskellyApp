let GiftCard = function() {

    const client = new DiscountClient();
    const $creationForm = $("#new-gift-card");
    const $deliveryTypes = $creationForm.find("[data-gift-card-delivery]");

    $deliveryTypes.on("click", ":radio", function () {
        const deliveryTypeId = this.id;
        if (!deliveryTypeId) { return; }

        $creationForm.find("[data-for=" + deliveryTypeId + "]").removeClass("invisible");
        $creationForm.find("[data-for]")
            .not("[data-for='" + deliveryTypeId + "']")
            .addClass("invisible")
            .find("input").val("");
    });



    $creationForm.submit(function (event) {
        client.createGiftCard(
            $("#gift-card-amount").val(),
            $("#gift-card-giving-name").val(),
            $("#gift-card-recipient-name").val(),
            $("#gift-card-recipient-email").val(),
            $("#gift-card-recipient-address").val()
        )
            .done(function (data) {
                client.payForGiftCard(data.id)
                    .done(function (submitForm) {
                        ga_giftcard_submit();
                        $creationForm.after(submitForm);
                        $("#send-payment-request").submit();
                    })
                    .fail(function (error) { alert(error.responseJSON.message); })
                }
            )
            .fail(function (response) {
                response.responseJSON.errors.forEach(function (el) {

                    const $invalidInput = $creationForm
                        .find("[data-response-field='" + el.field + "']");

                    $invalidInput.addClass("is-invalid-input")
                        .next(".form-error").addClass("is-visible").text(el.message);

                });
            });

        event.preventDefault();
    });
};

$( GiftCard );