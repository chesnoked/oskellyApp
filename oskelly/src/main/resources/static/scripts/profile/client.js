const ProfileClient = function() {};

ProfileClient.prototype.endpoint = "/api/v1/profiles/self";

ProfileClient.prototype.follow = function (userId) {
    return jQuery.ajax({
            url: this.endpoint + "/followees?userId=" + userId,
            method: "POST",
        }
    );
};

ProfileClient.prototype.stopFollowing = function (userId) {
    return jQuery.ajax({
            url: this.endpoint + "/followees/" + userId,
            method: "DELETE"
        }
    );
};

ProfileClient.prototype.dropPriceTracking = function (productId) {
    return jQuery.ajax({
            url: this.endpoint + "/price-tracking/" + productId,
            method: "DELETE"
        }
    );
};


