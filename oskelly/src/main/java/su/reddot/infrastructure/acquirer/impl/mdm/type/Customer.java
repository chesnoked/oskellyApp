package su.reddot.infrastructure.acquirer.impl.mdm.type;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Информация о клиенте */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter @Setter @ToString
class Customer {

    /** Уникальный идентификатор пользователя на сайте мерчанта */
    private String customerId;

    /** ФИО */
    private FullName fullName;

    /** код языка RU или EN */
    private String language;

    private Address address;
}

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter @Setter @ToString
class FullName {

    private String firstName;

    private String middleName;

    private String lastName;
}

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter @Setter @ToString
class Address {

    private String countryCode;

    private String city;

    private String addressLine;

    private String postalCode;
}
