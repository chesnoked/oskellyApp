package su.reddot.presentation.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import su.reddot.infrastructure.security.SecurityService;
import su.reddot.infrastructure.security.SecurityService.NewPasswordRequest;
import su.reddot.infrastructure.security.SecurityService.PasswordResetRequest;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
public class Security {

    private final SecurityService securityService;

    @PostMapping("/password/reset-token")
    public ResponseEntity<?> getResetPasswordToken(@Validated @RequestBody PasswordResetRequest req) {

        securityService.requestForPasswordReset(req);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Validated @RequestBody NewPasswordRequest req) {

        securityService.updatePassword(req);

        return ResponseEntity.ok().build();
    }
}
