package su.reddot.domain.model.size;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SizeType {
	RUSSIAN("RUS", "Российский"),
	EUROPEAN("EU", "Европейский"),
	AMERICAN("US", "Американский"),
	INTERNATIONAL("INT", "Международный"),
	ENGLISH("UK", "Английский"),
	FRENCH("FR", "Французский"),
	ITALIAN("IT", "Итальянский"),
	DANISH("DEN", "Немецкий"),
	INCHES("INCH", "Дюймы"),
	CENTIMETERS("CM", "Сантиметры"),

	WAIST_CENTIMETERS("Талия, см", "Ремни (см)"),
	WAIST_INCHES("Талия, дюймы", "Обват талии в дюймах"),

	COLLAR_CENTIMETERS("Ворот, сантиметры", "Ворот в сантиметрах"),
	COLLAR_INCHES("Ворот, дюймы", "Ворот в дюймах"),

	RING_RUSSIAN("RUS (диаметр в мм)", "Российский размер колец (диаметр в миллиметрах)"),
	RING_EUROPEAN("EU (длина окружности в мм)", "Европейский размер колец (окружность в миллиметрах)"),

	WAIST_ENGLISH_AMERICAN("UK/US талия", "Обхват талии (английский / американский)"),
	BREAST_ENGLISH_AMERICAN("UK/US грудь", "Обхват груди (английский / американский)"),

	JEANS("JEANS", "Размер джинс"),

	HEIGHT("Рост", "Рост"),
	AGE("Возраст", "Возраст"),
	NO_SIZE("Без размера", "Без размера"),

	AUSTRALIAN("Австралийский", "Австралийский"),
	ROMAN_NUMERALS("Римские цифры", "Римские цифры");

	@Getter
	private String abbreviation;

	@Getter
	private String description;
}
