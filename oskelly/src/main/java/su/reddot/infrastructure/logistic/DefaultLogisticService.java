package su.reddot.infrastructure.logistic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import logistic.wsdl.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import su.reddot.domain.dao.logistic.WaybillOrderRepository;
import su.reddot.domain.dao.logistic.WaybillRepository;
import su.reddot.domain.dao.order.OrderPositionRepository;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.logistic.DestinationType;
import su.reddot.domain.model.logistic.Waybill;
import su.reddot.domain.model.logistic.WaybillOrder;
import su.reddot.domain.model.logistic.WaybillRequisite;
import su.reddot.domain.model.logistic.event.SaleConfirmedEvent;
import su.reddot.domain.model.order.OrderPosition;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Vitaliy Khludeev on 25.08.17.
 */
@Slf4j
@Component
public class DefaultLogisticService extends WebServiceGatewaySupport implements LogisticService {

	private final ObjectFactory objectFactory;

	private final WaybillRepository waybillRepository;

	private final WaybillOrderRepository waybillOrderRepository;

	private final OrderPositionRepository orderPositionRepository;

	@Setter
	@Value("${logistic.login}")
	private String login;

	@Setter
	@Value("${logistic.password}")
	private String password;

	@Setter
	@Value("${logistic.office.address}")
	private String officeAddress;

	@Setter
	@Value("${logistic.office.zip-code}")
	private String officeZipCode;

	@Setter
	@Value("${logistic.office.phone}")
	private String officePhone;

	@Setter
	@Value("${logistic.office.name}")
	private String officeName;

	@Setter
	@Value("${logistic.endpoint}")
	private String endpoint;

    @Autowired
    public DefaultLogisticService(
			WaybillRepository waybillRepository,
			Jaxb2Marshaller marshaller,
			WaybillOrderRepository waybillOrderRepository,
			OrderPositionRepository orderPositionRepository
	) {

        this.waybillRepository = waybillRepository;
		this.waybillOrderRepository = waybillOrderRepository;
		this.orderPositionRepository = orderPositionRepository;
        this.setMarshaller(marshaller);
        this.setUnmarshaller(marshaller);
        this.objectFactory = new ObjectFactory();
    }

    @PostConstruct
    public void initEndpoint() {
		this.setDefaultUri(endpoint);
	}

	@Override
	@Async @EventListener
	@Transactional
	public void createWaybill(SaleConfirmedEvent e) {

		OrderPosition orderPosition = orderPositionRepository.findOne(e.getOrderPositionId());

		WaybillRequisite pickupRequisite = of(orderPosition, e.getPickupDestinationType());
		WaybillRequisite deliveryRequisite = of(orderPosition, e.getDeliveryDestinationType());

		WaybillOrder waybillOrder = createWaybillOrder(orderPosition, pickupRequisite, deliveryRequisite);

		SaveWaybillOffice request = new SaveWaybillOffice();

		request.setLogin(login);
		request.setPassword(password);

		Cargo cargo = new Cargo();
		cargo.setCargoPackageQty(1);
		cargo.setWeight(objectFactory.createCargoWeight(1f));
		//cargo.setCOD(objectFactory.createCargoCOD(orderPosition.getAmount().round(new MathContext(2, RoundingMode.UP)).floatValue()));

		Order order = new Order();
		order.setSender(objectFactory.createOrderSender(getDestinationInformation(pickupRequisite, cargo)));
		order.getRecipient().add(getDestinationInformation(deliveryRequisite, cargo));
		order.setTypeOfPayer(objectFactory.createOrderTypeOfPayer("0")); // заказчик
		order.setWayOfPayment(objectFactory.createOrderWayOfPayment("1")); // безнал
		order.setTypeOfCargo(objectFactory.createOrderTypeOfCargo("4aab1fc6-fc2b-473a-8728-58bcd4ff79ba")); // Груз
		order.setParentOrderForWaybill(objectFactory.createOrderParentOrderForWaybill(waybillOrder.getExternalSystemId()));
		order.setTypeOfParentForWaybill(objectFactory.createOrderTypeOfParentForWaybill("order"));

		request.setOrderData(order);
		request.setNumber("");

		UUID waybillUuid = UUID.randomUUID();

		request.setClientNumber(waybillUuid.toString());
		request.setLanguage("");
		request.setCompany("");
		request.setOffice("");

		SaveWaybillOfficeResponse response = (SaveWaybillOfficeResponse) getWebServiceTemplate()
				.marshalSendAndReceive(request);

		if(response.getReturn().isError()) {
			throw new RuntimeException("OrderPositionId: " + orderPosition.getId() + ". "
					+ response.getReturn().getErrorInfo().getValue());
		}

		if(response.getReturn().getItems().get(0).isError()) {
			throw new RuntimeException("OrderPositionId: " + orderPosition.getId() + ". "
					+ response.getReturn().getItems().get(0).getErrorInfo().getValue());
		}

		Waybill waybill = new Waybill()
				.setUuid(waybillUuid)
				.setExternalSystemId(response.getReturn().getItems().get(0).getValue().getValue())
				.setPickupRequisite(pickupRequisite)
				.setDeliveryRequisite(deliveryRequisite)
				.setPickupDestinationType(e.getPickupDestinationType())
				.setDeliveryDestinationType(e.getDeliveryDestinationType())
				.setOrderPosition(orderPosition)
				.setWaybillOrder(waybillOrder);

		waybillRepository.save(waybill);
	}

