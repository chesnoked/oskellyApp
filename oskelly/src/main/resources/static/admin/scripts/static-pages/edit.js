$(function () {
	CKEDITOR.replace('ckeditor', {
		//FIXME учесть что мы используем main.[HASH].css
		contentsCss: ["/styles/main.css"],
		customConfig: '/admin/scripts/ckeditor-config.js'
	});
});

function savePageAndStayOnThePage() {
	var success = function (data) {
		//В случае, если мы создали новую страницу, отдается Id созданной страницы
		if (data.id != undefined) {
			$("#page-id").val(data.id);
		}

		var $previewButton = $("#preview");
		$previewButton.removeAttr("disabled");

		var $pageField = $("#page-url");
		$pageField.attr("data-url", $pageField.val());
		alert("Страница успешно сохранена");
		window.location.reload();
	};

	var fail = function (data) {
		var errors = JSON.parse(data.responseText);
		var errorText = '';
		for (key in errors) {
			errorText += errors[key] + "\n";
		}
		alert(errorText);
	};
	savePage(success, fail);
}

function publish() {
	if(!confirm("Вы уверены?")) {
		return;
	}
	var id = $("#page-id").val();
	$.ajax({
		type: "PUT",
		url: "/admin/static/pages/publish/" + id
	}).done(function (data) {
		alert("Страница опубликована");
		window.location.reload();
	}).fail(function (data) {
		alert(data.responseText)
	});
}

function savePageAndReturnToPageList() {
	if(!confirm("Вы уверены?")) {
		return;
	}
	var success = function (data) {
		location.href = getStaticPageUrl();
	};
	var fail = function (data) {
		var errors = JSON.parse(data.responseText);
		var errorText = '';
		for (key in errors) {
			errorText += errors[key] + "\n";
		}
		alert(errorText);
	};
	savePage(success, fail);
}

/**
 *
 * @param success функция, срабатывающая при успешном сохранении страницы
 * @param fail функция, срабатывающая при ошибке сохранения
 */
function savePage(success, fail) {
	var data = getPageData();
	if (data == null) {
		return;
	}

	//Если data.id == '', создаем новую страницу, иначе обновляем старую
	$.ajax({
		type: "POST",
		url: $("#page-id").val().trim() === "" ? getStaticPageUrl() : getStaticPageUrl() + "/" + $("#page-id").val().trim(),
		data: data,
		processData: false,
		contentType: false
	}).done(function (data) {
		success(data);
	}).fail(function (data) {
		fail(data)
	});
}

function cancelPage() {
	var response = confirm("Вы уверены, что хотите уйти с этой страницы?\nДанные не будут сохранены!");
	if (response) {
		location.href = getStaticPageUrl();
	}
}

function deletePage() {
	var id = $("#page-id").val();
	if (id == '') {
		return;
	}
	var confirmResponse = confirm("Вы уверены, что хотите удалить страницу?");

	if (!confirmResponse) {
		return;
	}

	$.ajax({
		type: "DELETE",
		url: "/admin/static/pages/" + id
	}).done(function (data) {
		location.href = getStaticPageUrl();
	}).fail(function (data) {
		alert(data.responseText)
	});
}

function previewPage() {

	var $urlField = $("#page-url");
	var pageUrl = $urlField.attr("data-url");
	if (pageUrl.substr(0, 1) != '/') {
		pageUrl = "/" + pageUrl;
	}

	var $group = $("#page-type");
	var groupUrl = $group.attr("data-group-url");

	var openUrl = groupUrl + pageUrl;
	window.open(openUrl, '_blank');
	return false;

}

function getStaticPageUrl() {
	var groupUrl = $("#page-type").attr("data-group-url");
	return "/admin/static" + groupUrl;
}

/**
 * Получение объекта с данными страницы (вываливаем алерты в случае некоторых незаполненных полей)
 * @return {*}
 */
function getPageData() {
	var data = new FormData();

	data.append("id", $("#page-id").val().trim());

	var pageName = $("#page-name").val().trim();
	if (pageName == "") {
		alert("Не указано название страницы");
		return null;
	}
	data.append("name", pageName);

	var url = $("#page-url").val().trim();
	if (url == "") {
		alert("Не указан URL страницы");
		return null;
	}

	data.append("url", url);

	data.append("content", CKEDITOR.instances.ckeditor.getData());

	var metaDescription = $("#page-meta-description").val();
	if (metaDescription != '') {
		data.append("metaDescription", metaDescription);
	}

	var metaKeywords = $("#page-meta-keywords").val();
	if (metaKeywords != '') {
		data.append("metaKeywords", metaKeywords);
	}

	if($("#tags")[0] !==  undefined && $("#tags").val() !== null) {
		data.append("tagId", $("#tags").val())
	}

	if($("#mainImageInput")[0] !== undefined) {
		var file = $("#mainImageInput").prop("files")[0];
		if (file != undefined) {
			data.append("image", file);
		}
	}

	return data;
}

