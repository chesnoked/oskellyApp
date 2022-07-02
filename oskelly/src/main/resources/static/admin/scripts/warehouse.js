$('[data-open-details]').click(function (e) {
    e.preventDefault();
    $(this).next().toggleClass('is-active');
    $(this).toggleClass('is-active');
});

function takeToWarehouse(itemId, waybillId) {
    var confirmResponse = confirm("подтвердите размещение вещи на складе по накладной №" + waybillId.toString());

    if (!confirmResponse) {
        return;
    }

    $.ajax({
        type: "PUT",
        url: "/admin/warehouse/taketowarehouse",
        data: { itemId: itemId }
    }).done(function (data) {
        var itemToRemoveNode = document.getElementById(itemId);
        itemToRemoveNode.parentNode.removeChild(itemToRemoveNode);
    }).fail(function (data) {
        alert(data.responseText)
    });

}

function startVreification(itemId) {

    $.ajax({
        type: "PUT",
        url: "/admin/warehouse/startverification",
        data: { itemId: itemId }
    }).done(function (data) {
        //FIXME
        var labelToChange1 = document.getElementById('state-label-1-' + itemId);
        var labelToChange2 = document.getElementById('state-label-2-' + itemId);
        $(labelToChange1).removeClass('primary');
        $(labelToChange1).addClass('warning');
        labelToChange1.innerHTML = "На экспертизе";
        $(labelToChange2).removeClass('primary');
        $(labelToChange2).addClass('warning');
        labelToChange2.innerHTML = "На экспертизе";
        var buttonToDisable = document.getElementById('start-button-' + itemId);
        var buttonToEnable = document.getElementById('stop-button-' + itemId);
        $(buttonToDisable).addClass('disabled');
        $(buttonToEnable).removeClass('disabled');
        var switchGroup = document.getElementById('verificationSwitchGroup-' + itemId);
        $(switchGroup).removeClass('hide');
    }).fail(function (data) {
        alert(data.responseText);
    });

}

function stopVerification(itemId) {
    var state = 'EMPTY';
    var labelClass = '';
    var labelText = '';
    state = $('input[name=verificationGroup-'+itemId +']:checked').val();
    if (state == 'VERIFICATION_OK') {
        labelText = 'Экспертиза завершена успешно';
        labelClass = 'success';
    }
    if (state == 'VERIFICATION_NEED_CLEANING'){
        labelClass = 'secondary';
        labelText = 'Требуется химчистка';
    }
    if (state == 'REJECTED_AFTER_VERIFICATION'){
        labelText = 'Плохое качество. Возврат Продавцу';
        labelClass = 'alert';
    }

    $.ajax({
        type: "PUT",
        url: "/admin/warehouse/finalyverification",
        data: {
            state: state,
            itemId: itemId
        }
    }).done(function (data) {
        // FIXME АДДД!!! но мозг уже не варит на красиво
        var labelToChange1 = document.getElementById('state-label-1-' + itemId);
        var labelToChange2 = document.getElementById('state-label-2-' + itemId);
        $(labelToChange1).removeClass('warning');
        $(labelToChange1).addClass(labelClass);
        labelToChange1.innerHTML = labelText;
        $(labelToChange2).removeClass('warning');
        $(labelToChange2).addClass(labelClass);
        labelToChange2.innerHTML = labelText;
        var buttonToDisable = document.getElementById('stop-button-' + itemId);
        $(buttonToDisable).addClass('disabled');
        var switchGroup = document.getElementById('verificationSwitchGroup-' + itemId);
        $(switchGroup).addClass('hide');
    }).fail(function (data) {
        alert(data.responseText)
    });
}

function setReadyToShip(itemId) {
    var state = 'EMPTY';
    var labelText = 'Готово к отгрузке Покупателю';
    var labelClass = 'success';
    $.ajax({
        type: "PUT",
        url: "/admin/warehouse/setreadytoship",
        data: { itemId: itemId }
    }).done(function (data) {
        // FIXME АДДД!!! но мозг уже не варит на красиво
        var labelToChange1 = document.getElementById('state-label-1-' + itemId);
        var labelToChange2 = document.getElementById('state-label-2-' + itemId);
        $(labelToChange1).removeClass('warning');
        $(labelToChange1).addClass(labelClass);
        labelToChange1.innerHTML = labelText;
        $(labelToChange2).removeClass('warning');
        $(labelToChange2).addClass(labelClass);
        labelToChange2.innerHTML = labelText;
    }).fail(function (data) {
        alert(data.responseText)
    });

}

function createWaybill(itemId) {
    var confirmResponse = confirm("Вызов курьера по Товру №" + itemId.toString());

    if (!confirmResponse) {
        return;
    }

    $.ajax({
        type: "PUT",
        url: "/admin/warehouse/createwaybilltobuyer",
        data: { itemId: itemId }
    }).done(function (data) {
        var buttonToChange = document.getElementById(itemId+'-waybill-button');
        var labelToChange1 = document.getElementById('state-label-1-' + itemId);
        $(labelToChange1).removeClass('success');
        $(labelToChange1).addClass('warning');
        labelToChange1.innerHTML = "Ожидаем курьера";
        buttonToChange.removeEventListener("click", createWaybill);
        buttonToChange.addEventListener("click", function(){ sendToBuyer(itemId); });
        buttonToChange.innerHTML = "Передать курьеру";
        //для чистоты, можно и id поеменять, но после перезагрузки страницы всё равно будет норм
    }).fail(function (data) {
        alert(data.responseText)
    });

}

function sendToBuyer(itemId) {
    var confirmResponse = confirm("Вы передаёте курьеру вещь " + itemId.toString());

    if (!confirmResponse) {
        return;
    }

    $.ajax({
        type: "PUT",
        url: "/admin/warehouse/sendtobuyer",
        data: { itemId: itemId }
    }).done(function (data) {
        var itemToRemoveNode = document.getElementById(itemId);
        itemToRemoveNode.parentNode.removeChild(itemToRemoveNode);
    }).fail(function (data) {
        alert(data.responseText)
    });

}