package su.reddot.domain.service.publication.info.view;

import lombok.Value;

/**
 * Id - это Enum::name() (на клиенте выставляется как Id),
 * name - это расширенное описание типа размера, которое отображается для клиента
 */
@Value
public class PublicationSizeTypeView {
	String id, name;
}
