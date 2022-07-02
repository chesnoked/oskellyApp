$(function () {

	initModerationRowHandler();

	$("#add-moderator-button").click(function () {
		var $reveal = $("#search-users-reveal");
		$reveal.foundation("open");
	});

	$("#find-user-input").keypress(function (event) {
		if (event.which == 13) {
			var email = $(this).val();
			if (email.trim().length == 0) {
				return;
			}

			var $searchUsersContainer = $("#search-users-container");
			$searchUsersContainer.empty();
			$.get({
				url: "/admin/users/moderators/find_users",
				data: {email: email}
			}).done(function (data) {
				if (data.trim().length == 0) {
					var notFound = $("<div class='row column'><h5>Пользователи не найдены</h5></div>");
					$searchUsersContainer.html(notFound);
				} else {
					$("#search-users-container").html(data);
					initAddModeratorRowClick();
				}
			})
		}
	});

});

function initModerationRowHandler() {
	$(".moderator-row").click(function () {
		var id = $(this).attr("data-id");
		var $reveal = $("#moderator-view-" + id);
		$reveal.foundation("open");

		//наши модальные окна пересоздаются после закрытия, поэтому надо переназначать обработчики
		initModalButtons($reveal);
		initSwitches($reveal);
	});
}

/**
 * Устанавливаем обработчик на строки в окне поиска пользователей
 */
function initAddModeratorRowClick() {
	$(".add-moderator-row").click(function () {
		var id = $(this).attr("data-id");
		$.get({
			url: "/admin/users/moderators/create_view",
			data: {userId: id}
		}).done(function (data) {
			/*
			 что мы делаем здесь:
			 1) помещаем разметку reveal-а в блок на странице
			 2) активируем плагин (reveal пропадает из блока, в который мы его поместили)
			 3) на новеньком reveal-е мы активируем обработчики кнопок и свичеров
			 4) при закрытии reveal-а мы удаляем его из разметки (если он останется в разметке,
			 то будут дублироваться id свичеров и они не будут переключаться)
			 */
			var $revealContainer = $("#add-moderator-reveal-container");
			$revealContainer.html(data);
			var $reveal = $revealContainer.find("#moderator-view-" + id);
			//без этого reveal не открывается
			$($reveal).foundation();
			$reveal.foundation("open");
			initSwitches($reveal);
			initModalButtons($reveal);
			$reveal.on("closed.zf.reveal", function () {
				$(this).remove();
			})
		}).fail(function (data) {
			showErrorFieldAlert(data);
		})
	});

}

/**
 * Эта функция нужна потому, что этот контрол (foundation switch) самостоятельно не меняет атрибут checked при нажатии
 * (меняется только визуально)
 * @param $reveal тот Reveal, который открывается
 */
function initSwitches($reveal) {
	$reveal.find(".switch-input").change(function () {
		var $switch = $(this);
		var checked = $switch.attr("checked");
		if (checked != undefined) {
			$switch.removeAttr("checked");
		} else {
			$switch.attr("checked", "checked");
		}
	})
}

function initModalButtons($reveal) {

	var $cancelButton = $reveal.find(".reveal-cancel");
	var $saveButton = $reveal.find(".reveal-save");

	$cancelButton.click(function () {
		var $reveal = $(this).parents(".reveal");
		$reveal.foundation("close");
	});

	$saveButton.click(function () {
		var $reveal = $(this).parents(".reveal");
		var data = getAuthorityRequest($reveal);
		$.ajax({
			url: "/admin/users/moderators/authorities",
			type: "PUT",
			contentType: "application/json",
			data: JSON.stringify(data)
		}).done(function (data) {
			location.reload();
		}).fail(function (data) {
			showErrorFieldAlert(data);
		})
	});
}

/**
 * Получить данные для запроса на обновление прав пользователя
 */
function getAuthorityRequest($reveal) {
	var data = {};
	data.userId = $reveal.attr("data-user-id");

	var $switches = $reveal.find(".switch-input");
	data.authorities = $.map($switches, function (item, i) {
		var authorityId = $(item).attr("data-authority-id");
		var checked = $(item).attr("checked");
		checked = (checked == 'checked');
		return {authorityId: authorityId, checked: checked};
	});

	return data;
}

function showErrorFieldAlert(data) {
	var errors = JSON.parse(data.responseText);
	var errorText = '';
	for (key in errors) {
		errorText += errors[key] + "\n";
	}
	alert(errorText);
}