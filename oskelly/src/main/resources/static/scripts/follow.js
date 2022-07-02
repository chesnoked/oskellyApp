var $followElements = $("#follow-action-1, #follow-action-2, #follow-action-3");

$followElements.each(function () {
    if ($(this).text() === 'Отписаться') {
        $(this).addClass('hollow');
    } else {
        $(this).removeClass('hollow');
    }
});

function follow(userId) {
    var $followElements = $("#follow-action-1, #follow-action-2, #follow-action-3");

    $.ajax({
        type: "POST",
        url: "/api/v1/profiles/self/followees",
        data: {userId: userId}
    }).done(function() {
        $followElements.addClass("hollow");
        $followElements.html("Отписаться");
        $followElements.off("click");
        $followElements.on("click", function() { unFollow(userId); });

    }).fail(function (data) {});
}

function unFollow(userId) {
    var $followElements = $("#follow-action-1, #follow-action-2, #follow-action-3");

    $.ajax({
        type: "DELETE",
        url: "/api/v1/profiles/self/followees/" + userId,
    }).done(function() {
        $followElements.removeClass("hollow");
        $followElements.html("Подписаться");
        $followElements.off("click");
        $followElements.on("click", function() { follow(userId); });

    }).fail(function (data) {});
}