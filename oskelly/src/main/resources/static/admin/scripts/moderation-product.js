(function($, window, document) {
	$('.slider-for').slick({
		slidesToShow: 1,
		slidesToScroll: 1,
		arrows: false,
		fade: true,
		asNavFor: '.slider-nav'
	});
	$('.slider-nav').slick({
		slidesToShow: 4,
		slidesToScroll: 1,
		asNavFor: '.slider-for',
		dots: true,
		centerMode: true,
		focusOnSelect: true
	});

	var PRODUCTS_API = "/api/v1/products";
	var decisionButtons = $(".button[data-product-id]");
	decisionButtons.each(handleDecisionButton);
	function handleDecisionButton() {
		var $button = $(this);

		var newProductState;
		if ($button.attr("id") === "publish-button") {
			newProductState = "PUBLISHED";
		}
		else if ($button.attr("id") === "reject-button") {
			newProductState = "REJECTED";
		}
		else if ($button.attr("id") === "hide-button"){
            newProductState = "HIDDEN";
		}
		else { /* элемент не содержит информации о состоянии товара */
		}

		if (newProductState) {
			var productId = $button.attr("data-product-id");

			$button.click(function () {
				updateProductState(productId, newProductState)
					.done( function() { window.location.reload(); } )
					.fail( function() { alert("Произошла ошибка"); } );
			});
		}
	}

	$(".button.cancel").click(function () {
		if(confirm("Вы уверены?")) {
			window.close();
		}
	});

	function updateProductState(productId, state) {
		return jQuery.ajax({
			url: [PRODUCTS_API, productId].join("/"),
			type: "PUT",
			data: "state=" + state
		});
	}

	(function () {
		let $attributesForm = $("#attributesForm");
		let $saveButton = $attributesForm.find("button.save");
		let $editButton = $attributesForm.find("button.edit");
		$editButton.click(function () {
			$(this).hide();
			$attributesForm.find("input").removeAttr("disabled");
			$saveButton.show();
			return false;
		});
		$saveButton.click(function () {
			var $data = getInfo();
			if($data === null) {
				return false;
			}
			$attributesForm.fadeTo(300, 0.2);
			$data.productId = $attributesForm.attr("data-product-id");
			$.ajax({
				url: "/admin/moderation/attributes",
				type: "PUT",
				data: $data
			})
				.done(function() {
					$saveButton.hide();
					$attributesForm.find("input").attr("disabled", "disabled");
					$editButton.show();
					window.location.reload();
				})
				.fail(function() {
					alert("Произошла ошибка");
				})
				.always(function () {
					$attributesForm.fadeTo(300, 1);
				});
			return false;
		});
	})();

	function getMarks() {
		var data = {};
		var vintage = $("input[name='vintage']:checked").length > 0;
		var newCollection = $("input[name='newCollection']:checked").length > 0;
		var ourChoice = $("input[name='ourChoice']:checked").length > 0;
		data.vintage = vintage;
		data.ourChoice = ourChoice;
		data.newCollection = newCollection;
		data.productId = $("#attributesForm").attr("data-product-id");
		return data;
	}

	(function () {
		let $attributesForm = $("#marksBlock");
		let $saveButton = $attributesForm.find("button.save");
		let $editButton = $attributesForm.find("button.edit");
		$editButton.click(function () {
			$(this).hide();
			$attributesForm.find("input").removeAttr("disabled");
			$saveButton.show();
			return false;
		});
		$saveButton.click(function () {
			var $data = getMarks();
			if($data === null) {
				return false;
			}
			$attributesForm.fadeTo(300, 0.2);
			$.ajax({
				url: "/admin/moderation/marks",
				type: "PUT",
				data: $data
			})
				.done(function() {
					$saveButton.hide();
					$attributesForm.find("input").attr("disabled", "disabled");
					$editButton.show();
					window.location.reload();
				})
				.fail(function() {
					alert("Произошла ошибка");
				})
				.always(function () {
					$attributesForm.fadeTo(300, 1);
				});
			return false;
		});
	})();

	function getDescription() {
		var data = {};
		data.origin = $("#origin").val();
		data.purchasePrice = $("#purchase-price").val();
		data.purchaseYear = $("#purchase-year").val();
		data.productId = $("#attributesForm").attr("data-product-id");
		data.description = $("#description").val();
		data.rrp = $("#rrp").val();
		data.vendorCode = $("#vendorCode").val();
		data.model = $("#model").val();
		return data;
	}

	(function () {
		let $attributesForm = $("#descriptionBlock");
		let $saveButton = $attributesForm.find("button.save");
		let $editButton = $attributesForm.find("button.edit");
		$editButton.click(function () {
			$(this).hide();
			$attributesForm.find("input").removeAttr("disabled");
			$attributesForm.find("textarea").removeAttr("disabled");
			$saveButton.show();
			return false;
		});
		$saveButton.click(function () {
			var $data = getDescription();
			if($data === null) {
				return false;
			}
			$attributesForm.fadeTo(300, 0.2);
			$.ajax({
				url: "/admin/moderation/description",
				type: "PUT",
				data: $data
			})
				.done(function() {
					$saveButton.hide();
					$attributesForm.find("input").attr("disabled", "disabled");
					$attributesForm.find("textArea").attr("disabled", "disabled");
					$editButton.show();
					window.location.reload();
				})
				.fail(function(data) {
					showErrorFieldAlert(data);
				})
				.always(function () {
					$attributesForm.fadeTo(300, 1);
				});
			return false;
		});
	})();

	function getBrand() {
		var data = {};
		data.brandId = $("#brand").val();
		data.productId = $("#attributesForm").attr("data-product-id");
		return data;
	}

	(function () {
		let $attributesForm = $("#brandBlock");
		let $saveButton = $attributesForm.find("button.save");
		let $editButton = $attributesForm.find("button.edit");
		$editButton.click(function () {
			$(this).hide();
			$attributesForm.find("input").removeAttr("disabled");
			$attributesForm.find("select").removeAttr("disabled");
			$attributesForm.find("textarea").removeAttr("disabled");
			$saveButton.show();
			return false;
		});
		$saveButton.click(function () {
			var $data = getBrand();
			if($data === null) {
				return false;
			}
			$attributesForm.fadeTo(300, 0.2);
			$.ajax({
				url: "/admin/moderation/brand",
				type: "PUT",
				data: $data
			})
				.done(function() {
					$saveButton.hide();
					$attributesForm.find("input").attr("disabled", "disabled");
					$attributesForm.find("select").attr("disabled", "disabled");
					$attributesForm.find("textArea").attr("disabled", "disabled");
					$editButton.show();
				})
				.fail(function() {
					alert("Произошла ошибка");
				})
				.always(function () {
					$attributesForm.fadeTo(300, 1);
				});
			return false;
		});
	})();


	(function () {
		let $attributesForm = $("#tab-form-photos");
		let $saveButton = $attributesForm.find("button.save");
		$saveButton.click(function () {
			window.location.reload();
		});
	})();

	setSubcategoryButtonHandler();
	function setSubcategoryButtonHandler() {
		$("input[name='subcategory']").click(function () {
			//id выбранной подкатегории
			var subcategory = $(this).val();

			//все группы атрибутов, кроме категории и подкатегории
			var loadedGroups = $(".attributeBlock").filter(function (i) {
				var name = $(this).attr("data-name");
				return name != "category" && name != "subcategory";
			});
			//проверяем, были ли ранее загружены атрибуты и подкатегории
			if (loadedGroups.length == 0) {
				//если нет, то грузим и отображаем атрибуты
				loadAttributesForSubcategory(subcategory);
				//показываем размеры
			} else {
				//если да, то запрашиваем подтверждение (атрибуты сбросятся)
				var result = confirm("Вы уверены что хотите изменить подкатегорию товара?\n" +
					"Внесённые значения атрибутов не сохранятся!");
				if (result) {
					//перезаписываем атрибуты
					loadedGroups.remove();
					loadAttributesForSubcategory(subcategory);
					//размеры ранее были открыты, переоткрывать их не надо
				} else {
					return false;
				}
			}
		});
	}

	setCategoryButtonHandler();
	function setCategoryButtonHandler() {
		$("input[name='category']").click(function () {
			//id выбранной категории
			var category = $(this).val();

			//все группы атрибутов, кроме категории
			var loadedGroups = $(".attributeBlock").filter(function (i) {
				var name = $(this).attr("data-name");
				return name != "category";
			});
			//проверяем, были ли ранее загружены атрибуты и подкатегории
			if (loadedGroups.length == 0) {
				//если нет, то грузим
				loadAttributesForCategory(category);
			} else {
				//если да, то запрашиваем подтверждение (атрибуты сбросятся)
				var result = confirm("Вы уверены что хотите изменить категорию товара?\n" +
					"Внесённые значения атрибутов не сохранятся!");
				if (result) {
					loadedGroups.remove();
					//скрываем размеры
					loadAttributesForCategory(category);
				} else {
					return false;
				}
			}
		});
	}

	function loadAttributesForCategory(categoryId) {
		$.get({
			url: "/publication/properties/attributes?categoryId=" + categoryId
		}).done(function (data) {
			$(".attributeBlock").last().after(data);
			var subcategories = $("input[name='subcategory']");
			if (subcategories.length != 0) {
				//мы загрузили подкатегории
				setSubcategoryButtonHandler();
			} else {
			}
		});
	}

	function loadAttributesForSubcategory(subcategoryId) {
		$.get({
			url: "/publication/properties/attributes?categoryId=" + subcategoryId
		}).done(function (data) {
			$(".attributeBlock").last().after(data);
		});
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

		return data;
	}




	(function () {
		let $attributesForm = $("#conditionBlock");
		let $saveButton = $attributesForm.find("button.save");
		let $editButton = $attributesForm.find("button.edit");
		$editButton.click(function () {
			$(this).hide();
			$attributesForm.find("input").removeAttr("disabled");
			$saveButton.show();
			return false;
		});
		$saveButton.click(function () {
			var $data = getProductCondition();
			if($data === null) {
				return false;
			}
			$attributesForm.fadeTo(300, 0.2);
			$.ajax({
				url: "/admin/moderation/condition",
				type: "PUT",
				data: $data
			})
				.done(function() {
					$saveButton.hide();
					$attributesForm.find("input").attr("disabled", "disabled");
					$editButton.show();
				})
				.fail(function() {
					alert("Произошла ошибка");
				})
				.always(function () {
					$attributesForm.fadeTo(300, 1);
				});
			return false;
		});
	})();

	function getSizeType() {
		var data = {};
		data.productId = $("#attributesForm").attr("data-product-id");
		data.sizeType = $("#size-type").val();
		return data;
	}

	(function () {
		let $attributesForm = $("#sizeTypeBlock");
		let $saveButton = $attributesForm.find("button.save");
		let $editButton = $attributesForm.find("button.edit");
		$editButton.click(function () {
			$(this).hide();
			$attributesForm.find("input").removeAttr("disabled");
			$attributesForm.find("select").removeAttr("disabled");
			$saveButton.show();
			return false;
		});
		$saveButton.click(function () {
			var $data = getSizeType();
			if($data === null) {
				return false;
			}
			$attributesForm.fadeTo(300, 0.2);
			$.ajax({
				url: "/admin/moderation/sizeType",
				type: "PUT",
				data: $data
			})
				.done(function() {
					$saveButton.hide();
					$attributesForm.find("input").attr("disabled", "disabled");
					$attributesForm.find("select").attr("disabled", "disabled");
					$editButton.show();
					window.location.reload()
				})
				.fail(function() {
					alert("Произошла ошибка");
				})
				.always(function () {
					$attributesForm.fadeTo(300, 1);
				});
			return false;
		});
	})();

	function getProductCondition() {
		var conditionId = $("input[name='condition']:checked").val();
		if (conditionId == undefined) {
			alert("Не указано состояние товара");
			return null;
		}
		var data = {};
		data.conditionId = conditionId;
		data.productId = $("#attributesForm").attr("data-product-id");
		return data;
	}

	(function () {
		$("#saveItemsButton").click(function () {
			var $items = [];
			$.each($("#itemsTable").find("tbody tr"), function (index, value) {
				var $item = {};
				$item.size = {};
				$item.size.id = $(value).find(".size").val();
				$item.count = $(value).find(".count").val();
				$item.price = $(value).find(".price").val();
				$items.push($item);
			});
			var $data = {};
			$data.productId = $("#attributesForm").attr("data-product-id");
			$data.productSizeMappings = $items;
			$.ajax({
				url: "/admin/moderation/items",
				type: "PUT",
				data: JSON.stringify($data),
				contentType: "application/json"
			})
			.done(function() {
				window.location.reload();
			})
			.fail(function(data) {
				showErrorFieldAlert(data);
			});
		});
	})();

	(function () {
		$("#addItemsButton").click(function () {
			$("#itemsTable").find("tbody").append($("#itemTemplateContainer").html());
		});
	})();

	function showErrorFieldAlert(data) {
		var errors = JSON.parse(data.responseText);
		var errorText = '';
		for (key in errors) {
			errorText += errors[key] + "\n";
		}
		alert(errorText);
	}

	(function () {
		$("#sendToModerationButton").click(function () {
			$.ajax({
				url: "/admin/moderation/sendToModeration",
				type: "POST",
				data: {
					productId: $("#attributesForm").attr("data-product-id")
				}
			})
				.done(function() {
					window.location.reload();
				})
				.fail(function(data) {
					showErrorFieldAlert(data);
				});
		});
	})();

})(window.jQuery, window, document);

