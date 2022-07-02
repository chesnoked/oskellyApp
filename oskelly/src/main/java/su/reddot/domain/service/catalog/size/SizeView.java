package su.reddot.domain.service.catalog.size;

import lombok.Value;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

/**
 * Используется в Mobile Api
 * @see su.reddot.presentation.mobile.api.v1.PublicationRestControllerV1#getPublicationInfo(UserIdAuthenticationToken, Long)
 */
@Value
public class SizeView {

	private final Long id;

	private final String name;

	/** <p>Значения размера во всех сетках, в которых оно задано, в виде json строки:
	 *  {"RUSSIAN" : "42", "WAIST_INCHES" : "32,4" , ... , "INTERNATIONAL" : "L"}</p>
     *  <p>Используется только на странице категории для отображения фильтра по размеру. В остальных случаях это свойство можно не задавать.</p>
	 **/
	private final String optionalValuesForAllSizeTypes;
}