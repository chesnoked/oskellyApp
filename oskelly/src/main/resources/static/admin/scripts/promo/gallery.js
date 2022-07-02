$(function () {

	$("#submitButton").click(createOrUpdateGallery);
});

function createOrUpdateGallery() {
	var file = $("input[name='image']").prop("files")[0];
	var orderIndex = $("#orderIndex").val();
	var url = $("#url").val();
	var id = $("input[name='id']").val();


	var data = new FormData();
	data.append("id", id);
	data.append("url", url);
	if (file != undefined) {
		data.append("image", file);
	}
	data.append("orderIndex", orderIndex);

	$.ajax({
		type: "POST",
		url: "/admin/promo/gallery/",
		data: data,
		processData: false,
		contentType: false,

	}).done(function (data) {
		location.href = '/admin/promo';
	}).fail(function (data) {
		var errors = JSON.parse(data.responseText);
		var errorText = '';
		for (key in errors) {
			errorText += errors[key] + "\n";
		}
		alert(errorText);
	});
}