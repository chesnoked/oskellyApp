package su.reddot.domain.model.staticPage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static su.reddot.infrastructure.util.Utils.normalizeUrl;

/**
 * Подмножество статических страниц. Например, страницы блога
 */
@Getter
@Slf4j
public enum StaticPageGroup {

	INFO("/info", "Информационная страница"), BLOG("/blog", "Страница блога");

	StaticPageGroup(String url, String displayName) {
		this.url = url;
		this.displayName = displayName;
	}

	private String url;
	private String displayName;

	private static final Map<String, StaticPageGroup> urlMap;

	static {
		urlMap = new HashMap<>();
		for (StaticPageGroup pageGroup : StaticPageGroup.values()) {
			urlMap.put(pageGroup.getUrl(), pageGroup);
		}
	}

	public static Optional<StaticPageGroup> getGroupByUrl(String url) {
		String normalizedUrl = normalizeUrl(url);
		StaticPageGroup group = urlMap.get(normalizedUrl);
		return Optional.ofNullable(group);
	}

	public static Optional<StaticPageGroup> getGroupByName(String name) {
		try {
			StaticPageGroup staticPageGroup = StaticPageGroup.valueOf(name);
			return Optional.of(staticPageGroup);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return Optional.empty();
		}
	}
}
