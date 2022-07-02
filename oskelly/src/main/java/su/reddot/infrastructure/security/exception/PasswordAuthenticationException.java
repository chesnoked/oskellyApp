package su.reddot.infrastructure.security.exception;

public class PasswordAuthenticationException extends CustomAuthenticationException {
	public PasswordAuthenticationException(String message) {
		super(message);
	}

	public PasswordAuthenticationException(Throwable cause) {
		super(cause);
	}

	public PasswordAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
