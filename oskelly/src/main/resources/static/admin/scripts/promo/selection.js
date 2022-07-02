$(function () {

	$("#submitButton").click(createOrUpdateSelection);
});

function createOrUpdateSelection() {
	var file = $("input[name='image']").prop("files")[0];

	var data = new FormData();
	data.append("id", $("input[name='id']").val());
	data.append("url", $("#url").val());
	if (file != undefined) {
		data.append("image", file);
	}
	data.append("promoGroup", $("#promoGroup").val());
	data.append("alt", $("#alt").val());
	data.append("orderIndex", $("#orderIndex").val());
	data.append("firstLine", $("#firstLine").val());
	data.append("secondLine", $("#secondLine").val());
	data.append("thirdLine", $("#thirdLine").val());

	$.ajax({
		type: "POST",
		url: "/admin/promo/selection/",
		data: data,
		processData: false,
		contentType: false
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