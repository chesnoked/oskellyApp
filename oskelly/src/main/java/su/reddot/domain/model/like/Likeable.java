package su.reddot.domain.model.like;

/**
 * @author Vitaliy Khludeev on 14.09.17.
 */
public interface Likeable {

	Class<? extends Like> getLikeClass();

	String getImagePreview();

	String getUrl();

	String getEntityName();
}
