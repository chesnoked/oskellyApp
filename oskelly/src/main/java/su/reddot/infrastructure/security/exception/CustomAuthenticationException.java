package su.reddot.infrastructure.security.exception;

import su.reddot.infrastructure.security.SecurityException;

public class CustomAuthenticationException extends SecurityException {

    public CustomAuthenticationException(String message) {
        super(message);
    }

    public CustomAuthenticationException(Throwable cause) {
        super(cause);
    }

    public CustomAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
