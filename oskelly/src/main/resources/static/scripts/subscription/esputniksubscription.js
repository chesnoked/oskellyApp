$(function () {
    $("#subscription_button").click(function () {
        subscribeUser();
    });

    $("#subscription_form").keypress(function (event) {
        if (event.which == 13) {
            subscribeUser();
        }
    });

});

function subscribeUser() {
    var $emailField = $("#subscription-email");
    var $nameField = $("#subscription-name");

    if (validateFieldsNotEmpty([$emailField, $nameField])) {
        return false;
    }

    $.ajax({
        type: "POST",
        url: "/subscribe/email",
        data: {email: $emailField.val(), name: $nameField.val()}
    }).done(function (data) {
        var message = data;
        //TODO: nice messsage view
        alert(data);
        location.reload();
    }).fail(function (data) {
        var serverErrorObject = data.responseJSON;

        var emailError = serverErrorObject.email;
        var nameError = serverErrorObject.name;
        if (emailError != undefined) {
            showError($emailField, emailError);
        } else {
            removeError($emailField);
        }
        if (nameError != undefined) {
            showError($nameField, nameError);
        } else {
            removeError($nameField);
        }
    });
}