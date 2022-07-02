
const CartClient = function() {};

CartClient.prototype.itemsEndpoint = "/api/v1/cart/items";
CartClient.prototype.cartEndpoint = "/api/v1/cart";

CartClient.prototype.addItem = function (productId, sizeId, price) {
    return jQuery.ajax({
            url: this.itemsEndpoint,
            method: "POST",
            data: {productId: productId, sizeId: sizeId, price: price}
        }
    );
};

CartClient.prototype.removeItem = function (itemId) {
    return jQuery.ajax({
            url: [this.itemsEndpoint, itemId].join("/"),
            method: "DELETE"
        }
    );
};

CartClient.prototype.get = function () {
    return jQuery.ajax({
        url: this.cartEndpoint,
        method: "GET"
    });
};
