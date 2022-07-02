package su.reddot.domain.service.catalog.size;

import lombok.Value;
import su.reddot.domain.model.size.SizeType;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import java.util.List;

/**
 * Используется в Mobile Api
 * @see su.reddot.presentation.mobile.api.v1.PublicationRestControllerV1#getPublicationInfo(UserIdAuthenticationToken, Long)
 */
@Value
public class CatalogSize {

    private final SizeType sizeType;

    private List<SizeView> values;
}
