package su.reddot.domain.service.offer.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.product.OfferRepository;
import su.reddot.domain.model.product.Offer;
import su.reddot.domain.model.product.QOffer;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.offer.OfferService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultOfferService implements OfferService {

    private final OfferRepository repo;

    @Override
    public GetBuilder get() { return new DefaultGetBuilder(); }

    public class DefaultGetBuilder implements OfferService.GetBuilder {

        private QOffer offer = QOffer.offer;
        private OrderSpecifier order = offer.id.desc(); /* сортировка по-умолчанию */
        private BooleanBuilder predicate = new BooleanBuilder();

        @Override
        public GetBuilder fromBuyer(User buyer) {

            predicate.and(offer.offeror.eq(buyer));
            order = offer.createdAt.asc();

            return this;
        }

        @Override
        public GetBuilder forSeller(User seller) {

            predicate.and(offer.product.seller.eq(seller));
            order = offer.createdAt.asc();

            return this;
        }

        @Override
        public GetBuilder withId(Long id) {
            predicate.and(offer.id.eq(id));
            return this;
        }

        @Override
        public List<Offer> build() {
            List<Offer> offers = new ArrayList<>();
            repo.findAll(predicate, order)
                    .iterator().forEachRemaining(offers::add);

            return offers;
        }
    }
}
