package su.reddot.domain.exception;

/** Используется в случае, если запрашиваемая сущность не найдена */
public class NotFoundException extends Exception {
    public NotFoundException(String message) { super(message); }
}
