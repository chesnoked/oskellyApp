package su.reddot.domain.dao.subscription;

import su.reddot.domain.model.user.User;

/** Проекция подписчика (когда все данные подписки не нужны) */
public interface ProductSubscriber {

    User getSubscriber();
}
