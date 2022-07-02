function getPublicationRequest() {
	var request = {};
	request.article = $("input[name='article']").val();
	request.category = $("select[name='category']").val();
	request.brand = $("select[name='brand']").val();
	request.description = $("textarea[name='description']").val();
	request.sizeType = $("select[name='sizeType']").val();
	request.rrpPrice = $("input[name='rrpPrice']").val();
	request.vintage = $("input[name='vintage']:checked").length > 0;
	request.newCollection = $("input[name='newCollection']:checked").length > 0;

	var attributeValues = [];
	$("select[name='attribute']").each(function (index, value) {
		attributeValues.push($(value).val());
	});
	request.attributeValues = attributeValues;

	var items = [];
	$(".oneItemsBlock").each(function (index, value) {
		if($(value).find(".count").val() !== null && $.trim($(value).find(".count").val()) !== "") {
			var item = {};
			item.count = $(value).find(".count").val();
			item.price = $(value).find(".price").val();
			item.size = {};
			item.size.id = $(value).find(".size").val();
			items.push(item);
		}
	});
	request.productSizeMappings = items;
	return request;
}

(function () {
	let rootCategorySelect = $("#rootCategorySelect");
	let categorySelectLabel = $("#categorySelectLabel");
	let attributesBlock = $("#attributesBlock");
	let sizesBlock = $("#sizesBlock");
	let sizesContainer = $("#sizesContainer");
	rootCategorySelect.on('change', function () {
		jQuery.ajax({
			url: "/admin/api/v1/moderation/leafCategories",
			type: "GET",
			data: {
				categoryId: rootCategorySelect.val()
			}
		})
			.done(function (data) {
				$("#categoryBlock").html(data);
				$("#categoryBlock").find('select').SumoSelect({
					search: true,
					searchText: 'Поиск...'
				});
				attributesBlock.html("");
				sizesContainer.html("");
				sizesBlock.hide();
				addCategoryChangeListener($("#categorySelect"));
			})

	});
	function addCategoryChangeListener(select) {
		select.on('change', function () {
			jQuery.ajax({
				url: "/admin/api/v1/moderation/attributes",
				type: "GET",
				data: {
					categoryId: select.val()
				}
			})
				.done(function (data) {
					attributesBlock.html(data);
					sizesContainer.html("");
					sizesBlock.hide();
					attributesBlock.find('select').SumoSelect({
						search: true,
						searchText: 'Поиск...'
					});
					addSizeTypeChangeListener($("#sizeTypeSelect"));
					if($("#sizeTypeSelect").val() !== null) {
						loadSizes($("#sizeTypeSelect"));
					}
				})

		});
	}

	function loadSizes(select) {
		jQuery.ajax({
			url: "/admin/api/v1/moderation/sizes",
			type: "GET",
			data: {
				sizeType: select.val(),
				categoryId: $("#categorySelect").val()
			}
		})
			.done(function (data) {
				sizesBlock.show();
				sizesContainer.append(data);
				sizesBlock.find('select').SumoSelect({
					search: true,
					searchText: 'Поиск...'
				});
			})
	}

	function addSizeTypeChangeListener(select) {
		select.on('change', function () {
			sizesContainer.html("");
			loadSizes(select);

		});
	}
	addCategoryChangeListener($("#categorySelect"));

	$('#addSizeButton').on('click', function () {
		sizesContainer.append('<hr/>');
		loadSizes($("#sizeTypeSelect"));
	});

	$('#saveProductButton').click(function () {
		var request = getPublicationRequest();

		jQuery.ajax({
			url: "/admin/moderation/publish",
			type: "POST",
			data: JSON.stringify(request),
			contentType: "application/json"
		})
			.done(function (data) {
				if(confirm("Добавить еще товар?")) {
					window.location.replace("/admin/moderation?addMore=true")
				}
				else {
					window.location.replace("/admin/moderation")
				}
			})
			.fail(function (data) {
				showErrorFieldAlert(data);
			})
	});

	function showErrorFieldAlert(data) {
		var errors = JSON.parse(data.responseText);
		var errorText = '';
		for (key in errors) {
			errorText += errors[key] + "\n";
		}
		alert(errorText);
	}

})();

(function () {
	if($('#addProductModal').attr("initial-state") === 'open') {
		$.magnificPopup.open({
			items: {
				src: '#addProductModal'
			},
			type: 'inline'
		});
	}
})();

function calculateCommission(arg) {
	jQuery.ajax({
		url: "/commission/price",
		type: "GET",
		data: {
			category: getPublicationRequest().category,
			price: $(arg).val(),
			newCollection: $("input[name='newCollection']:checked").length > 0
		}
	})
		.done(function (data) {
			$(arg).parent().parent().find(".priceWithCommission").val(data)
		})
}