package su.reddot.infrastructure.util;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Класс нужен для уведомления пользователя об ошибках, к которым он имеет отношение (например, неправильно ввел пароль
 * или забыл выбрать категорию товара).
 * Используется в качестве замены exception-ам в том случае, если вполне ожидается,
 * что действия пользователя будут некорректны
 */
public class ErrorNotification {

	private final Map<String, String> errors;

	public ErrorNotification() {
		errors = new TreeMap<>();
	}

	/**
	 * В случае, если ранее была ошибка с тем же полем, ошибка перетрется
	 *
	 * @param field   имя поля на стороне клиента
	 * @param message сообщение об ошибке
	 */
	public void add(String field, String message) {
		errors.put(field, message);
	}

	public boolean hasErrors() {
		return errors.size() > 0;
	}

	public Map<String, String> getFirstError() {
		Map.Entry<String, String> next = errors.entrySet().iterator().next();
		return Collections.singletonMap(next.getKey(), next.getValue());
	}

	public Map<String, String> getAll() {
		return errors;
	}
}
