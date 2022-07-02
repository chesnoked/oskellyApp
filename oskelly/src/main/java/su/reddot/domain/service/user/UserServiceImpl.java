package su.reddot.domain.service.user;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.dao.UserRepository;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.security.view.EmailRegistrationRequest;
import su.reddot.infrastructure.security.view.EmailValidationRequest;
import su.reddot.infrastructure.util.ErrorNotification;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;

import static java.util.Optional.empty;
import static su.reddot.domain.model.user.QUser.user;
import static su.reddot.domain.model.user.User.UserType.SIMPLE_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final MessageDigestPasswordEncoder apiPasswordEncoder;

	@Override
	public Optional<User> registerUserByEmail(EmailRegistrationRequest request, ErrorNotification notification) {
		String email = request.getEmail().trim().toLowerCase();
		String nickname = request.getNickname();
		String password = request.getPassword();
		String confirmPassword = request.getConfirmPassword();
		String phone = request.getPhone();

		if (!password.equals(confirmPassword)) {
			notification.add("confirmPassword", "Пароли не совпадают");
			return empty();
		}

		User existingUserByEmail = userRepository.findByEmail(email);
		if (existingUserByEmail != null) {
			notification.add("email", "Пользователь уже зарегистрирован в системе");
			return empty();
		}

		User existingUserByNickname = userRepository.findByNickname(nickname);
		if (existingUserByNickname != null) {
			notification.add("nickname", "Псевдоним уже зарегистрирован в системе");
			return empty();
		}

		User user = new User();
		user.setEmail(email)
				.setNickname(nickname)
				.setHashedPassword(passwordEncoder.encode(password))
				.setRegistrationTime(ZonedDateTime.now())
				.setApiHashedPassword(
						apiPasswordEncoder.encodePassword(
								apiPasswordEncoder.encodePassword(password, email),
								email
						)
				)
				.setUserType(SIMPLE_USER);
		SellerRequisite sellerRequisite = user.getSellerRequisite();

		if (sellerRequisite != null) {
			sellerRequisite.setPhone(phone);
		} else {
			sellerRequisite = new SellerRequisite();
			sellerRequisite.setPhone(phone);
			user.setSellerRequisite(sellerRequisite);
		}

		User createdUser = userRepository.save(user);
		return Optional.of(createdUser);
	}

	@Override
	public void checkEmail(EmailValidationRequest request, ErrorNotification notification) {
		String email = request.getEmail().trim().toLowerCase();
		User existingUserByEmail = userRepository.findByEmail(email);
		if (existingUserByEmail != null) {
			notification.add("email", "Введеный email уже был зарегистрирован, пожалуйста, введите другой email или воспользуйтесь формой входа");
		}
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Optional<User> getUserById(Long id) {
		return Optional.ofNullable(userRepository.findOne(id));
	}

	@Override
	public Optional<User> getUserByNickname(String nickname) {
		return Optional.ofNullable(userRepository.findByNickname(nickname));
	}

	@Override
	@Transactional
	public void setDeliveryRequisite(Long userId, DeliveryRequisite r) {
		User u = userRepository.findOne(userId);
		if (u == null) {
			return;
		}

		DeliveryRequisite actualDeliveryRequisite = u.getDeliveryRequisite();
		if (actualDeliveryRequisite == null && !r.complete()) {
			return;
		}

		u.setDeliveryRequisite(r);
		userRepository.save(u);
	}

	@Override
	public void save(User u) {
		userRepository.save(u);
	}

    @Override
    public UserWithPassword registerSilently(String name, String nickname, String email) {

		if (Strings.isNullOrEmpty(email)) { throw new IllegalArgumentException("Адрес электронной почты не может быть пустым"); }
		if (Strings.isNullOrEmpty(nickname)) { throw new IllegalArgumentException("Псевдоним не может быть пустым"); }

		if (userRepository.exists(user.email.eq(email))) {
			throw new IllegalArgumentException(
					"Пользователь с таким адресом электронной почты уже существует");
		}

		if (userRepository.exists(user.nickname.eq(nickname))) {
			throw new IllegalArgumentException(
					"Пользователь с таким псевдонимом уже существует");
		}

		Random rand = new Random();
		String randomStringAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            char c = randomStringAlphabet.charAt(rand.nextInt(randomStringAlphabet.length()));
            sb.append(c);
        }

        String rawPassword = sb.toString();

		User user = new User();
		user.setEmail(email)
				.setNickname(nickname)
				.setHashedPassword(passwordEncoder.encode(rawPassword))
				.setRegistrationTime(ZonedDateTime.now())
				.setApiHashedPassword(
						apiPasswordEncoder.encodePassword(
								apiPasswordEncoder.encodePassword(rawPassword, email),
								email
						)
				)
				.setUserType(SIMPLE_USER)
				.setName(name);

		userRepository.save(user);

        return new UserWithPassword().setUser(user).setPassword(rawPassword);
    }

}
