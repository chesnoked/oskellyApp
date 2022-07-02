package su.reddot.infrastructure.security.exception;

public class LoginAuthenticationException extends CustomAuthenticationException {
	public LoginAuthenticationException(String message) {
		super(message);
	}

	public LoginAuthenticationException(Throwable cause) {
		super(cause);
	}

	public LoginAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
