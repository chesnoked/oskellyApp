package su.reddot.presentation.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Vitaliy Khludeev on 13.08.17.
 */
@Getter
@Setter
@Accessors(chain = true)
public class Pageable<T> {
	Integer totalPagesCount;
	Long totalItemsCount;
	List<T> items;
}