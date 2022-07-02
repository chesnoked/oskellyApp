package su.reddot.presentation.mapper.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import su.reddot.domain.model.staticPage.StaticPage;
import su.reddot.domain.model.user.User;
import su.reddot.presentation.view.admin.ModifierView;
import su.reddot.presentation.view.admin.StaticPagePreview;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public final class StaticPageMapper {
	private static final int PREVIEW_TEXT_LENGTH = 300;
	private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private String imageUrlPrefix;

	public StaticPageMapper(@Value("${resources.images.urlPrefix}") String imageUrlPrefix) {
		this.imageUrlPrefix = imageUrlPrefix;
	}

	public StaticPagePreview mapStaticPageToPreview(StaticPage staticPage) {
		StaticPagePreview preview = new StaticPagePreview();
		preview.setId(staticPage.getId())
				.setName(staticPage.getName());

		String url = staticPage.getPageGroup().getUrl() + staticPage.getUrl();
		preview.setUrl(url);

		String content = staticPage.getContent();
		String strippedText = content.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");

		//чистим пробелы
		strippedText = strippedText.replaceAll("\\s+", " ");

		if (strippedText.length() > PREVIEW_TEXT_LENGTH) {
			String previewText = strippedText.substring(0, PREVIEW_TEXT_LENGTH - 4) + "...";
			preview.setText(previewText);
		} else {
			preview.setText(strippedText);
		}

		preview.setModifiedBy(staticPage.getModifiedBy());

		if (staticPage.getModifiedAt() != null) {
			preview.setModifiedAt(getModifiedAt(staticPage));
			preview.setFormattedModifiedAt(getFormattedModifiedAt(staticPage));
		}


		if(staticPage.getTag() != null) {
			preview.setTag(staticPage.getTag().getName());
		}

		if(staticPage.getStatus() != null) {
			preview.setStatus(staticPage.getStatus().getDescription());
		}

		if(staticPage.getImagePath() != null) {
			preview.setImagePath(imageUrlPrefix + staticPage.getImagePath());
		}

		return preview;
	}

	private String getModifiedAt(StaticPage staticPage) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		DateTimeFormatter zoneOffsetFormatter = DateTimeFormatter.ofPattern("X");
		String formattedDateTime = staticPage.getModifiedAt().format(formatter);
		String formattedZoneOffset = staticPage.getModifiedAt().format(zoneOffsetFormatter);
		return String.format("%s (GMT %s)", formattedDateTime, formattedZoneOffset);
	}

	private String getFormattedModifiedAt(StaticPage staticPage) {
		return staticPage.getModifiedAt().format(DateTimeFormatter.ofPattern("d, MMMM, YYYY"));
	}

	public ModifierView getModifierParamsForStaticPage(StaticPage staticPage) {
		User modifiedBy = staticPage.getModifiedBy();
		ZonedDateTime modifiedAt = staticPage.getModifiedAt();

		if (modifiedAt == null || modifiedBy == null) {
			return null;
		}

		String modifiedAtString = modifiedAt.format(format) + modifiedAt.getOffset().toString();

		return new ModifierView()
				.setAt(modifiedAtString)
				.setBy(modifiedBy.getEmail());
	}
}
