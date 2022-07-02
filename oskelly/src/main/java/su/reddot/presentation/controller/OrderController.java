package su.reddot.presentation.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.order.OrderService;
import su.reddot.domain.service.order.view.OrderItem;
import su.reddot.domain.service.order.view.OrderView;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/{id}")
    public String get(@PathVariable Long id,

                      /* Если пользователь в запросе не указывает правильную стадию оформления заказа,
                      перенаправить его на нее. */
                      @RequestParam(name = "stage", required = false) String stage,

                      UserIdAuthenticationToken token, Model m, HttpSession session,
                      RedirectAttributes redirectAttributes) {

        User u = userService.getUserById(token.getUserId()).get();

        OrderView existingOrder = orderService.getUserOrder(id, u)
        // TODO бросать осмысленное исключение вроде OrderNotFound с информацией для страницы 404
                            .orElseThrow(IllegalArgumentException::new);

        Optional<String> redirectUrl = getRedirectIfTheCase(stage, id, existingOrder, redirectAttributes);
        if (redirectUrl.isPresent()) { return "redirect:" + redirectUrl.get(); }

        Object unavailableItems = session.getAttribute("unavailableItems");
        if (unavailableItems != null) {
            m.addAttribute("unavailableItems", unavailableItems);
            session.removeAttribute("unavailableItems");
        }

        m.addAttribute("order", existingOrder);
        OrderInfoForGTM orderInfoForGTM = from(existingOrder);
        m.addAttribute("orderInfoForGTM", orderInfoForGTM);

        if (existingOrder.isPayable()) {
            DeliveryRequisite profileDeliveryRequisite = u.getDeliveryRequisite();

            if (existingOrder.getDeliveryRequisite() != null) {
                m.addAttribute("deliveryRequisite", existingOrder.getDeliveryRequisite());
            }
            else if (profileDeliveryRequisite != null) {
                m.addAttribute("deliveryRequisite", profileDeliveryRequisite);
            }
        }

        return existingOrder.isPayable()?               "order/page"
                : existingOrder.isPaidAtThisMoment()?   "order/payment"
                :                                       "order/status";
    }

    private Optional<String> getRedirectIfTheCase(String nullableStage,
                                                  Long currentOrderId,
                                                  OrderView existingOrder,
                                                  RedirectAttributes redirectAttributes) {

        List<String> validStages = Arrays.asList("new", "pay", "done");

        String redirectUrlIfAny = null;

        if (nullableStage == null || !validStages.contains(nullableStage)) {

            String actualStage = existingOrder.isPayable()? "new"
                    : existingOrder.isPaidAtThisMoment()?   "pay"
                    :                                       "done";
            redirectAttributes.addAttribute("stage", actualStage);

            redirectUrlIfAny = String.format("/orders/%s", currentOrderId);
        }
        else if (existingOrder.isPayable() && !nullableStage.equals("new")) {

            redirectAttributes.addAttribute("stage", "new");
            redirectUrlIfAny = String.format("/orders/%s", currentOrderId);
        }
        else if (existingOrder.isPaidAtThisMoment() && !nullableStage.equals("pay")) {

            redirectAttributes.addAttribute("stage", "pay");
            redirectUrlIfAny = String.format("/orders/%s", currentOrderId);
        }
        else if (!(existingOrder.isPayable() || existingOrder.isPaidAtThisMoment())
                && !nullableStage.equals("done")) {

            redirectAttributes.addAttribute("stage", "done");
            redirectUrlIfAny = String.format("/orders/%s", currentOrderId);
        }

        return Optional.ofNullable(redirectUrlIfAny);
    }
    //TODO сделать проверки значений
    @Getter @Setter @Accessors(chain = true)
    @RequiredArgsConstructor
    private static class OrderInfoForGTM{
        private final String event = "order";
        private Long transactionId;
        private final String transactionAffiliation = "OSKELLY.RU";
        private BigDecimal transactionTotal;
        private final BigDecimal transactionTax = BigDecimal.valueOf(0.00);
        private final BigDecimal transactionShipping = BigDecimal.valueOf(0.00);
        private List<ItemInfoForGTM> transactionProducts = new ArrayList<>();
    }

    @Getter @Setter @Accessors(chain = true)
    @RequiredArgsConstructor
    private static class ItemInfoForGTM{
        private Long sku;
        private String name;
        private String category;
        private BigDecimal price;
        private  final Integer quantity = 1;
    }

    private OrderInfoForGTM from(OrderView ov){
        OrderInfoForGTM result = new OrderInfoForGTM()
                .setTransactionId(ov.getId())
                .setTransactionTotal(ov.getPrice());
        for (OrderItem orderItem : ov.getItems()) {
            ItemInfoForGTM itemInfo = from(orderItem);
            result.getTransactionProducts().add(itemInfo);
        }
        return result;
    }

    private ItemInfoForGTM from(OrderItem orderItem){
        return new ItemInfoForGTM()
                .setSku(orderItem.getProductId())
                .setName(orderItem.getProductName()+ " " + orderItem.getBrandName() + " "+orderItem.getProductSize())
                .setPrice(orderItem.getProductPrice())
                .setCategory(orderItem.getProductName());
    }
}
