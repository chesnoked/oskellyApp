$(function () {

    $("#confirm-photos-next, #confirm-photos-back").click(function () {
        var href = $(this).attr("href");

        var success = function (data) {
            location.href = href;
        };
        checkPhotosUploaded(success);
        return false;
    });

    $(".productPhoto").change(function () {
        var $photoInput = $(this);
        var photo = $photoInput.prop("files")[0];

		if (photo == undefined) {
			return;
		}
        var photoOrder = $photoInput.attr("data-photo-order");

        var data = new FormData();
        data.append("image", photo);
        data.append("photoOrder", photoOrder);

        $.ajax({
            url: ["/publication/properties", getProductId(), "photo"].join("/"),
            type: "POST",
            data: data,
            processData: false,
            contentType: false
        }).done(function (data) {
            showThumbnailForPhotoByOrder($photoInput, photo);
        }).fail(function (data) {
            showErrorFieldAlert(data);
        })
    });
});

function checkPhotosUploaded(success) {
    var mainPhotoEmpty = $("input[name='photo-main']").next().is(":empty");
    var secondPhotoEmpty = $("input[name='photo-second']").next().is(":empty");

    if (mainPhotoEmpty) {
        alert("Не загружена главная фотография товара");
        return;
    }
    if (secondPhotoEmpty) {
        alert("Не загружена вторая фотография товара");
        return;
    }

    success();
}

function showThumbnailForPhotoByOrder($photoInput, photo) {

    var $photoContainer = $photoInput.next();
    $photoContainer.empty();

    loadImage(
        photo,
        function (img) {
            $photoContainer.append(img);
        },
        {
            maxWidth: 240,
            maxHeight: 320,
            orientation: true
        }
    );
}

