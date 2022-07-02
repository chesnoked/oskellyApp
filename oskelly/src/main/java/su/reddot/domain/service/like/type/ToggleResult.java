package su.reddot.domain.service.like.type;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
public class ToggleResult {

    private boolean canBeLiked;

    private int actualLikesCount;
}
