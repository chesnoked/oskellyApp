const DiscountClient = function() {};

DiscountClient.prototype.endpoint = "/api/v1/discount";

DiscountClient.prototype.createGiftCard = function (amount, givingName, recipientName,
                                                    recipientEmail, recipientAddress) {

    let request = {
        amount: amount? amount : null,
        givingName: givingName? givingName : null,
        recipient: {
            name: recipientName? recipientName : null,
            email: recipientEmail ? recipientEmail : null,
            address: recipientAddress ? recipientAddress : null
        }
    };

    return jQuery.ajax({
            method: "POST",
            contentType: "application/json; charset=UTF-8",
            url: `${this.endpoint}/gift-cards`,
            data: JSON.stringify(request)
        }
    );
};

DiscountClient.prototype.payForGiftCard = function (id) {

    if (!id) { return; }

    return jQuery.ajax({
            method: "GET",
            url: `${this.endpoint}/gift-cards/${id}/payment-request`,
        }
    );
};
