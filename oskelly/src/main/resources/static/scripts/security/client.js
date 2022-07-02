const SecurityClient = function() {};

SecurityClient.prototype.endpoint = "/api/v1/security";

SecurityClient.prototype.resetPassword = function (email) {
    return jQuery.ajax({
            url: this.endpoint + "/password/reset-token",
            method: "POST",
            contentType: "application/json; charset=UTF-8",
            data: JSON.stringify({email: email})
        }
    );
};

SecurityClient.prototype.updatePassword = function (newPassword, newPasswordOnceMore, resetToken) {
    return jQuery.ajax({
            url: this.endpoint + "/password",
            method: "PUT",
            contentType: "application/json; charset=UTF-8",
            data: JSON.stringify({
                password: newPassword,
                passwordOnceMore: newPasswordOnceMore,
                resetToken: resetToken
            })
        }
    );
};
