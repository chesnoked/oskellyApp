var PROMO_GALLERY_URL = "/admin/promo/gallery/";
var PROMO_SELECTION_URL = "/admin/promo/selection/";

function openGalleryCreation() {
	window.location.href = PROMO_GALLERY_URL + "new";
}

function openGalleryUpdate(id) {
	window.location.href = PROMO_GALLERY_URL + id;
}

function deleteGalleryItem(id) {
	$.ajax({
		url: [PROMO_GALLERY_URL, id].join("/"),
		type: "DELETE"
	}).always(function () {
		location.reload();
	});
}

function openSelectionCreation() {
	location.href = PROMO_SELECTION_URL + "new";
}

function openSelectionUpdate(id) {
	location.href = PROMO_SELECTION_URL + id;
}

function deleteSelectionItem(id) {
	$.ajax({
		url: [PROMO_SELECTION_URL, id].join("/"),
		type: "DELETE"
	}).always(function () {
		location.reload();
	});
}