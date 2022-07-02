const ProductClient = function() {
    this.endpoint = "/api/v1/products";
};

ProductClient.prototype.get = function (requestParams) {
    return $.get(this.endpoint, requestParams);
};

ProductClient.prototype.getJustInProducts = function (requestParams) {
    requestParams["sort"] = "publishtime_desc";
    return $.get(this.endpoint, requestParams);
};

ProductClient.prototype.confirmProductItemSale = function (doConfirmSale, id, pickupRequisite) {

    const request = {
        method: "PUT",
        contentType: "application/json; charset=UTF-8",
        url: `${this.endpoint}/items/${id}?doConfirmSale=${doConfirmSale}`
    };

    if (doConfirmSale) {
        request.data = JSON.stringify(pickupRequisite);
    }

    return $.ajax(request);
};

ProductClient.prototype.makeAnOffer = function (productId, offeredPrice) {
    return jQuery.ajax({
            method: "POST",
            contentType: "application/json; charset=UTF-8",
            data: JSON.stringify({price: offeredPrice}),
            url: `${this.endpoint}/${productId}/offer`
        }
    );
};

ProductClient.prototype.confirmOffer = function (doOrDont, id) {

    const request = {
        method: "PUT",
        url: `${this.endpoint}/offers/${id}?confirm=${doOrDont}`
    };

    return $.ajax(request);
};

ProductClient.prototype.toggleLike = function (productId) {

    const request = {
        method: "PUT",
        url: `${this.endpoint}/${productId}/like`
    };

    return $.ajax(request);
};
