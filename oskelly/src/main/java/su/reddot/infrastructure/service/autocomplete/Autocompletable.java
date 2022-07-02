package su.reddot.infrastructure.service.autocomplete;

import java.util.List;

/**
 * Данный интерфейс реализуют те сервисы, которые при работе со своими моделями/сущностями могут предоставлять поиск
 * наиболее подходящих вариантов полного слова для части слова
 */
public interface Autocompletable {

	/**
	 * Поиск по части слова
	 *
	 * @param value часть слова
	 * @return список сопадений. Каждое совпадение должно содержать ID той сущности, по которой осуществлялся поиск,
	 * а также полное текстовое значение, по которому произошло совпадение
	 */
	List<AutocompleteMatch> matchAutocompletes(String value);

	/**
	 * @return Класс той сущности, работу с которой осуществляет сервис
	 */
	Class getAutocompleteType();
}
