package su.reddot.domain.service.discount.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AmountValidator.class)
public @interface Amount {

    String message() default "Не задан номинал сертификата";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
