package su.reddot.domain.model.size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import su.reddot.domain.model.category.Category;

import javax.persistence.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Setter @Getter
@JsonIgnoreProperties({"category"})
public class Size {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Category category;

	private String russian;

	private String european;

	private String american;

	private String international;

	private String english;

	private String french;

	private String italian;

	private String danish;

	private String inches;

	private String centimeters;

	private String collar_inches;

	private String collar_centimeters;

	private String waist_inches;

	private String waist_centimeters;

	private String ring_russian;

	private String ring_european;

	private String waist_english_american;

	private String breast_english_american;

	private String jeans;

	private String height;

	private String age;

	private String noSize;

	private String australian;

	private String roman_numerals;

	public String getBySizeType(SizeType sizeType) {
		switch (sizeType) {
			case INTERNATIONAL:
				return international;

			case RUSSIAN:
				return russian;

			case AMERICAN:
				return american;

			case EUROPEAN:
				return european;

			case ENGLISH:
				return english;

			case FRENCH:
				return french;

			case ITALIAN:
				return italian;

			case DANISH:
				return danish;

			case INCHES:
				return inches;

			case CENTIMETERS:
				return centimeters;

			case WAIST_INCHES:
				return waist_inches;

			case WAIST_CENTIMETERS:
				return waist_centimeters;

			case COLLAR_INCHES:
				return collar_inches;

			case COLLAR_CENTIMETERS:
				return collar_centimeters;

			case RING_RUSSIAN:
				return ring_russian;

			case RING_EUROPEAN:
				return ring_russian;

			case WAIST_ENGLISH_AMERICAN:
				return waist_english_american;

			case BREAST_ENGLISH_AMERICAN:
				return breast_english_american;

			case JEANS:
				return jeans;

			case HEIGHT:
				return height;

			case AGE:
				return age;

			case NO_SIZE:
				return noSize;

			case AUSTRALIAN:
				return australian;

			case ROMAN_NUMERALS:
				return roman_numerals;

			default:
				return null;
		}
	}

	/**
	 * @return таблица размеров вида
	 *
	 * | российский размер | международный размер | ... | обхват талии в см |
	 * |        52         |         XL           | ... |       100         |
     * ,
	 * в которой содержатся только те сетки, в которых размер определен
	 */
	public Map<SizeType, String> getChart() {

		return Stream.of(SizeType.values())
				.filter(sizeType -> getBySizeType(sizeType) != null)
				.collect(Collectors.toMap(Function.identity(), this::getBySizeType));
	}
}
