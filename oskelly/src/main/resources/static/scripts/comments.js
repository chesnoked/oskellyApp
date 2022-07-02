(function($, window, document) {

    window.onload = initComments();

    function initComments() {
        const productId = $("#main").attr("data-product-id");
        var getURI = location.origin +"/api/v1/products/" + productId + "/comments";
        $.getJSON(getURI,function(data) {
            document.getElementById("commentsWrapper").innerHTML = data["content"];
        });

    }

})(window.jQuery, window, document);

function postComment(event) {
    event.preventDefault();
    const productId = $("#main").attr("data-product-id");
    var postURI = location.origin +"/api/v1/products/" + productId + "/comment";
    var commentSrc = this.getAttribute('data-comment-src');
    var commentText = document.getElementById(commentSrc).value;
    var commentsHeading = document.getElementById('comments-counter');
    var commentsCounter = document.getElementById('comments-counter').getAttribute('datа-comments-count');
    if (!commentText.isEmpty){
        $.post(postURI,
            {
                text: commentText
            }
        )
            .done(function (data) {
                commentsCounter++;
                document.getElementById('comments-counter').setAttribute('datа-comments-count',commentsCounter.toString());
                commentsHeading.innerHTML = 'Комментарии (' + commentsCounter + ')';
                document.getElementById("commentText-bottom").value = "";
                $("#commentContainer").append(data["content"])
                    .removeClass("hide");
            })
            .fail(function(data){
                alert(data["content"]);
            });
    }


}
