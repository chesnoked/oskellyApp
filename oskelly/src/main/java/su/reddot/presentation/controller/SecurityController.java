package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.attractionOfTraffic.AttractionOfTrafficService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.SecurityException;
import su.reddot.infrastructure.security.SecurityService;
import su.reddot.infrastructure.security.view.EmailAuthorizationRequest;
import su.reddot.infrastructure.security.view.EmailRegistrationRequest;
import su.reddot.infrastructure.security.view.EmailValidationRequest;
import su.reddot.infrastructure.util.ErrorNotification;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.util.StringUtils.isEmpty;
import static su.reddot.presentation.Utils.mapErrors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SecurityController {

	private final UserService                  userService;
	private final SecurityService              securityService;
	private final AttractionOfTrafficService   attractionOfTrafficService;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;

	@GetMapping("/login")
	public String loginPage(@RequestParam(value = "from", required = false) String fromInBase64, Model model) {
		if (isEmpty(fromInBase64)) {
			fromInBase64 = "Lw=="; // base64 версия "/"
		}
		model.addAttribute("from", fromInBase64);

		return "login";
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(
			HttpServletRequest request,
			HttpServletResponse response,
			@Valid EmailRegistrationRequest registrationRequest,
		  	BindingResult bindingResult) throws IOException, ServletException {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(mapErrors(bindingResult));
		}
		ErrorNotification registrationNotification = new ErrorNotification();
		Optional<User> userOpt = userService.registerUserByEmail(registrationRequest, registrationNotification);
		if (registrationNotification.hasErrors() || !userOpt.isPresent()) {
			return ResponseEntity.badRequest().body(registrationNotification.getAll());
		}

		securityService.sendAuthorizationConfirmation(userOpt.get());
		if (request.getCookies() != null) {
			attractionOfTrafficService.track(userOpt.get(), Arrays.asList(request.getCookies()));
		}

		ErrorNotification loginNotification = new ErrorNotification();
		Authentication authentication = securityService.loginByEmail(
				registrationRequest.getEmail(),
				registrationRequest.getPassword(),
				loginNotification);

		if (loginNotification.hasErrors()) {
			return ResponseEntity.badRequest().body(loginNotification.getAll());
		}
		else {
			authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		}

		return ResponseEntity.ok("Зарегистрирован успешно");
	}

	@GetMapping("/checkEmail")
	public ResponseEntity<?> validateEmail(@Valid EmailValidationRequest request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(mapErrors(bindingResult));
		}

		ErrorNotification notification = new ErrorNotification();
		userService.checkEmail(request, notification);
		if (notification.hasErrors()) {
			return ResponseEntity.badRequest().body(notification.getAll());
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(
			HttpServletRequest request,
			HttpServletResponse response,
			@Valid EmailAuthorizationRequest emailAuthorizationRequest,
			BindingResult bindingResult) throws IOException, ServletException {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(mapErrors(bindingResult));
		}
		ErrorNotification loginNotification = new ErrorNotification();
		Authentication authentication = securityService.loginByEmail(
				emailAuthorizationRequest.getEmail(),
				emailAuthorizationRequest.getPassword(),
				loginNotification);

		if (loginNotification.hasErrors()) {
			return ResponseEntity.badRequest().body(loginNotification.getAll());
		}
		else {
			authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		}
		return ResponseEntity.ok().build();

	}

	@GetMapping("/registrationConfirm")
	public String activateUser(@RequestParam("user") Long userId,
	                           @RequestParam("token") String token) {
		try {
			securityService.activateUserByActivationToken(userId, token);
		} catch (SecurityException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return "redirect:/";
	}
}
