package su.reddot.domain.service.attractionOfTraffic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.AttractionOfTrafficRepository;
import su.reddot.domain.model.AttractionOfTraffic;
import su.reddot.domain.model.AttractionOfTraffic.Type;
import su.reddot.domain.model.user.User;
import su.reddot.infrastructure.acquirer.Payable;

import javax.servlet.http.Cookie;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultAttractionOfTrafficService implements AttractionOfTrafficService {

	private final RestTemplateBuilder restTemplateBuilder;

	private final AttractionOfTrafficRepository attractionOfTrafficRepository;

	/** Регистрация пользователя */
	@Async
	public void track(User u, List<Cookie> cookies) {

	    /* писать нормальный код некогда, alas */
	    /* отсылать ответ только advertise */
		Cookie advertiseCookie = cookies.stream()
				.filter(c -> c.getName().equals(Type.ADVERTISE.getCookieName()))
				.findFirst().orElse(null);
		if (advertiseCookie == null) { return; }

		/* срок действия куки истек? */

		/* под одной кукой можно зарегистрироваться только один раз */
		if (attractionOfTrafficRepository.existsByCookieAndType(advertiseCookie.getValue(), Type.ADVERTISE)) {
			return;
		}

		String endpoint = String.format("http://advertiseru.net/postback/452e13f74002426b/?token=ad3d908e0e986ec1f17969f450749fcc&uid=%s&client_id=%s",
				advertiseCookie.getValue(),
				u.getId());

		/* Так как сразу после регистрации выполняется автоматическая аутентификация пользователя, то
		* как раз она и сохранит куку в бд. После чего ее можно использовать для оплаты заказа. */
		ResponseEntity<String> advertiseResponse = restTemplateBuilder.build().getForEntity(endpoint, String.class);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(advertiseResponse);
			log.debug(json);
		} catch (JsonProcessingException ex) {
			log.error(ex.getLocalizedMessage(), ex);
		}
	}

	@Override
	@Async
	@EventListener
	public void sendAttractionOfTrafficResult(AttractionOfTrafficEvent e) {
		Payable payable = e.getPayable();
		User buyer = payable.getBuyer();
		if (attractionOfTrafficRepository.existsByUserAndUseTimeIsNotNull(buyer)) {
			return;
		}

		Optional<AttractionOfTraffic> attractionOfTrafficOpt = attractionOfTrafficRepository
				.findFirstByUserAndExpireTimeGreaterThanOrderByExpireTime(buyer, ZonedDateTime.now());

		if (!attractionOfTrafficOpt.isPresent()) {
			return;
		}

		AttractionOfTraffic attractionOfTraffic = attractionOfTrafficOpt.get();
		String endpointFormat = attractionOfTraffic.getType().getEndpointFormat();
		String endpoint = String.format(endpointFormat,
				attractionOfTraffic.getType().getAccountId(),
				attractionOfTraffic.getCookie(),
				payable.getOrderId(), payable.getPaymentAmount());

		ResponseEntity<String> responseEntity = restTemplateBuilder.build().getForEntity(endpoint, String.class);

		if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
			attractionOfTraffic.setUseTime(ZonedDateTime.now());
			attractionOfTraffic.setOrderId(Long.valueOf(payable.getOrderId()));
			attractionOfTrafficRepository.save(attractionOfTraffic);
		}
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(responseEntity);
			log.error(json);
		} catch (JsonProcessingException ex) {
			log.error(ex.getLocalizedMessage(), ex);
		}
	}

	@Override
	public void saveIfNeed(User user, List<Cookie> cookies) {

		List<Type> attractionTypes = Arrays.asList(Type.values());
		cookies.forEach( c ->
						attractionTypes.stream().filter(
							t -> c.getName().equals(t.getCookieName()) && !attractionOfTrafficRepository.existsByCookieAndType(c.getValue(), t)
						) /* только те трекеры, имя кук которых совпадает с этой кукой и у которых нет з*/
						.forEach(t -> {
			Optional<Cookie> expireCookie = cookies.stream().filter(cc -> cc.getName().equals(t.getCookieExpireTimeName())).findFirst();
			if(!expireCookie.isPresent()) {
				log.error(String.format("Expire cookie not present for type %s and value %s", t, c.getValue()));
				return;
			}
			AttractionOfTraffic attractionOfTraffic = new AttractionOfTraffic()
					.setCookie(c.getValue())
					.setType(t)
					.setUser(user)
					.setCreateTime(ZonedDateTime.now())
					.setExpireTime(ZonedDateTime.parse(expireCookie.get().getValue(), AttractionOfTrafficService.DATE_TIME_FORMATTER));
			attractionOfTrafficRepository.save(attractionOfTraffic);
		}));
	}
}
