function addToWishList() {
    const productId = $("#main").attr("data-product-id");
    $.ajax({
        type: "PUT",
        url: "/wishlist/add",
        data: {productId: productId}
    }).done(function () {
        $("#wish-list-label").html("Добавлено в Wish List");
        document.getElementById("wish-list-action").removeEventListener("click", addToWishList);
        document.getElementById("wish-list-action").addEventListener("click", removeFromWishList);
        document.getElementById("wish-star-off").setAttribute("display", "none");
        document.getElementById("wish-star-on").setAttribute("display", " ");

        //GA
        ga_product_wishlistAddSuccess();

    }).fail(function (data) { });


}

function removeFromWishList() {

    const productId = $("#main").attr("data-product-id");
    $.ajax({
        type: "DELETE",
        url: "/wishlist/remove/" + productId,
        data: {productId: productId}
    }).done(function () {
        $("#wish-list-label").html("Добавить в Wish List");
        document.getElementById("wish-list-action").removeEventListener("click", removeFromWishList);
        document.getElementById("wish-list-action").addEventListener("click", addToWishList);
        document.getElementById("wish-star-off").setAttribute("display", " ");
        document.getElementById("wish-star-on").setAttribute("display", "none");
    }).fail(function (data) { });

}