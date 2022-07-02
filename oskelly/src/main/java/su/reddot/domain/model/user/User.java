package su.reddot.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.dao.LocalDateTimeConverter;
import su.reddot.domain.dao.ZonedDateTimeConverter;
import su.reddot.domain.dao.admin.UserProductsAndOrdersProjection;
import su.reddot.domain.model.Authority;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.SellerRequisite;
import su.reddot.domain.model.enums.AuthorityName;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter @Setter @Accessors(chain = true)
@Table(name = "\"user\"")
@SqlResultSetMapping(
		name = "userProductsAndOrdersProjection", classes = {@ConstructorResult(
		targetClass = UserProductsAndOrdersProjection.class,
		columns = {
				@ColumnResult(name = "userid"), @ColumnResult(name = "nickname"), @ColumnResult(name = "email"),
				@ColumnResult(name = "firstname"), @ColumnResult(name = "lastname"), @ColumnResult(name = "phone"),
				@ColumnResult(name = "orderscount"), @ColumnResult(name = "orderitems"),
				@ColumnResult(name = "products"), @ColumnResult(name = "productstate"),
				@ColumnResult(name = "isPro"), @ColumnResult(name = "isVip"), @ColumnResult(name = "isTrusted")
		}
)}
)
@NamedNativeQuery(name = "User.getUserStats",
		query = "select * from get_user_statistic(:isPro, :isSeller, :isNew, :_offset, :_limit) us ",
		resultSetMapping = "userProductsAndOrdersProjection")
public class User {

	public static String noImageUrl = "/images/no-photo.jpg";

	@AllArgsConstructor @Getter
	public enum Sex {
		MALE("Мужской"), FEMALE("Женский");

		private String displayName;
	}

	@AllArgsConstructor @Getter
	public enum UserType {

		/**
		 * Физик
		 */
		SIMPLE_USER("Физ.Лицо"),

		/**
		 * Индивидуальный предприниматель
		 */
		IP("ИП"),

		/**
		 * ООО
		 */
		OOO("ООО");

		private String description;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	private String facebookId;

	private String nickname;

	private String hashedPassword;

	@Embedded
    @AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "password_reset_token")),
			@AttributeOverride(name = "createdAt", column = @Column(name = "password_reset_token_created_at")),
			@AttributeOverride(name = "usedAt", column = @Column(name = "password_reset_token_used_at")),
	})
	private PasswordResetToken passwordResetToken;

	@Convert(converter = ZonedDateTimeConverter.class)
	private ZonedDateTime registrationTime;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime activationTime;

	private String activationToken;

	private String apiHashedPassword;

	@Enumerated(EnumType.STRING)
	private Sex sex;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime birthDate;

	//TODO: лишнее поле
	private String name;

	/**
	 * TODO: Переделать на trustedStatusTime
	 * TODO: если кто-то будет переделывать, не забудьте, пожалуйста, переделать его заодно в хранимке, иначе пизда
	 */
	private Boolean isTrusted;

	private String paymentDetails;

	/**
	 * Время установки PRO статуса
	 */
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime proStatusTime;

	/**
	 * Время установки VIP статуса
	 */
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime vipStatusTime;

	private String avatarPath;

	/**
	 * Адрес, по которому курьер дотавляет пользователю купленную им вещь
	 */
	@Embedded
	private DeliveryRequisite deliveryRequisite;

	/**
	 * Адрес, по которому курьер забирает у  пользователю проданную им вещь
	 */
	@Embedded
	private SellerRequisite sellerRequisite;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<UserAuthorityBinding> userAuthorityBindings = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private UserType userType;

	/*
		Эти поля не помещены в Embedded потому, что они пересекаются (например, инн может быть и у физика, и у ИП)
	 */

	/**
	 * Паспорт (физик)
	 */
	private String passport;

	/**
	 * Индивидуальный номер налогоплательщика (физик, ИП)
	 */
	private String INN;

	/**
	 * Основной государственный регистрационный номер индивидульного предпринимателя (ИП)
	 */
	private String OGRNIP;

	/**
	 * Основной государственный регистрационный номер (ООО)
	 */
	private String OGRN;

	/**
	 * Код причины постановки (ООО)
	 */
	private String KPP;

	@Embedded
	private PaymentRequisite paymentRequisite;

	public boolean isPro() {
		return proStatusTime != null;
	}

	public boolean isVip() {
	   //noinspection ConstantConditions nullable
		return vipStatusTime != null;
	}

	@SuppressWarnings("ConstantConditions")
	public Optional<String> getFullName() {

		if (sellerRequisite == null) { return Optional.empty(); }

		boolean firstNameIsNull = sellerRequisite.getFirstName() == null;
		boolean lastNameIsNull  = sellerRequisite.getLastName() == null;

		if (firstNameIsNull && lastNameIsNull) { return Optional.empty(); }

		if (!firstNameIsNull && !lastNameIsNull) {
			return Optional.of(sellerRequisite.getFirstName() + " " + sellerRequisite.getLastName());
		}

		if (firstNameIsNull) {
			return Optional.of(sellerRequisite.getLastName());
		}
		else {
			return Optional.of(sellerRequisite.getFirstName());
		}
	}

	public boolean isModerator() {
		return userAuthorityBindings.stream()
				.filter(binding -> binding.getAuthority().getType() == Authority.AuthorityType.MODERATOR)
				.count() > 0;
	}

	public boolean canViewAllProducts(){
		return userAuthorityBindings.stream()
				.anyMatch(binding -> binding.getAuthority().getName() == AuthorityName.CAN_VIEW_ALL_PRODUCTS);

	}
}
