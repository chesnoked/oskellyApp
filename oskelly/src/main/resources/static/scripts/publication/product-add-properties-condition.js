$(function () {
    const OSKELLY_COMISSION = 0.3;

    $("#confirm-condition-next, #confirm-condition-back").click(function () {
        var href = $(this).attr("href");
        var success = function (data) {
            location.href = href;
        };
        var fail = function (data) {
            showErrorFieldAlert(data);
        };
        updateProductCondition(success, fail);
        return false;
    });
});

function calculateCommission(arg) {
	jQuery.ajax({
		url: ["/publication/properties", getProductId(), "commission"].join("/"),
		type: "GET",
		data: {
			price: $(arg).val()
		}
	})
		.done(function (data) {
			$("#price-seller").val(data)
		})
}

function updateProductCondition(success, fail) {
    var data = getProductCondition();
    if (data == null) {
        return false;
    } else {
        $.ajax({
            url: ["/publication/properties", getProductId(), "condition"].join("/"),
            type: "PUT",
            data: data
        }).done(function (data) {
            success(data);
        }).fail(function (data) {
            fail(data);
        });
    }
}


function getProductCondition() {
    var conditionId = $("input[name='condition']:checked").val();
    if (conditionId == undefined) {
        alert("Не указано состояние товара");
        return null;
    }
    var price = $("#price-oskelly").val();
    if (price == undefined || price == null || price.trim().length == 0) {
        alert("Не указана цена товара");
        return null;
    }
    var data = {};
    data.conditionId = conditionId;
    data.price = price;
    return data;
}
