(function($, window, document) {
$(function() {

	var currentPage = 1;
    var MODERATION_PRODUCTS_API = "/admin/api/v1/moderation/products";
	var totalPages = $("[data-total-pages]").attr("data-total-pages");

	let productsContainer = $("#products");
	let productsTotalAmountLabel = $("#totalAmount");

	function applyFilters() {
		productsContainer.fadeTo(300, 0.2);
		currentPage = 1;
		reloadData(currentPage)
			.done(function (renderedProducts) {
				productsContainer.html(renderedProducts.content);
				totalPages = renderedProducts.totalPages;
                productsTotalAmountLabel.html(renderedProducts.totalProducts ? renderedProducts.totalProducts : "0" );
			})
			.fail(function () {
				alert("Произошла ошибка при загрузке данных");
			})
			.always(function () {
				productsContainer.fadeTo(300, 1);
			});
	}

    $('#state-select').on('change', function (event) {
        applyFilters();
    });

	$('select[name=pro]').change(function() {
		applyFilters();
	});

	$('#sort').change(function() {
		applyFilters();
	});

	$('#applyFilters').click(function () {
		applyFilters();
		$.magnificPopup.close();
	});

	$('input[type=checkbox][name=vip]').change(function() {
		applyFilters();
	});

    function reloadData (currentPage) {
		let currentState = $('#state-select :selected').attr('value');
		let currentCondition = $('input[type=radio][name=conditionFilter]:checked').val();
		return jQuery.ajax({
            url: MODERATION_PRODUCTS_API,
            type: "GET",
            data: {
            	state: currentState === 'ALL' ? null : currentState,
				page: currentPage,
				pro: $("#pro").val() !== 'ALL' ? $("#pro").val() : null,
				vip: document.getElementById('vip').checked ? true : null,
				sort:  $("#sort").val() !== 'ALL' ? $("#sort").val() : null,
				brand: $("#brandFilter").val() !== 'ALL' ? $("#brandFilter").val() : null,
				startPrice: $("#startPriceFilter").val(),
				endPrice: $("#endPriceFilter").val(),
				condition: currentCondition,
				category: $("#categorySelectFilter").val() !== 'ALL' ? $("#categorySelectFilter").val() : null,
				newCollection: document.getElementById('newCollectionFilter').checked ? true : null,
				descriptionExists: document.getElementById('descriptionExistsFilter').checked ? false : null,
				email: $("#emailFilter").val() === '' ? null : $("#emailFilter").val()
			}
        })
	}

	(function initInfiniteScroll() {

		if (totalPages < 2) { return; }

		let scrolledElement = $("#scrollContainer");

		let scrollIsBlocked = false;
		scrolledElement.scroll(handleScroll);
		scrolledElement.on("touchmove", handleScroll);

		function handleScroll() {
			if (scrollIsBlocked) { return; }
			let scrollHeight = this.scrollHeight;
			let currentScrollPosition = this.scrollTop;
			let screenHeight = document.documentElement.clientHeight;

			if (currentScrollPosition + screenHeight < scrollHeight * 2 / 3) { return; }

			if (currentPage >= totalPages) {
				return;
			}

			++currentPage;
			scrollIsBlocked = true;
			reloadData(currentPage)
				.done(function (renderedProducts) {
					productsContainer.append(renderedProducts.content);
					totalPages = renderedProducts.totalPages;
					scrollIsBlocked = false;
				})
				.fail(function () {
					alert("Произошла ошибка при загрузке данных");
				});
		}

	})();
});

})(window.jQuery, window, document);

