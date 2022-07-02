/* вызовы событий аналитики*/
/* PRODUCT PAGE*/

function ga_product_imgClick(){
    dataLayer.push(
        {'event': 'img-click'}
    );
}
//+
function ga_product_sizeTableClick(){
    dataLayer.push(
        {'event': 'size-click'}
    );
}
//+
function ga_product_sizeCheck(){
    dataLayer.push(
        {'event': 'size-check-click'}
    );
}
//Возможно никодга не сработает
function ga_product_noSizeCheckedError(){
    dataLayer.push(
        {'event': 'add-click-error-size'}
    );
}
//+
//Клик наеавторизованного по "добавить в карзину"
function ga_product_anonymousAddToCartkError(){
    dataLayer.push(
        {'event': 'add-click-error-authorization'}
    );
}
//+
function ga_product_addToCartSuccess(){
    dataLayer.push(
        {'event': 'add-success'}
    );
}
//+
function ga_product_anonymousOfferError(){
    dataLayer.push(
        {'event': 'offer-error-authorization'}
    );
}
//+
function ga_product_offerOpenSuccess(){
    dataLayer.push(
        {'event': 'offer-success'}
    );
}

function ga_product_offerSubmit() {
    dataLayer.push({
        'event': 'offer-submit'
    });
}

function ga_product_alertSubmit() {
    dataLayer.push({
        'event': 'soobschit-o-nalichii-submit'
    });


}
function ga_product_wishlistAddSuccess(){
    dataLayer.push(
        {'event': 'wish-success'}
    );
}
//+
function ga_product_anonymousWishlistAddError(){
    dataLayer.push(
        {'event': 'wish-error-authorization'}
    );
}
//
function ga_product_anonymousPriceFollowAddError(){
    dataLayer.push(
        {'event': 'follow-error-authorization'}
    );
}
function ga_product_priceFollowAddSuccess(){
    dataLayer.push(
        {'event': 'follow-success'}
    );
}
function ga_product_share() {
    dataLayer.push({'event': 'share'});
}

/* ORDER PAGE*/

function ga_order_done(orderData) {
    var url_string = window.location.href;
    var url = new URL(url_string);
    var param = url.searchParams.get("stage").toLocaleLowerCase();
    if (param === "done"){
        dataLayer.push({
            'transactionId': orderData.transactionId,
            'transactionAffiliation': 'OSKELLY.RU',
            'transactionTotal': orderData.transactionTotal,
            'transactionProducts': orderData.transactionProducts
        })
    }

}

/*Gift CARD PAGE*/

function ga_giftcard_submit() {
    dataLayer.push({
        'event': 'gift-card-submit'
    });

}