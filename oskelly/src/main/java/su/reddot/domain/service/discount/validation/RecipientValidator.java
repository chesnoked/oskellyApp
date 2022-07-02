package su.reddot.domain.service.discount.validation;

import su.reddot.domain.model.discount.GiftCard;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RecipientValidator implements ConstraintValidator<Recipient, GiftCard.Recipient> {

    @Override
    public void initialize(Recipient constraintAnnotation) {}

    @Override
    public boolean isValid(GiftCard.Recipient value, ConstraintValidatorContext context) {
        if (value == null) { return false; }

        String name          = value.getName();
        String email         = value.getEmail();
        String address       = value.getAddress();

        if (name == null && email == null && address == null) { return false; }

        boolean passedValidation = true;
        if (isBlank(name)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Имя получателя сертификата не может быть пустым")
                    .addPropertyNode("name")
                    .addConstraintViolation();

            passedValidation = false;
        }

        if (address == null && email == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Не выбран метод доставки сертификата")
                    .addConstraintViolation();

            passedValidation = false;
        }
        else if (address != null && email != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("В качестве метода доставки можно выбрать " +
                    "либо отправку электронной копии сертификата по электронной почте, " +
                    "либо доставку копии курьером, но не обе одновременно")
                    .addConstraintViolation();

            passedValidation = false;
        }
        else if (email != null && isBlank(email)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Адрес электронной почты не может быть пустым")
                    .addPropertyNode("email")
                    .addConstraintViolation();

            passedValidation = false;
        }
        else if (address != null && isBlank(address)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Адрес доставки не может быть пустым")
                    .addPropertyNode("address")
                    .addConstraintViolation();

            passedValidation = false;

        }

        return passedValidation;
    }

    private boolean isBlank(String nullableString) {
        return nullableString == null || nullableString.trim().length() == 0;

    }
}
