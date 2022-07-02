const OrderClient = function() {};

OrderClient.prototype.endpoint = "/api/v1/orders";

OrderClient.prototype.initHold = function (orderId, optionalAvailablePositions, optionalUnavailablePositions) {

    let request = {
        method: "POST",
        url: `${this.endpoint}/${orderId}/hold`
    };

    if (optionalAvailablePositions !== undefined && optionalUnavailablePositions !== undefined) {
        request.contentType = "application/json; charset=UTF-8";
        request.data = JSON.stringify({
            availablePositions: optionalAvailablePositions,
            unavailablePositions: optionalUnavailablePositions
        });
    }

    return jQuery.ajax(request);
};

OrderClient.prototype.updateDelivery = function (orderId, deliveryRequisite, doUpdateProfile) {
    return jQuery.ajax({
            method: "PUT",
            contentType: "application/json; charset=UTF-8",
            url: `${this.endpoint}/${orderId}/delivery?updateProfile=${doUpdateProfile}`,
            data: JSON.stringify(deliveryRequisite)
        }
    );
};

OrderClient.prototype.applyDiscount = function (orderId, code) {
    return jQuery.ajax({
            method: "POST",
            contentType: "application/json; charset=UTF-8",
            data: JSON.stringify({code: code}),
            url: `${this.endpoint}/${orderId}/discount`
        }
    );
};

OrderClient.prototype.removeDiscount = function (orderId) {
    return jQuery.ajax({
            method: "DELETE",
            url: `${this.endpoint}/${orderId}/discount`
        }
    );
};
