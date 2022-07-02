$(function () {
    var childCategory = $('#childCategory');

    $("#first-step-add-product").click(function () {
        var category = $("#category1").is(':checked') || $("#category2").is(':checked') || $("#category3").is(':checked');
        var child = $('#childCategory').val();
        if (category) {
        	childCategory.prop('disabled', false);
		}
    });

	$("input[name='top-category']").click(function () {
		//Это костыль для метода $sumoCategorySelect.add
		//Почему-то возникает ошибка, что эти переменные не найдены, при присваивании им значений
		opts = 1;
		opt = 2;

		var $input = $(this);
		var $categorySelect = $("#childCategory");
		var $sumoCategorySelect = $categorySelect[0].sumo;
		var $selectBox = $(".SelectBox").first();
		var categoryId = $input.val();
		var childCategoriesUrl = ['/api/v1/categories', categoryId, 'childs'].join('/');
		$.getJSON(childCategoriesUrl)
			.done(function (data) {
				if (data.length > 0) {
					var currentLength = $sumoCategorySelect.E.children().length;
					for (var i = currentLength; i > 1; i--) {
						$sumoCategorySelect.remove(i - 1);
					}

					data.forEach(function (item, i) {
						$sumoCategorySelect.add(item.id, item.displayName, i + 1);
					});

					/*
					 (Костыль) Выше при добавлении новых значений у нас автоматически выбралось 1е значение.
					 Здесь мы его сбрасываем
					 */
					$categorySelect.val([]);

					/*
					 (Костыль) При смене верхней категории сбрасывался текст placeholder-а.
					 */
					var $optionLabel = $selectBox.children().first();
					if ($optionLabel.hasClass("placeholder")) {
						$optionLabel.text("Выберите");
					}
				}
			});
		return true;
	});

	$("#button-submit").click(function () {
		var brand = $("#brand").val();
		var topCategory = $("input[name='top-category']:checked").val();
		var childCategory = $("#childCategory").val();


		if (fieldIsEmpty(brand) || fieldIsEmpty(topCategory) || fieldIsEmpty(childCategory)) {
			console.log("Указаны не все параметры");
			return;
		}
		var data = {brand: brand, topCategory: topCategory, childCategory: childCategory};
		$.ajax({
			type: "POST",
			url: "/publication",
			data: data
		}).done(function (data) {
			location.href = ["/publication", "properties", data.productId].join("/");
		}).fail(function (data) {
			console.log(data.responseText);
		});
	});

	function fieldIsEmpty(field) {
		return field == null || field == undefined;
	}
});