function translitUrl() {
	let pageName = $("#page-name").val();
	let translit = urlRusLat(pageName);
	$("#page-url").val(translit);
}

//Транслитерация кириллицы в URL
function urlRusLat(str) {
	str = str.toLowerCase(); // все в нижний регистр
	var cyr2latChars = [['а', 'a'], ['б', 'b'], ['в', 'v'], ['г', 'g'],
		['д', 'd'],  ['е', 'e'], ['ё', 'yo'], ['ж', 'zh'], ['з', 'z'],
		['и', 'i'], ['й', 'y'], ['к', 'k'], ['л', 'l'],
		['м', 'm'],  ['н', 'n'], ['о', 'o'], ['п', 'p'],  ['р', 'r'],
		['с', 's'], ['т', 't'], ['у', 'u'], ['ф', 'f'],
		['х', 'h'],  ['ц', 'c'], ['ч', 'ch'],['ш', 'sh'], ['щ', 'shch'],
		['ъ', ''],  ['ы', 'y'], ['ь', ''],  ['э', 'e'], ['ю', 'yu'], ['я', 'ya'],

		['А', 'A'], ['Б', 'B'],  ['В', 'V'], ['Г', 'G'],
		['Д', 'D'], ['Е', 'E'], ['Ё', 'YO'],  ['Ж', 'ZH'], ['З', 'Z'],
		['И', 'I'], ['Й', 'Y'],  ['К', 'K'], ['Л', 'L'],
		['М', 'M'], ['Н', 'N'], ['О', 'O'],  ['П', 'P'],  ['Р', 'R'],
		['С', 'S'], ['Т', 'T'],  ['У', 'U'], ['Ф', 'F'],
		['Х', 'H'], ['Ц', 'C'], ['Ч', 'CH'], ['Ш', 'SH'], ['Щ', 'SHCH'],
		['Ъ', ''],  ['Ы', 'Y'],
		['Ь', ''],
		['Э', 'E'],
		['Ю', 'YU'],
		['Я', 'YA'],

		['a', 'a'], ['b', 'b'], ['c', 'c'], ['d', 'd'], ['e', 'e'],
		['f', 'f'], ['g', 'g'], ['h', 'h'], ['i', 'i'], ['j', 'j'],
		['k', 'k'], ['l', 'l'], ['m', 'm'], ['n', 'n'], ['o', 'o'],
		['p', 'p'], ['q', 'q'], ['r', 'r'], ['s', 's'], ['t', 't'],
		['u', 'u'], ['v', 'v'], ['w', 'w'], ['x', 'x'], ['y', 'y'],
		['z', 'z'],

		['A', 'A'], ['B', 'B'], ['C', 'C'], ['D', 'D'],['E', 'E'],
		['F', 'F'],['G', 'G'],['H', 'H'],['I', 'I'],['J', 'J'],['K', 'K'],
		['L', 'L'], ['M', 'M'], ['N', 'N'], ['O', 'O'],['P', 'P'],
		['Q', 'Q'],['R', 'R'],['S', 'S'],['T', 'T'],['U', 'U'],['V', 'V'],
		['W', 'W'], ['X', 'X'], ['Y', 'Y'], ['Z', 'Z'],

		[' ', '_'],['0', '0'],['1', '1'],['2', '2'],['3', '3'],
		['4', '4'],['5', '5'],['6', '6'],['7', '7'],['8', '8'],['9', '9'],
		['-', '-']];

	var newStr = String();

	for (var i = 0; i < str.length; i++) {

		ch = str.charAt(i);
		var newCh = '';

		for (var j = 0; j < cyr2latChars.length; j++) {
			if (ch === cyr2latChars[j][0]) {
				newCh = cyr2latChars[j][1];

			}
		}
		// Если найдено совпадение, то добавляется соответствие, если нет - пустая строка
		newStr += newCh;

	}
	// Удаляем повторяющие знаки - Именно на них заменяются пробелы.
	// Так же удаляем символы перевода строки, но это наверное уже лишнее
	return newStr.replace(/[_]{2,}/gim, '_').replace(/\n/gim, '');
}

// БЛОГ Обновлени preview
function readImg(input) {

    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#mainImage').attr('src', e.target.result);
        }

        reader.readAsDataURL(input.files[0]);
    }
}

$("#mainImageInput").change(function(){
    readImg(this);
});