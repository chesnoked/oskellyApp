package su.reddot.domain.model.logistic;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.order.OrderPosition;

import javax.persistence.*;
import java.util.UUID;

/**
 * @author Vitaliy Khludeev on 27.08.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Waybill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * uuid будем передавать в систему логистов
	 */
	private UUID uuid;

	/**
	 * Идентификатор в системе логистов
	 */
	private String externalSystemId;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "phone",    column = @Column(name = "pickup_phone")),
			@AttributeOverride(name = "zipCode",     column = @Column(name = "pickup_zip_code")),
			@AttributeOverride(name = "name",  column = @Column(name = "pickup_name")),
			@AttributeOverride(name = "address",        column = @Column(name = "pickup_address")),
	})
	private WaybillRequisite pickupRequisite;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "phone",    column = @Column(name = "delivery_phone")),
			@AttributeOverride(name = "zipCode",     column = @Column(name = "delivery_zip_code")),
			@AttributeOverride(name = "name",  column = @Column(name = "delivery_name")),
			@AttributeOverride(name = "address",        column = @Column(name = "delivery_address")),
	})
	private WaybillRequisite deliveryRequisite;

	@ManyToOne
	private OrderPosition orderPosition;

	@Enumerated(EnumType.STRING)
	private DestinationType pickupDestinationType;

	@Enumerated(EnumType.STRING)
	private DestinationType deliveryDestinationType;
	//TODO Нужна метка времени при стоздании
	@ManyToOne
	private WaybillOrder waybillOrder;
}
