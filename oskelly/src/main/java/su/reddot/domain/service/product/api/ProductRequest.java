package su.reddot.domain.service.product.api;

import lombok.Getter;
import lombok.Setter;
import su.reddot.domain.model.product.ProductState;
import su.reddot.domain.model.size.SizeType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author Vitaliy Khludeev on 13.08.17.
 */
@Getter
@Setter
public class ProductRequest {

	private Long category;

	private Integer page;

	private String sort;

	private List<Long> filter = Collections.emptyList();

	private List<Long> size = Collections.emptyList();

	private ProductState state;

	private SizeType sizeType;

	private List<Long> brand = Collections.emptyList();

	private List<Long> productState = Collections.emptyList();

	private List<Long> productCondition = Collections.emptyList();

	private BigDecimal startPrice;

	private BigDecimal endPrice;

    private Long seller;

    private Boolean vintage;
    private Boolean ourChoice;
    private Boolean onSale;
	private Boolean newCollection;

}