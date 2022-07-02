package su.reddot.infrastructure.security.validation;

import lombok.RequiredArgsConstructor;
import su.reddot.infrastructure.security.SecurityService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static su.reddot.infrastructure.security.SecurityService.NewPasswordRequest;

@RequiredArgsConstructor
public class ValidNewPasswordValidator implements ConstraintValidator<ValidNewPassword, NewPasswordRequest> {

   private final SecurityService securityService;

   public void initialize(ValidNewPassword constraint) {
   }

   public boolean isValid(NewPasswordRequest newPasswordRequest,
                          ConstraintValidatorContext context) {

      String password = newPasswordRequest.getPassword();
      String passwordOnceMore = newPasswordRequest.getPasswordOnceMore();

      if (password != null && passwordOnceMore != null && !password.equals(passwordOnceMore)) {
         context.disableDefaultConstraintViolation();
         context.buildConstraintViolationWithTemplate("Пароли не совпадают")
                 .addConstraintViolation();

         return false;
      }

      /* Использовать валидатор, назначенный на поле токена. */
      if (newPasswordRequest.getResetToken() == null) { return true; }

      try {
         securityService.assertTokenValidity(newPasswordRequest.getResetToken());
      }
      catch (IllegalArgumentException e) {
         context.disableDefaultConstraintViolation();
         context.buildConstraintViolationWithTemplate(e.getMessage())
                 .addPropertyNode("resetToken")
                 .addConstraintViolation();

         return false;
      }

      return true;
   }
}
