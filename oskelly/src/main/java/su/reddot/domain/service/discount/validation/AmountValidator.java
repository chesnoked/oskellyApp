package su.reddot.domain.service.discount.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class AmountValidator implements ConstraintValidator<Amount, String> {

    private static final List<String> possibleAmounts = Arrays.asList(
            "5000", "10000", "25000", "50000", "100000"
    );

    public void initialize(Amount constraint) {}

    public boolean isValid(String possiblyAmount, ConstraintValidatorContext context) {
        if (possiblyAmount == null) return false;

        if (!possibleAmounts.contains(possiblyAmount)) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("Неверный номинал сертификата")
                    .addConstraintViolation();

            return false;
        }

        try {
            new BigDecimal(possiblyAmount);
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Неверный номинал сертификата")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
