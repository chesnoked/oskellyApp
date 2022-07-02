$(function () {
	getCurrentLoadedUsers();
	initScroll();

	$(".filter-button").click(function () {
		$button = $(this);
		var checked = !$button.hasClass("secondary");
		if (checked) {
			$button.addClass("secondary");
		} else {
			$button.removeClass("secondary");
		}

		$button.attr("data-filter-enabled", checked);
		reloadPage();
	});

	$(".user-list-row").click(openUserPage);
});


var $table = $("#user-table");
var $allLoadedUsers = $("#all-loaded-users");
var $currentLoadedUsers = $("#current-loaded-users");

var currentPage = 1;
var allUsersLoaded = false;
var loadedUsers = 0;
var allUsers = parseInt($allLoadedUsers.text());


function getCurrentLoadedUsers() {
	loadedUsers = parseInt($(".users-tbody").attr("data-size"));
	if (loadedUsers >= allUsers) {
		allUsersLoaded = true;
	}
}

function getFilterValues() {
	var isPro = $("#filter-pro").attr("data-filter-enabled");
	var isSeller = $("#filter-seller").attr("data-filter-enabled");
	var isNew = $("#filter-new").attr("data-filter-enabled");
	return {isPro: isPro, isSeller: isSeller, isNew: isNew};
}

function reloadPage() {

	//при перезагрузке всего списка сбрасываем все значения
	currentPage = 0;
	$(".users-tbody").remove();
	loadedUsers = 0;
	allUsersLoaded = false;

	loadUsersPageWithFilters(currentPage + 1)
		.done(function (tbody) {
			$tbody = $(tbody);
			appendUserList($tbody);
		});
}


function initScroll() {
	let scrolledElement = $("main");
	let scrollIsLocked = false;
	scrolledElement.scroll(handleScroll);
	scrolledElement.on("touchmove", handleScroll);

	function handleScroll() {
		if (allUsersLoaded || scrollIsLocked) {
			return;
		}

		let scrollHeight = this.scrollHeight;
		let currentScrollPosition = this.scrollTop;
		let screenHeight = document.documentElement.clientHeight;

		if (currentScrollPosition + screenHeight < scrollHeight * 2 / 3) {
			return;
		}

		scrollIsLocked = true;
		loadUsersPageWithFilters(currentPage + 1)
			.done(function (data) {
				$tbody = $(data);
				appendUserList($tbody);
				scrollIsLocked = false;
			});
	}
}


function appendUserList($tbody) {
	var size = parseInt($tbody.attr("data-size"));
	if (size == 0) {
		allUsersLoaded = true;
	} else {
		currentPage += 1;
		loadedUsers += size;
		$currentLoadedUsers.text(loadedUsers);
		$table.append($tbody);
		$(".user-list-row").click(openUserPage);
	}
}

function loadUsersPageWithFilters(page) {
	var data = getFilterValues();
	data.page = page;
	return $.get({
		url: "/admin/users/list",
		data: data
	})
}

function openUserPage() {
	var id = $(this).attr("data-id");
	location.href = "/admin/users/" + id;
}