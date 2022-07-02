package su.reddot.domain.service.staticPage;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class StaticPageRequest {

	private Long id;

	@NotNull(message = "Не указан контент страницы")
	@Size(min = 1, message = "Не указан контент страницы")
	private String content;

	@NotNull(message = "Неправильно указан URL страницы")
	@Size.List({
			@Size(min = 1, message = "Неправильно указан URL страницы"),
			@Size(max = 50, message = "Слишком длинный URL страницы")
	})
	@Pattern(regexp = "[a-zA-Z0-9-_+/]+", message = "URL должен содержать только латинские символы, -, _, + и /")
	private String url;

	@NotNull(message = "Неправильно указано название страницы")
	@Size.List({
			@Size(min = 1, message = "Неправильно указано название страницы"),
			@Size(max = 64, message = "Слишком длинное название страницы"),
	})
	private String name;

	private String metaDescription;
	private String metaKeywords;
	private MultipartFile image;
	private Long tagId;
}
