const PAGES_URL = "/admin/static/pages/";

function createStaticPage() {
	location.href = PAGES_URL;
}

function editStaticPage(id) {
	location.href = PAGES_URL + id;
}

function deleteStaticPage(id) {
	$.ajax({
		method: "DELETE",
		url: PAGES_URL + id
	}).always(function () {
		location.reload();
	})
}