package su.reddot.domain.service.wishlist;

public class WishListException extends Exception {

    public WishListException() {
    }

    public WishListException(String message) {
        super(message);
    }

    public WishListException(String message, Throwable cause) {
        super(message, cause);
    }

    public WishListException(Throwable cause) {
        super(cause);
    }
}
