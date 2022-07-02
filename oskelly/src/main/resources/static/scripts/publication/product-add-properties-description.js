$(function () {

    $("#confirm-description-next, #confirm-description-back").click(function () {
        var href = $(this).attr("href");
        var success = function (data) {
            location.href = href;
        };
        var fail = function (data) {
            showErrorFieldAlert(data);
        };
        updateProductDescription(success, fail);
        return false;
    });

});

function updateProductDescription(success, fail) {
    var data = getDescriptionData();
    if (data == null) {
        return false;
    } else {
        $.ajax({
            url: ["/publication/properties", getProductId(), "description"].join("/"),
            type: "PUT",
            data: data
        }).done(function (data) {
            success(data);
        }).fail(function (data) {
            fail(data);
        });
    }
}

function getDescriptionData() {
    var data = {};
    var description = $("textarea[name='description']").val();
    if (description == undefined || description.trim() == '') {
        alert("Не заполнено описание товара");
        return null;
    }

    data.description = description;
    data.vintage = $("input[name='vintage']:checked").val() != undefined;

    var model = $("#model").val();
    if (model != undefined && model.trim().length > 0) {
        data.model = model;
    }

    var origin= $("#origin").val();
    if (origin != undefined && origin.trim().length > 0) {
        data.origin = origin;
    }

    var purchasePrice= $("#purchase-price").val();
    if (purchasePrice != undefined && purchasePrice.trim().length > 0) {
        data.purchasePrice = purchasePrice;
    }

    var purchaseYear= $("#purchase-year").val();
    if (purchaseYear != undefined && purchaseYear.trim().length > 0) {
        data.purchaseYear = purchaseYear;
    }

    var serialNumber= $("#serial-number").val();
    if (serialNumber != undefined && serialNumber.trim().length > 0) {
        data.serialNumber = serialNumber;
    }
    return data;
}