const SubscriptionClient = function() {};

SubscriptionClient.prototype.endpoint = "/api/v1/subscriptions";

SubscriptionClient.prototype.create = function (
    interestingCategory,
    interestingBrand,
    interestingCondition,
    interestingSizeType,
    interestingSize,
    interestingAttributes)
{
    let request = {};
    if  (interestingCategory)  request.category   = interestingCategory;
    if  (interestingBrand)     request.brand      = interestingBrand;
    if  (interestingCondition) request.condition  = interestingCondition;

    if  (interestingSizeType && interestingSize) {
        request.sizeType  = interestingSizeType;
        request.size      = interestingSize;
    }

    if  (interestingAttributes.length) request.attributes = interestingAttributes;

    return jQuery.ajax({
        url: this.endpoint,
        method: "POST",
        contentType: "application/json; charset=UTF-8",
        data: JSON.stringify(request)
    });
};

SubscriptionClient.prototype.remove = function (id) {
    return jQuery.ajax({
        url: this.endpoint + "/" + id,
        method: "DELETE"
    });
};
