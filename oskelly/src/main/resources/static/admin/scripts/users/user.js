$(function () {

	proStatusSwitchHandler();

});

function proStatusSwitchHandler() {
	$("label[for='pro-status-switch']").click(function () {

		$switchLabel = $(this);
		$switch = $("#pro-status-switch");

		//это текущий статус
		var checked = $switch.attr("checked");
		if (checked) {
			return false;
		} else {
			var result = confirm("Вы уверены, что хотите добавить продавцу статус PRO?");
			if (result) {
				$switch.attr("checked", "checked");
				var userId = $switch.attr("data-user-id");
				$switch.click();
				setProStatus(userId, true)
					.done(function () {
						location.reload();
					}).fail(function (data) {
					$switch.click();
					$switch.removeAttr("checked");
					showErrorFieldAlert(data);
				});
				return true;
			} else {
				return false;
			}
		}
	});


}


function setProStatus(userId, proStatus) {
	return $.ajax({
		url: "/admin/users/set_pro/" + userId,
		type: "PUT",
		data: {proStatus: proStatus}
	});
}
function showErrorFieldAlert(data) {
	var errors = JSON.parse(data.responseText);
	var errorText = '';
	for (key in errors) {
		errorText += errors[key] + "\n";
	}
	alert(errorText);
}
