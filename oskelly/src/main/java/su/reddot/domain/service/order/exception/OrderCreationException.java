package su.reddot.domain.service.order.exception;

public class OrderCreationException extends RuntimeException {
    public OrderCreationException(String reason) {
        super(reason);
    }
}
