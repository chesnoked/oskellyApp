$(function () {

    const address = new Address();

    $("#confirm-seller-next").click(function () {
        var success = function (data) {
            location.href = "/products/" + getProductId() + "?from=publication";
        };
        var fail = function (data) {
            showErrorFieldAlert(data);
        };
        updateSellerInfo(success, fail, true, address);
        return false;
    });

    $("#confirm-seller-back").click(function () {
        var href = $(this).attr("href");
        var success = function (data) {
            location.href = href;
        };
        var fail = function (data) {
            showErrorFieldAlert(data);
        };

        updateSellerInfo(success, fail, false, address);

        return false;
    });

});

function updateSellerInfo(success, fail, completePublication, address) {

    var data = getSellerInfo();
    if (data == null) {
        return false;
    }
    data.completePublication = completePublication;

    address.validate();
    const nullableAddress = address.getValidIfAny();
    if (nullableAddress === null) { return; }

    $.extend(data, nullableAddress);

    $.ajax({
        url: ["/publication/properties", getProductId(), "seller"].join("/"),
        type: "PUT",
        data: data
    }).done(function (data) {
        success(data);
    }).fail(function (data) {
        fail(data);
    });

}

function getSellerInfo() {
    var data = {};
    var phone = $("#phone-number").val();
    if (phone == undefined || phone.trim().length == 0) {
        alert("Не указан номер телефона продавца");
        return null;
    }
    data.phone = phone;

    var firstName = $("#seller-first-name").val();
    if (firstName == undefined || firstName.trim().length == 0) {
        alert("Не указано имя продавца");
        return null;
    }
    data.firstName = firstName;

    var lastName = $("#seller-last-name").val();
    if (lastName == undefined || lastName.trim().length == 0) {
        alert("Не указана фамилия продавца");
        return null;
    }
    data.lastName = lastName;

    return data;
}

const Address = function() {

    const $component = $("[data-address]");
    const $postCode = $component.find("[data-postcode]");
    const $city = $component.find("[data-city]");
    const $address = $component.find("[data-street]");
    const $componentForm = $component.closest("form");

    let formIsCurrentlyValid;
    let lastSuggest;

    $city.suggestions({
        token: "abb1e065856a2c6747958a4a21c2ce8fd27ceacc",
        type: "ADDRESS",
        bounds: "city-settlement",
        onSelect: handleSuggestedAddress,
        onSelectNothing: function() { $(this).val(""); $postCode.val(""); },
        formatSelected: formatCity
    });

    $address.suggestions({
        token: "abb1e065856a2c6747958a4a21c2ce8fd27ceacc",
        type: "ADDRESS",
        constraints: $city,
        bounds: "street-house",
        onSelect: handleSuggestedAddress,
        onSelectNothing: function() { $(this).val("") }
    });

    this.validate = function() {
        $component.closest("form").foundation("validateForm");
    };

    this.getValidIfAny = function() {
        if ($componentForm.find("input:required").get()
                .filter(function(el) {return !el.value}).length > 0) {
            return null;
        }

        return {
            postcode: $("[data-postcode]").val(),
            city: $("[data-city]").val(),
            address: $("[data-street]").val(),
            extensiveAddress: JSON.stringify(lastSuggest)
        };
    };

    $componentForm.on("forminvalid.zf.abide", function() { formIsCurrentlyValid = false; });
    $componentForm.on("formvalid.zf.abide",   function() { formIsCurrentlyValid = true; });

    function handleSuggestedAddress(suggest) {
        $postCode.val(suggest.data.postal_code);

        lastSuggest = suggest.data;
    }

    function formatCity(suggestion) {
        const address = suggestion.data;
        if (address.city_with_type === address.region_with_type) {
            return address.settlement_with_type || address.city_with_type;
        } else {
            return join([
                address.city,
                join([address.settlement_type_full, address.settlement]," ")]);
        }
    }

    function join(strings, optionalSeparator) {
        const sep = optionalSeparator || ", ";
        return strings.filter(function(s) {return s}).join(sep);
    }

};