	private WaybillOrder createWaybillOrder(OrderPosition orderPosition, WaybillRequisite pickupRequisite, WaybillRequisite deliveryRequisite) {
    	SaveDocuments saveDocuments = objectFactory.createSaveDocuments();
    	saveDocuments.setLogin(login);
    	saveDocuments.setPassword(password);
    	saveDocuments.setData(getData(orderPosition, pickupRequisite, deliveryRequisite));
    	saveDocuments.setParameters(getParameters());
		SaveDocumentsResponse response = (SaveDocumentsResponse) getWebServiceTemplate()
				.marshalSendAndReceive(saveDocuments);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(response);
			log.error(json);
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		List<Element> properties = response.getReturn().getList().get(0).getProperties();
		Element numberElement = properties.stream().filter(p -> p.getKey().equals("Number")).findFirst().get();
		Element clientNumberElement = properties.stream().filter(p -> p.getKey().equals("ClientNumber")).findFirst().get();
		WaybillOrder waybillOrder = new WaybillOrder()
				.setCreateTime(ZonedDateTime.now())
				.setExternalSystemId(numberElement.getValue().getValue().toString())
				.setUuid(UUID.fromString(clientNumberElement.getValue().getValue().toString()));
		waybillOrderRepository.save(waybillOrder);
		return waybillOrder;
	}

	private Element getData(OrderPosition orderPosition, WaybillRequisite pickupRequisite, WaybillRequisite deliveryRequisite) {
		Element element = objectFactory.createElement();
		element.setKey("Orders");
		element.getList().add(getOrderElement(orderPosition, pickupRequisite, deliveryRequisite));
		return element;
	}

	private Element getOrderElement(OrderPosition orderPosition, WaybillRequisite pickupRequisite, WaybillRequisite deliveryRequisite) {
		Element element = objectFactory.createElement();
		element.setKey("Order");

		addPropertiesToElement(element, "ClientNumber", UUID.randomUUID().toString());

		addFieldToElement(element, "Sender", pickupRequisite.getName());
		addFieldToElement(element, "Department", "");
		addFieldToElement(element, "Project", "");
		addFieldToElement(element, "SenderGeography", "postcode-" + pickupRequisite.getZipCode());
		addFieldToElement(element, "SenderAddress", pickupRequisite.getAddress());
		addFieldToElement(element, "SenderPhone", pickupRequisite.getPhone());
		addFieldToElement(element, "Recipient", deliveryRequisite.getName());
		addFieldToElement(element, "RecipientGeography", "postcode-" + deliveryRequisite.getZipCode());
		addFieldToElement(element, "RecipientAddress", deliveryRequisite.getAddress());
		addFieldToElement(element, "RecipientPhone", deliveryRequisite.getPhone());
		addFieldToElement(element, "Urgency", "18c4f207-458b-11dc-9497-0015170f8c09"); // срочная
		addFloatFieldToElement(element, "Payer", BigDecimal.valueOf(0)); // заказчик
		addFloatFieldToElement(element, "PaymentMethod", BigDecimal.valueOf(1)); // безнал
		addFieldToElement(element, "ShippingMethod", "e45b6d73-fd62-44da-82a6-44eb4d1d9490"); // курьер
		addFieldToElement(element, "TypeOfCargo", "4aab1fc6-fc2b-473a-8728-58bcd4ff79ba");
		addFloatFieldToElement(element, "Weight", BigDecimal.valueOf(1));
		addIntFieldToElement(element, "CargoPackageQty", BigDecimal.valueOf(1)); // груз
		return element;
	}

