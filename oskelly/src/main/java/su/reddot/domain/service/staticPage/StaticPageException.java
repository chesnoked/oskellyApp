package su.reddot.domain.service.staticPage;

public class StaticPageException extends Exception {
	public StaticPageException() {
	}

	public StaticPageException(String message) {
		super(message);
	}

	public StaticPageException(String message, Throwable cause) {
		super(message, cause);
	}

	public StaticPageException(Throwable cause) {
		super(cause);
	}
}
