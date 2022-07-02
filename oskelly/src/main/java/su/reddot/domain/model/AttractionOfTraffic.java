package su.reddot.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.model.user.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * @author Vitaliy Khludeev on 09.10.17.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class AttractionOfTraffic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private Type type;

	private String cookie;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime expireTime;

	@ManyToOne
	private User user;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime useTime;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime createTime;

	// FIXME: сделать привязку к Payable
	private Long orderId;

	public boolean isUsed() {
		return useTime != null;
	}

	@Getter
	public enum Type {

		ACTION_PAY("15607", "actionpay", "actionpay_expire", "actionpay", "https://x.actionpay.ru/ok/%1$s.png?actionpay=%2$s&apid=%3$s&price=%4$s", 30),
		ADVERTISE("ad3d908e0e986ec1f17969f450749fcc", "advertise", "advertise_expire", "uid", "http://advertiseru.net/postback/452e13f74102426b/?token=%1$s&uid=%2$s&order_id=%3$s&amount=%4$s", 30);

		private String accountId;
		private String cookieName;
		private String cookieExpireTimeName;
		private String requestParam;
		private String endpointFormat;
		private int daysBeforeExpire;

		Type(String accountId, String cookieName, String cookieExpireTimeName, String requestParam, String endpointFormat, int daysBeforeExpire) {
			this.accountId = accountId;
			this.cookieName = cookieName;
			this.cookieExpireTimeName = cookieExpireTimeName;
			this.requestParam = requestParam;
			this.endpointFormat = endpointFormat;
			this.daysBeforeExpire = daysBeforeExpire;
		}
	}
}