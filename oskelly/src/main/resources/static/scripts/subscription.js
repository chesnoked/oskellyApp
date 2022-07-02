function addPriceSubscription() {
    const productId = $("#main").attr("data-product-id");
    const subscribeUrl = "/api/v1/products/"+productId+"/pricesubscription";
    $.ajax({
        type: "POST",
        url: subscribeUrl,
        data: {productId: productId}
    }).done(function () {
        $("#price-subscription-label").html("Вы следите за ценой");
        document.getElementById("price-subscription-action").removeEventListener("click", addPriceSubscription);
        document.getElementById("price-subscription-action").addEventListener("click", deletePriceSubscription);

        //GA
        ga_product_priceFollowAddSuccess();

    }).fail(function (data) { });


}

function deletePriceSubscription() {

    const productId = $("#main").attr("data-product-id");
    const subscribeUrl = "/api/v1/products/"+productId+"/pricesubscription";
    $.ajax({
        type: "DELETE",
        url: subscribeUrl,
        data: {productId: productId}
    }).done(function () {
        $("#price-subscription-label").html("Подписаться на снижение цены");
        document.getElementById("price-subscription-action").removeEventListener("click", deletePriceSubscription);
        document.getElementById("price-subscription-action").addEventListener("click", addPriceSubscription);

    }).fail(function (data) { });

}