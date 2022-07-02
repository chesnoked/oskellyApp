package su.reddot.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Embeddable;

/**
 * Данные о доставке должны быть либо заполнены полностью,
 * либо не заполнены вообще.
 * Состояние, когда некоторые поля проинициализированы,
 * а некоторые - нет, является недопустимым.
 */
@Embeddable
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryRequisite {

    private String deliveryName;

    private String deliveryPhone;

    private String deliveryCountry;

    private String deliveryCity;

    private String deliveryAddress;
    /** Детальные данные адреса от дадаты */
    private String deliveryExtensiveAddress;

    private String deliveryZipCode;

    public String humanReadable() {
        return (deliveryZipCode == null? "" : deliveryZipCode)
                + (deliveryCountry == null? "" : " " + deliveryCountry)
                + (deliveryCity == null? "" : " " + deliveryCity)
                + (deliveryAddress == null? "" : " " + deliveryAddress)
                + (deliveryName == null? "" : " " + deliveryName)
                + (deliveryPhone == null? "" : " " + deliveryPhone);
    }

    public boolean complete() {
        return deliveryName != null
                && deliveryPhone != null
                && deliveryCountry != null
                && deliveryCity != null
                && deliveryAddress != null
                && deliveryZipCode != null;
    }
}