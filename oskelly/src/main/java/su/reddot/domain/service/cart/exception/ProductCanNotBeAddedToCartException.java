package su.reddot.domain.service.cart.exception;

/**
 * Товар нельзя положить в корзину:
 * продавец снял товар с продажи, модератор заблокировал товар,
 * другой пользователь забронировал товар и пр.
 */
public class ProductCanNotBeAddedToCartException extends Exception {
    public ProductCanNotBeAddedToCartException(String reason) {
        super(reason);
    }
}
