package su.reddot.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.model.user.PasswordResetToken;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.security.token.EmailAuthenticationToken;
import su.reddot.infrastructure.sender.NotificationSender;
import su.reddot.infrastructure.util.ErrorNotification;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

	private final UserRepository userRepository;

	private final AuthenticationManager authenticationManager;

	private final TemplateEngine               templateEngine;
	private final BCryptPasswordEncoder        passwordEncoder;
	private final MessageDigestPasswordEncoder apiPasswordEncoder;

	private final NotificationSender sender;

	@Value("${app.host}")
	private String host;

	@Override
	@Transactional
	public Authentication loginByEmail(String email, String password, ErrorNotification errorNotification) {

		String normalizedEmail  = email.trim().toLowerCase();

		if (normalizedEmail.trim().isEmpty()) {
			errorNotification.add("email", "Не указан email");
			return null;
		}
		if (password == null || password.trim().isEmpty()) {
			errorNotification.add("password", "Не указан пароль");
		}

		EmailAuthenticationToken token = new EmailAuthenticationToken(normalizedEmail, password, null);
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(token);
		} catch (UsernameNotFoundException | BadCredentialsException e) {
			errorNotification.add("password", "Неправильный email или пароль");
			return null;
		}

		if (authentication.isAuthenticated()) {
			SecurityContextHolder.getContext().setAuthentication(token);
		} else {
			errorNotification.add("Ошибка", "Не удалось авторизовать пользователя в системе");
		}
		return authentication;
	}

	@Override
	public void sendAuthorizationConfirmation(User user) {
		if (user.getActivationTime() != null) {
			return;
		}

		String link = host + "/registrationConfirm?user=" + user.getId() + "&token=";
		String token = UUID.randomUUID().toString();

		user.setActivationToken(token);
		userRepository.save(user);

		Context context = new Context();
		context.setVariable("resource", link + token);
		String text = templateEngine.process("mail/registration-confirmation", context);

		sender.sendViaRegistrationBox(user.getEmail(), "Подтверждение регистрации на сайте Oskelly", text);
	}

	@Override @Transactional
	public void activateUserByActivationToken(Long userId, String token) throws SecurityException {
		User user = userRepository.findOne(userId);
		if (user == null) {
			log.error("Пользователь не найден. Id: " + userId);
			throw new SecurityException("Пользователь не найден. Id: " + userId);
		}

		/* Пользователь, который ранее уже подтвердил регистрацию,
		* пытается перейти по ссылке подтверждения регистрации повторно. */
		if (user.getActivationTime() != null) { return; }

		if (user.getActivationToken().equals(token)) {
			user.setActivationTime(LocalDateTime.now());
			user.setActivationToken(null);
		} else {
			log.error("Неверный токен подтверждения авторизации. Пользователь: {}, токен: {}",
					user.getId(), token);

			throw new SecurityException("Неверный токен подтверждения авторизации");
		}
	}

	@Override
	@Transactional
	public void requestForPasswordReset(PasswordResetRequest req) {

		String email = req.getEmail();
		User  userToResetPasswordFor = userRepository.findByEmail(email);

		/* Если запросили сброс пароля для несуществующего пользователя, не расценивать эту ситуацию как ошибочную
		* и не возвращать результат вида "пользователь с такой почтой не существует":
		* это выдает дополнительную информацию о наличии или отсутствии пользователя с указанной почтой. */
		if (userToResetPasswordFor == null) { return; }

		/* У пользователя уже есть неистекший запрос на восстановление пароля */
		if (userToResetPasswordFor.getPasswordResetToken() != null
				&& !userToResetPasswordFor.getPasswordResetToken().isExpired()) {
			return;
		}

		String newlyCreatedResetToken = UUID.randomUUID().toString();
		userToResetPasswordFor.setPasswordResetToken(
				new PasswordResetToken(newlyCreatedResetToken)
		);

		Context context = new Context();
		context.setVariable("resource", String.format("%s/?t=%s", host, newlyCreatedResetToken));
		context.setVariable("name", userToResetPasswordFor.getNickname());

		String content = templateEngine.process("mail/reset-password-request", context);

		sender.sendViaRegistrationBox(email, "Восстановление пароля", content);
	}

	@Override
    @Transactional
	public void updatePassword(NewPasswordRequest req) {

		String newPassword = req.getPassword();

		//noinspection ConstantConditions метод получает уже проверенные данные
		User userToResetPasswordFor = userRepository.findByPasswordResetTokenValue(req.getResetToken()).get();
		String userEmail = userToResetPasswordFor.getEmail();

		userToResetPasswordFor
				.setHashedPassword(passwordEncoder.encode(newPassword))
				.setApiHashedPassword(
						apiPasswordEncoder.encodePassword(
								apiPasswordEncoder.encodePassword(newPassword, userEmail),
								userEmail
						)
				);

		userToResetPasswordFor.getPasswordResetToken()
				.setUsedAt(ZonedDateTime.now());
	}

	@Override
	public void assertTokenValidity(String possiblyInvalidToken) {
	    if (possiblyInvalidToken == null) { throw new IllegalArgumentException("Ссылка недействительна"); }

		User userToResetPasswordFor = userRepository.findByPasswordResetTokenValue(possiblyInvalidToken).
				orElseThrow(() -> new IllegalArgumentException("Ссылка недействительна"));

	    if (userToResetPasswordFor.getPasswordResetToken()
				.isExpired()) {
	    	throw new IllegalArgumentException("Срок действия ссылки истек");
		}
	}
}
