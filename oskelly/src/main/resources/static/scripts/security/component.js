$(function () {
    $("#registration-phone").mask("+7 (999) 999-99-99");

	$(".authentication-button").click(function () {
		let $clickedForm = $(this).parents(".authentication-form");
		console.log($clickedForm);
		authenticateUser($clickedForm);
	});

	$(".authentication-form").keypress(function (event) {
		if (event.which == 13) {
			authenticateUser($(this));
		}
	});

    $("#registration-form-submit-button").click(function () {
        registerUser();
    });

    $("#registration-form-second-step-button").click(function () {
        validateRegistrationEmail();
        return false;
    });

    $("#registration-form-step-1").keypress(function (event) {
        if (event.which == 13) {
            validateRegistrationEmail();
        }
    });

    $("#registration-form-step-2").keypress(function (event) {
        if (event.which == 13) {
            registerUser();
        }
    });

    new NewPasswordRequest();
    new NewPassword();
});

let NewPasswordRequest = function() {

    const $component = $("#password-reset");
    const client = new SecurityClient();

    let componentIsValid = false;
    $component.on("formvalid.zf.abide",     function() {  componentIsValid = true; });
    $component.on("forminvalid.zf.abide",   function() {  componentIsValid = false; });

    const $email = $component.find("input[type=email]");

    $component.submit(function(e) {
        if (!componentIsValid) { return; }

        client.resetPassword($email.val())
            .done(function() {
                $component.addClass("hide")
                    .prev().removeClass("hide");
            })
            .fail(function(response) {
                response.responseJSON.errors.forEach(function (el) {

                    const $invalidInput = $component
                        .find("[data-response-field='" + el.field + "']");

                    $invalidInput.addClass("is-invalid-input")
                        .next(".form-error")
                        .addClass("is-visible").text(el.message);
                });
            });

        e.preventDefault();
    });

    $("[data-mfp-src='#login-popup']")
        .on("mfpClose", bringComponentToInitialState);

    function bringComponentToInitialState() {
        $component.removeClass("hide")
            .prev().addClass("hide");

        $email.val("");
    }
};

let NewPassword = function () {

    const $component = $("form[data-reset-token]");
    if ($component.length > 0) { init($component); }

    let $newPasswordPopup = $("#new-password-popup");
    if ($newPasswordPopup.length === 0) { return; }

    $.magnificPopup.open({
        items: {
            src: "#new-password-popup",
            type: "inline"
        },
        closeOnBgClick: $newPasswordPopup.attr("data-token-is-valid") !== "true",
        enableEscapeKey: $newPasswordPopup.attr("data-token-is-valid") !== "true"
    });

    history.pushState(null, null, "/");

    function init($component) {

        const client = new SecurityClient();

        let componentIsValid = false;
        $component.on("formvalid.zf.abide", function () { componentIsValid = true; });
        $component.on("forminvalid.zf.abide", function () { componentIsValid = false; });

        $component.submit(function (e) {
            if (!componentIsValid) {
                return;
            }

            const newPassword = $component.find("input[data-response-field=password]").val();
            const newPasswordOnceMore = $component.find("input[data-response-field=passwordOnceMore]").val();
            const resetToken = $component.attr("data-reset-token");

            client.updatePassword(newPassword, newPasswordOnceMore, resetToken)

                .done(function () {
                    $.magnificPopup.open({items: {src: "#login-popup"}, type: "inline"});
                })

                .fail(function (response) {
                    const responseJSON = response.responseJSON;

                    if (responseJSON.errors) {
                        responseJSON.errors.forEach(function (el) {

                            const $invalidInput = $component
                                .find("[data-response-field='" + el.field + "']");

                            $invalidInput.addClass("is-invalid-input")
                                .next(".form-error")
                                .addClass("is-visible").text(el.message);
                        });
                    }

                    if (responseJSON.globalError || responseJSON.message) {
                       $component.find("[data-global-error]")
                           .text(responseJSON.globalError? responseJSON.globalError : responseJSON.message)
                           .removeClass("hide");
                    }
                });

            e.preventDefault();
        });
    }
};

function validateRegistrationEmail() {
    var $emailField = $("#registration-form-email");

    $.get({
        url: "/checkEmail",
        data: {email: $emailField.val()}
    }).done(function (data) {
        dataLayer.push({'event': 'registration-step-2'});
        removeError($emailField);
        $("#registration-form-step-1").foundation("toggle");
        $("#registration-form-step-2").foundation("toggle");
    }).fail(function (data) {
        var serverErrorObject = data.responseJSON;
        var emailError = serverErrorObject.email;
        showFieldErrorIfExists($emailField, emailError);
    });
}

