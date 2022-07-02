$(function () {

	setCategoryButtonHandler();

	$("#confirm-info-next").click(function () {
		var href = $(this).attr("href");
		var success = function (data) {
			location.href = href;
		};
		updateProductInfo(success, showErrorFieldAlert);
		return false;
	});

	$("#size-type").change(function () {
		var sizeType = $("#size-type").val();
		var categoryId;
		var subcategory = $("input[name='subcategory']:checked").val();
		var category = $("input[name='category']:checked").val();
		var secondCategoryId = $("#childCategory").attr("data-child-categoryId");
		if (subcategory != undefined) {
			categoryId = subcategory;
		} else if (category != undefined) {
			categoryId = category;
		} else {
			categoryId = secondCategoryId
		}

		loadSizes(sizeType, categoryId);
	});

});


function loadPhotoSamples(categoryId) {
	const photoSamplesContainers = $("[data-photo-sample]");
    $.get({
        url: "/publication/properties/category-photo-samples?categoryId=" + categoryId
    }).done(function (data) {

    	data.samples.forEach(function(sample, index) {

			const correspondingContainer = photoSamplesContainers.eq(index);
			if (correspondingContainer) { correspondingContainer.html(sample); }
		});
    });
}

function setCategoryButtonHandler() {
	$("input[name='category']").click(function () {
		//id выбранной категории
		var category = $(this).val();
		//все группы атрибутов, кроме категории
		var loadedGroups = $(".attributeBlock").filter(function (i) {
			var name = $(this).attr("data-name");
			return name != "category";
		});

		//проверяем, была ли ранее выбрана категория
		if (loadedGroups.length > 0) {
			//если да, то запрашиваем подтверждение (атрибуты сбросятся)
			var result = confirm("Вы уверены что хотите изменить категорию товара?\n" +
				"Внесённые значения атрибутов не сохранятся!");
			if (!result) {
				return false;
			}
			clearSumoSelect($("#size-type-block"));
			clearSumoSelect($("#size-block"));
			setSizeTypesVisible(false);
			setSizeVisible(false);
			loadedGroups.remove();
		}

		var hasChildren = $(this).attr("data-has-children");
		hasChildren = hasChildren == "true";

		/*
		 Если это не конечная категория, грузим субкатегорию.
		 Иначе грузим атрибуты и размерные сетки.
		 */
		if (hasChildren) {
			setSizeTypesVisible(false);
			setSizeVisible(false);
			loadSubcategory(category);
		} else {
			loadAttributes(category);
			loadSizeTypes(category);
			loadPhotoSamples(category);
		}
	});
}

/**
 * Вызывая этот метод, мы однозначно уверены, что получим в ответ набор субкатегорий для категории
 * @param categoryId
 */
function loadSubcategory(categoryId) {
	$.get({
		url: "/publication/properties/attributes?categoryId=" + categoryId
	}).done(function (data) {
		$("#tab-form-information").find(".attributeBlock").last().after(data);
		setSubcategoryButtonHandler();
	});
}

/**
 * Вызывая этот метод, мы однозначно уверены, что была выбрана конечная категория и мы получим набор атрибутов
 * @param categoryId
 */
function loadAttributes(categoryId) {
	$.get({
		url: "/publication/properties/attributes?categoryId=" + categoryId
	}).done(function (data) {
		$("#tab-form-information").find(".attributeBlock").last().after(data);
	});
}

function loadSizeTypes(categoryId) {
	$.get({
		url: "/publication/properties/sizetypes?categoryId=" + categoryId
	}).done(function (data) {

		const categoryHasNoSizesAtAll = data.length === 1 && data[0].id === "NO_SIZE";

        resetSumoSelect($("#size-type-block"), data);
		if (categoryHasNoSizesAtAll) {
            handleNoSize(categoryId);
        }
        else {
            setSizeTypesVisible(true);
        }
	});

	function handleNoSize(categoryId) {
        $.get({
            url: "/publication/properties/sizes",
            data: {sizeType: "NO_SIZE", categoryId: categoryId}
        }).done(function (data) {
            setSizeVisible(false);
            setSizeTypesVisible(false);
            resetSumoSelect($("#size-block"), data);
        });
	}
}
function loadSizes(sizeType, categoryId) {
	$.get({
		url: "/publication/properties/sizes",
		data: {sizeType: sizeType, categoryId: categoryId}
	}).done(function (data) {
		if (sizeType !== "NO_SIZE") { setSizeVisible(true); }
		resetSumoSelect($("#size-block"), data);
	}).fail(function (data) {
		showErrorFieldAlert(data);
	});
}


