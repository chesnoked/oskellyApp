package su.reddot.infrastructure.util;

public class Utils {

	private Utils() {
	}

	/**
	 * В случае, если первый символ URL-а не '/', добавляем ее впереди строки
	 *
	 * @param url
	 * @return
	 */
	public static String normalizeUrl(String url) {
		if (!url.substring(0, 1).equals("/")) {
			url = "/" + url;
		}
		return url;
	}
}
