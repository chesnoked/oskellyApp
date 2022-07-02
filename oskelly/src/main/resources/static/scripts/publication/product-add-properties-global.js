function getProductId() {
    return $("#productInfoBlock").attr("data-product-id");
}

function sendToModeration() {
    $.ajax({
        url: ["/publication/properties", getProductId(), "moderate"].join("/"),
        type: "POST"
    }).done(function (data) {
        location.href = "/products/" + getProductId();
    }).fail(function (data) {
        showErrorFieldAlert(data);
    });
}

function showErrorFieldAlert(data) {
    var errors = JSON.parse(data.responseText);
    var errorText = '';
    for (key in errors) {
        errorText += errors[key] + "\n";
    }
    alert(errorText);
}