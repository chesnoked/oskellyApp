$(function () {

    $("#complete-publication").click(function () {
        var success = function (data) {
            sendToModeration();
        };
        var fail = function (data) {
            showErrorFieldAlert(data);
        };

        saveCurrentTab(success, fail);
    });

    $("#form-tabs").find("a").click(function () {
        var href = $(this).attr("href");

        var success = function (data) {
            location.href = href;
        };
        var fail = function (data) {
            showErrorFieldAlert(data);
        };

        saveCurrentTab(success, fail);
        return false;
    });

    function saveCurrentTab(success, fail) {
        var $currentPane = $(".tabs-content").find("div[role='tabpanel'][aria-hidden='false']");

        var currentPaneId = $currentPane.attr("id");
        switch (currentPaneId) {
            case "tab-form-information":
                updateProductInfo(success, fail);
                break;
            case "tab-form-photos":
                checkPhotosUploaded(success);
                break;
            case "tab-form-description":
                updateProductDescription(success, fail);
                break;
            case "tab-form-condition-and-pricing":
                updateProductCondition(success, fail);
                break;
            case "tab-form-seller":
                updateSellerInfo(success, fail, false);
                break;
            default:
                success();
        }
    }

});