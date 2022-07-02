/* вызовы событий аналитики FB*/

//Клик наеавторизованного по "добавить в карзину"
function fb_product_anonymousAddToCartkError(){
    //fbq('track', 'AddToCart');
}

//successfully added to cart
function fb_product_addToCartSuccess(){
    fbq('track', 'AddToCart');
}

//start creating order
function fb_initiateCheckout(){
    fbq('track', 'InitiateCheckout');
}

//registration complete
function fb_completeRegistration(){
    fbq('track', 'CompleteRegistration');
}

//order payed
function fb_orderPayed(){
    fbq('track', 'Lead');
}