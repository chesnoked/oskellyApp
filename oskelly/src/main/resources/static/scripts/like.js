function likeThis() {
    const productId = $("#main").attr("data-product-id");
    const likeUrl = '/api/v1/products/'+ productId + '/like';
    var likesCount = parseInt($("#like-controll").attr("data-likes-count"));

    $.ajax({
        type: "POST",
        url: likeUrl,
    }).done(function (data) {
        if(data == true) {
            document.getElementById("like-controll").classList.add("active");
            document.getElementById("like-controll").removeEventListener("click", likeThis);
            document.getElementById("like-controll").addEventListener("click", dislikeThis);
            likesCount = likesCount + 1;
            $("#likes-count").html(likesCount);
            $("#like-controll").attr("data-likes-count", likesCount);
        }
    }).fail(function (data) { });
}

function dislikeThis() {
    const productId = $("#main").attr("data-product-id");
    const dislikeUrl = '/api/v1/products/'+ productId + '/dislike';
    var likesCount = parseInt($("#like-controll").attr("data-likes-count"));

    $.ajax({
        type: "POST",
        url: dislikeUrl,
    }).done(function (data) {
        if(data == true) {
            document.getElementById("like-controll").classList.remove("active");
            document.getElementById("like-controll").removeEventListener("click", dislikeThis);
            document.getElementById("like-controll").addEventListener("click", likeThis);
            likesCount = likesCount - 1;
            $("#likes-count").html(likesCount);
            $("#like-controll").attr("data-likes-count", likesCount);
        }

    }).fail(function (data) { });
}