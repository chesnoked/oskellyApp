package su.reddot.domain.service.offer;

import su.reddot.domain.model.product.Offer;
import su.reddot.domain.model.user.User;

import java.util.List;

public interface OfferService {

    /** Получить список предложений по снижению цены (офферов) с заданными критериями отбора, которые
     * задаются в {@link GetBuilder}. */
    GetBuilder get();

    interface GetBuilder {

        /** Список предложений по снижению цены, которые отправил данный пользователь, от старых к новым. */
        GetBuilder fromBuyer(User buyer);

        /** Список предложений по снижению цены, которые получил данный пользователь, от старых к новым. */
        GetBuilder forSeller(User forSeller);

        GetBuilder withId(Long id);

        List<Offer> build();
    }
}
