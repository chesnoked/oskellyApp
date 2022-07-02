let FilterProductByState = function () {
    const stateProductSelect = $('#state-select');

    stateProductSelect.on('change', function (event) {
        let stateProductSelect = $(':selected', this).attr('value');
        window.location.replace("/account/products?state=" + stateProductSelect);
    })
};

$(function () {
    new FilterProductByState();
});