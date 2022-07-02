package su.reddot.infrastructure.gtm;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@Deprecated
/** создан для примера не симользуется см {@link su.reddot.presentation.controller.OrderController.OrderInfoForGTM}
 *
 */
public class OrderEventView {
    private final String event = "order";
    private Long transactionId;
    //TODO: get from config
    private final String transactionAffiliation = "OSKELLY.RU";
    private BigDecimal transactionTotal;
    private final BigDecimal transactionTax = BigDecimal.valueOf(0.00);
    private final BigDecimal transactionShipping = BigDecimal.valueOf(0.00);
    private List<TransactionProduct> transactionProducts;


    @Setter @Getter
    static class TransactionProduct{
        private String sku;
        private String name;
        private String category;
        private BigDecimal price;
        private Integer quantity;

    }
}
