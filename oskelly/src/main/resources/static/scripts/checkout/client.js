const CheckoutClient = function() {};

CheckoutClient.prototype.endpoint = "/api/v1/cart";

CheckoutClient.prototype.removeItem = function (id, fromCheckout) {

    let request = {
        method: "DELETE",
        url: this.endpoint + "/items/" + id + "?fromCheckout=" + !!fromCheckout
    };

    return jQuery.ajax(request);
};

CheckoutClient.prototype.complete = function (name, phone, address, city, nickname, email, zipCode, comment, extensiveAddress) {
    return jQuery.ajax({
        url: this.endpoint + "/order",
        method: "POST",
        data: {name: name, phone: phone, address: address,
            city: city, nickname: nickname, email: email, zipCode: zipCode, comment: comment,
            extensiveAddress: extensiveAddress}
    });
};

CheckoutClient.prototype.changeSize = function (itemId, newSize) {

    return jQuery.ajax({
        method: "PATCH",
        url: this.endpoint + "/items/" + itemId,
        data: {size: newSize}
    });
};

CheckoutClient.prototype.updateDiscount = function (code, checkout) {

    return jQuery.ajax({
        method: "PATCH",
        url: this.endpoint + "/discount",
        data: {code: code, checkout: !!checkout}
    });
};

CheckoutClient.prototype.updateDelivery = function (city, address, zipcode, extensiveAddress) {
    return jQuery.ajax({
        method: "PATCH",
        url: this.endpoint + "/delivery",
        data: {
            city: city || null,
            address: address || null,
            zipCode: zipcode || null,
            extensiveAddress: (extensiveAddress? JSON.stringify(extensiveAddress) : null)
        }
    });
};