function setSubcategoryButtonHandler() {
	$("input[name='subcategory']").click(function () {
		//id выбранной подкатегории
		var subcategory = $(this).val();

		var hasChildren = $(this).attr("data-has-children");
		hasChildren = hasChildren == "true";

		//все группы атрибутов, кроме категории и подкатегории
		var loadedGroups = $(".attributeBlock").filter(function (i) {
			var name = $(this).attr("data-name");
			return name != "category" && name != "subcategory";
		});

		//проверяем, была ли ранее выбрана категория
		if (loadedGroups.length > 0) {
			//если да, то запрашиваем подтверждение (атрибуты сбросятся)
			var result = confirm("Вы уверены что хотите изменить категорию товара?\n" +
				"Внесённые значения атрибутов не сохранятся!");
			if (!result) {
				return false;
			}
			loadedGroups.remove();
			clearSumoSelect($("#size-type-block"));
			clearSumoSelect($("#size-block"));
			setSizeTypesVisible(false);
			setSizeVisible(false);
		}

		loadAttributes(subcategory);
		loadSizeTypes(subcategory);
        loadPhotoSamples(subcategory);
	});
}

function updateProductInfo(successFunction, failFunction) {
	var data = getInfo();
	if (data == null) {
		return false;
	} else {
		$.ajax({
			url: ["/publication/properties", getProductId(), "info"].join("/"),
			type: "PUT",
			data: data
		}).done(function (data) {
			successFunction(data);
		}).fail(function (data) {
			failFunction(data);
		});
	}
}

function resetSumoSelect($sumoSelectDiv, values) {

	clearSumoSelect($sumoSelectDiv);

	var $select = $sumoSelectDiv.find("select");
	var $sumoSelect = $select[0].sumo;
	var $selectBox = $sumoSelectDiv.find(".SelectBox");

	values.forEach(function (item, i) {
		$sumoSelect.add(item.id, item.name, i + 1);
	});

	/*
	 (Костыль) при добавлении значений выше у нас автоматически выбирается 1е значение
	 */
	$select.val([]);

	/*
	 (Костыль) При смене верхней категории сбрасывался текст placeholder-а.
	 */
	var $optionLabel = $selectBox.children().first();
	if ($optionLabel.hasClass("placeholder")) {
		$optionLabel.text("Выберите");
	}

	//если у нас можно выбрать только один элемент, сразу выбираем его
	if (values.length == 1) {
		$sumoSelect.selectItem(1)
	}

}

function clearSumoSelect($sumoSelectDiv) {

	var $select = $sumoSelectDiv.find("select")[0].sumo;
	var $selectBox = $sumoSelectDiv.find(".SelectBox");

	//Это костыль для метода $categorySelect.add
	//Почему-то возникает ошибка, что эти переменные не найдены, при присваивании им значений
	opts = 1;
	opt = 2;

	var currentLength = $select.E.children().length;
	for (var i = currentLength; i > 1; i--) {
		$select.remove(i - 1);
	}
}

function getInfo() {
	var data = {};

	var subcategoryExists = $(".attributeBlock[data-name='subcategory']").length > 0;
	var categoryExists = $(".attributeBlock[data-name='category']").length > 0;
	if (subcategoryExists) {
		var subcategory = $("input[name='subcategory']:checked").val();
		if (subcategory == undefined) {
			alert("Не указана субкатегория");
			return null;
		}
		data.category = subcategory;
	} else if (categoryExists) {
		var category = $("input[name='category']:checked").val();
		if (category == undefined) {
			alert("Не указана категория");
			return null;
		}
		data.category = category;
	} else {
		data.category = $("#childCategory").attr("data-child-categoryId");
	}

	data.attributeValues = [];

	var attributes = $(".attributeBlock").filter(function (i) {
		var name = $(this).attr("data-name");
		return name != "category" && name != "subcategory";
	});
	var allChecked = true;
	attributes.each(function (index, value) {
		var inputGroup = $(value).find("input:checked");
		var inputGroupVal = inputGroup.val();
		if (inputGroupVal == undefined) {
			allChecked = false;
		} else {
			data.attributeValues.push(inputGroupVal);
		}
	});
	if (!allChecked) {
		alert("Указаны не все атрибуты");
		return null;
	}

	var $sizeTypeSelect = $("#size-type");
	var $sizeSelect = $("#size");

	var sizeType = $sizeTypeSelect.val();
	var size = $sizeSelect.val();
	if (sizeType == undefined || sizeType == null) {
		alert("Не выбран тип размера");
		return null;
	}
	if (size == undefined || sizeType == null) {
		alert("Не выбран размер товара");
		return null;
	}
	data.sizeType = sizeType;
	data.size = size;

	return data;
}

function getCurrentCategory() {
	var subcategoryExists = $(".attributeBlock[data-name='subcategory']").length > 0;
	var categoryExists = $(".attributeBlock[data-name='category']").length > 0;
	if (subcategoryExists) {
		var subcategory = $("input[name='subcategory']:checked").val();
		if (subcategory == undefined) {
			alert("Не указана субкатегория");
			return null;
		}
		data.category = subcategory;
	} else if (categoryExists) {
		var category = $("input[name='category']:checked").val();
		if (category == undefined) {
			alert("Не указана категория");
			return null;
		}
		data.category = category;
	} else {
		data.category = $("#childCategory").attr("data-child-categoryId");
	}
}

function setSizeTypesVisible(visible) {
	var block = $("#size-type-block");
	if (visible) {
		block.removeClass("hide");
	} else {
		block.addClass("hide");
	}
}

function setSizeVisible(visible) {
	var block = $("#size-block");
	if (visible) {
		block.removeClass("hide");
	} else {
		block.addClass("hide");
	}
}