	private void addFieldToElement(Element element, String key, String value) {
		Element senderGeography = createStringElement(key, value);
		element.getFields().add(senderGeography);
	}

	private void addPropertiesToElement(Element element, String key, String value) {
		Element senderGeography = createStringElement(key, value);
		element.getProperties().add(senderGeography);
	}

	private Element createStringElement(String key, String value) {
		Element senderGeography = objectFactory.createElement();
		senderGeography.setKey(key);
		senderGeography.setValueType(objectFactory.createElementValueType("string"));
		senderGeography.setValue(objectFactory.createElementValue(value));
		return senderGeography;
	}

	private void addFloatFieldToElement(Element element, String key, BigDecimal value) {
		Element senderGeography = objectFactory.createElement();
		senderGeography.setKey(key);
		senderGeography.setValueType(objectFactory.createElementValueType("float"));
		senderGeography.setValue(new JAXBElement(new QName("http://www.cargo3.ru", "Value"), BigDecimal.class, Element.class, value));
		element.getFields().add(senderGeography);
	}

	private void addIntFieldToElement(Element element, String key, BigDecimal value) {
		Element senderGeography = objectFactory.createElement();
		senderGeography.setKey(key);
		senderGeography.setValueType(objectFactory.createElementValueType("int"));
		senderGeography.setValue(new JAXBElement(new QName("http://www.cargo3.ru", "Value"), BigDecimal.class, Element.class, value));
		element.getFields().add(senderGeography);
	}

	private Element getParameters() {
		Element element = objectFactory.createElement();
		element.setKey("Parameters");
		element.getList().add(createStringElement("DocumentType", "order"));
		return element;
	}

	private WaybillRequisite getBuyerWaybillRequisite(su.reddot.domain.model.order.Order order) {
		DeliveryRequisite deliveryRequisite = order.getDeliveryRequisite();
		return new WaybillRequisite()
				.setName(deliveryRequisite.getDeliveryName())
				.setAddress(deliveryRequisite.getDeliveryCity() + ", " + deliveryRequisite.getDeliveryAddress())
				.setPhone(deliveryRequisite.getDeliveryPhone())
				.setZipCode(deliveryRequisite.getDeliveryZipCode());
	}

	private WaybillRequisite getSellerWaybillRequisite(OrderPosition orderPosition) {
		SellerRequisite pickupRequisite = orderPosition.getPickupRequisite();
		return new WaybillRequisite()
				.setName(pickupRequisite.getFullName())
				.setAddress(pickupRequisite.getCity() + ", " + pickupRequisite.getAddress())
				.setPhone(pickupRequisite.getPhone())
				.setZipCode(pickupRequisite.getZipCode());
	}

	private WaybillRequisite getOfficeWaybillRequisite() {
		return new WaybillRequisite()
				.setName(officeName)
				.setAddress(officeAddress)
				.setPhone(officePhone)
				.setZipCode(officeZipCode);
	}

	private DestinationInformation getDestinationInformation(WaybillRequisite requisite, Cargo cargo) {
		DestinationInformation destinationInformation = new DestinationInformation();
		destinationInformation.setClient(objectFactory.createDestinationInformationClient(requisite.getName()));
		DestinationAddress senderAddress = new DestinationAddress();
		senderAddress.setGeography(objectFactory.createDestinationAddressGeography("postcode-" + requisite.getZipCode()));
		senderAddress.setFreeForm(true);
		senderAddress.setInfo(objectFactory.createDestinationAddressInfo(requisite.getAddress()));
		destinationInformation.setAddress(objectFactory.createDestinationInformationAddress(senderAddress));
		destinationInformation.setPhone(objectFactory.createDestinationInformationPhone(requisite.getPhone()));
		destinationInformation.setCargo(objectFactory.createDestinationInformationCargo(cargo));
		destinationInformation.setUrgency(objectFactory.createDestinationInformationUrgency("18c4f207-458b-11dc-9497-0015170f8c09")); // срочная
		return destinationInformation;
	}

	private WaybillRequisite of(OrderPosition orderPosition, DestinationType destinationType) {

    	WaybillRequisite requisite;
		switch (destinationType) {
			case SELLER:
				requisite = getSellerWaybillRequisite(orderPosition);
				break;

			case BUYER:
				requisite = getBuyerWaybillRequisite(orderPosition.getOrder());
				break;

			case OFFICE:
				requisite = getOfficeWaybillRequisite();
				break;

			default: requisite = null;
		}

		return requisite;
	}
}
