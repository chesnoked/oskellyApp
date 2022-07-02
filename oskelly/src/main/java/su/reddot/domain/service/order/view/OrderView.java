package su.reddot.domain.service.order.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.reddot.domain.model.DeliveryRequisite;
import su.reddot.domain.service.order.Discount;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @apiNote {@link su.reddot.presentation.mobile.api.v1.ShoppingCartRestControllerV1} сильно зависит от этого класса.
 */
@RequiredArgsConstructor
@Getter @Setter
public class OrderView {
   private final Long id;

   private final List<OrderItem> items = new ArrayList<>();

   /** Стоимость без доставки */
   private BigDecimal price;
   private BigDecimal deliveryCost;

   private final String state;

   private final String stateName;

   /** Пользователь может перейти на страницу редактирования данных заказа перед тем, как оплачивать заказ.
    * Здесь пользователь может назначить скидки, изменить адрес доставки, выбрать метод оплаты. */
   private final boolean isPayable;

   /** Пользователь заполнил все необходимые для оплаты заказа данные и может перейти к оплате. */
   private final boolean isReadyForPayment;

   /** В новом заказе на данный момент нет ни одного доступного на данный момент товара. */
   private boolean isFaulty;

   /**
    * Заказ в данный момент оплачивается:
    * пользователь инициировал оплату, но
    * платежный шлюз еще не прислал результат этой операции.
    */
   private final boolean isPaidAtThisMoment;

   /** Данные о скидке, уже примененной к заказу. */
   private Discount appliedDiscount;

   private final DeliveryRequisite deliveryRequisite;

   private final ZonedDateTime createTime;
}