/**
 *
 * @param $form Jquery object to this form
 */
function authenticateUser($form) {
	if($form === undefined || $form === null){
		return;
	}

	const $emailField = $form.find("input[name='email']");
	const $passwordField = $form.find("input[name='password']");

	if (validateFieldsNotEmpty([$emailField, $passwordField])) {
		return false;
	}

	$.ajax({
		type: "POST",
		url: "/login",
		data: {email: $emailField.val(), password: $passwordField.val()}
	}).done(function (data) {

		const from = $form.attr("data-from");
		if (from === undefined || from === null) {
            dataLayer.push({ 'event': 'vhod-submit' });
			location.reload();
		} else {
			//мы передаем предыдущую запрошенную ссылку в кодировке base64
			try {
				location = atob(from);
			} catch (e) {
				location = "/";
			}
		}

	}).fail(function (data) {
		const serverErrorObject = data.responseJSON;

		const emailError = serverErrorObject.email;
		const passwordError = serverErrorObject.password;
		if (emailError !== undefined) {
			showError($emailField, emailError);
		} else {
			removeError($emailField);
		}
		if (passwordError !== undefined) {
			showError($passwordField, passwordError);
		} else {
			removeError($passwordField);
		}
	});
}

function registerUser() {
    var $emailField = $("#registration-form-email");
    var $passwordField = $("#registration-password");
    var $confirmPasswordField = $("#registration-confirm-password");
    var $nicknameField = $("#registration-nickname");
    var $phone = $("#registration-phone");


    if (validateFieldsNotEmpty([$emailField, $passwordField, $confirmPasswordField, $nicknameField])) {
        return false;
    }

    $.ajax({
        type: "POST",
        url: "/register",
        data: {
            email: $emailField.val(),
            password: $passwordField.val(),
            confirmPassword: $confirmPasswordField.val(),
            nickname: $nicknameField.val(),
            phone: $phone.val()
        }
    }).done(function (data) {
        dataLayer.push({'event': 'registration-success'});
        fb_completeRegistration();
        location.reload();
    }).fail(function (data) {
        var serverErrorObject = data.responseJSON;

        var emailError = serverErrorObject.email;
        var passwordError = serverErrorObject.password;
        var confirmPasswordError = serverErrorObject.confirmPassword;
        var nicknameError = serverErrorObject.nickname;
        showFieldErrorIfExists($emailField, emailError);
        showFieldErrorIfExists($passwordField, passwordError);
        showFieldErrorIfExists($confirmPasswordField, confirmPasswordError);
        showFieldErrorIfExists($nicknameField, nicknameError);
    });
}

function showFieldErrorIfExists($field, error) {
    if (error != undefined) {
        showError($field, error);
    } else {
        removeError($field);
    }
}

function validateFieldsNotEmpty(fieldArray) {
	let empty = false;
	fieldArray.forEach(($field) => {
		const fieldValue = $field.val();
		if (fieldValue === undefined || fieldValue === '') {
			showError($field, "Не заполнено поле");
			empty = true;
		}
	});
	return empty;
}

function showError($field, text) {
    $field.attr("required", "required");
    $field.addClass("is-invalid-input");
    $field.attr("data-invalid", "data-invalid");

    var $message = $field.next();
    if ($message[0] == undefined) {
        var fieldId = $field.attr("id");
        var $newMessage = $("<span class='form-error'/>");
        $newMessage.attr("data-form-error-for", fieldId);
        $newMessage.addClass("is-visible");
        $newMessage.text(text);
        $field.after($newMessage);
    } else {
        $message.text(text);
    }
}

function removeError($field) {
    var $errorMessage = $field.next();
    if ($errorMessage != undefined) {
        $errorMessage.remove();
    }
    $field.removeClass("is-invalid-input");
    $field.removeAttr("data-invalid");
}

function validateLink(event, url) {
    event.preventDefault();
    $.ajax({
        type: "HEAD",
        url: url
    }).done(function (data) {
            window.location.href = url;
    }).fail(function (data) {
        if(data.status == 403){
            $.magnificPopup.open({
                items: {
                    src: '#login-popup',
                    type: 'inline'
                }
            });
        }});
}