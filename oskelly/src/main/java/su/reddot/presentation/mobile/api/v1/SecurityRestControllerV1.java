package su.reddot.presentation.mobile.api.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.device.DeviceService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.SecurityService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.infrastructure.security.view.EmailRegistrationRequest;
import su.reddot.infrastructure.util.ErrorNotification;

import javax.validation.Valid;
import java.util.Optional;

import static su.reddot.presentation.Utils.mapErrors;

/**
 * @author Vitaliy Khludeev on 20.04.17.
 */
@RestController
@RequestMapping(value = "/mobile/api/v1/users")
@Slf4j
public class SecurityRestControllerV1 {

	private final UserService userService;
	private final SecurityService securityService;
	private final DeviceService deviceService;

	@Autowired
	public SecurityRestControllerV1(UserService userService, SecurityService securityService, DeviceService deviceService) {
		this.userService = userService;
		this.securityService = securityService;
		this.deviceService = deviceService;
	}

	@PostMapping("/registration")
	/*
	 * TODO: Здесь должна быть капча
	 */
	public ResponseEntity<?> register(@Valid EmailRegistrationRequest registrationRequest,
									  BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(mapErrors(bindingResult));
		}
		ErrorNotification registrationNotification = new ErrorNotification();
		Optional<User> userOpt = userService.registerUserByEmail(registrationRequest, registrationNotification);
		if (registrationNotification.hasErrors() || !userOpt.isPresent()) {
			return ResponseEntity.badRequest().body(registrationNotification.getAll());
		}

		securityService.sendAuthorizationConfirmation(userOpt.get());

		ErrorNotification loginNotification = new ErrorNotification();
		securityService.loginByEmail(
				registrationRequest.getEmail(),
				registrationRequest.getPassword(),
				loginNotification);

		if (loginNotification.hasErrors()) {
			return ResponseEntity.badRequest().body(loginNotification.getAll());
		}
		return ResponseEntity.ok().build();
	}

	/**
	 * Пустой метод, т.к. вся логика аутентификации находится в
	 * {@link su.reddot.infrastructure.security.RestAuthenticationFilter}
	 * @return OK
	 */
	@PostMapping("/authentication")
	public ResponseEntity<?> authenticate() {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/device/apple")
	public ResponseEntity<Void> saveAppleDevice(@RequestParam String token, UserIdAuthenticationToken t) {
		deviceService.saveAppleDevice(token, userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new));
		return ResponseEntity.ok().build();
	}

	@PostMapping("/device/android")
	public ResponseEntity<Void> saveAndroidDevice(@RequestParam String token, UserIdAuthenticationToken t) {
		deviceService.saveAndroidDevice(token, userService.getUserById(t.getUserId()).orElseThrow(IllegalArgumentException::new));
		return ResponseEntity.ok().build();
	}
}
