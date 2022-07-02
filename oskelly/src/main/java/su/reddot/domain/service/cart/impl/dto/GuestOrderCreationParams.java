package su.reddot.domain.service.cart.impl.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import su.reddot.domain.model.user.User;

@Getter @Setter @Accessors(chain = true)
public class GuestOrderCreationParams {

    /** @apiNote может быть null, при этом {@link #guestToken} не должен быть null. */
    private User loggedInUser;

    /** @apiNote может быть null, при этом {@link #loggedInUser} не должен быть null. */
    private String guestToken;

    private String email;

    /** Параметры для создания учетной записи гостя */
    private String nickname;
    private String name;

    private String phone;
    private String zipCode;
    private String city;
    private String address;

    /** Комментарий к заказу. */
    private String comment;
    private String extensiveAddress;
}
