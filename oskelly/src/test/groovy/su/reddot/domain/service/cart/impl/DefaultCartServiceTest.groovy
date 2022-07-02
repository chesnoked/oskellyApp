package su.reddot.domain.service.cart.impl

import spock.lang.Specification
import su.reddot.domain.dao.order.OrderRepository
import su.reddot.domain.model.order.Order
import su.reddot.domain.model.order.OrderState
import su.reddot.domain.model.product.Product
import su.reddot.domain.model.product.ProductItem
import su.reddot.domain.model.user.User
import su.reddot.domain.service.cart.exception.ProductCanNotBeAddedToCartException
import su.reddot.domain.service.cart.exception.ProductNotFoundException
import su.reddot.domain.service.product.item.ProductItemService

class DefaultCartServiceSpec extends Specification {
    def itemServiceMock = Mock(ProductItemService)
    def orderRepositoryMock = Mock(OrderRepository)

    def sut = new DefaultCartService(
            productItemService: itemServiceMock,
            orderRepository: orderRepositoryMock)

    def "Пользователь добавляет недоступный товар"() {
        def nonExistentProduct = 1L
        def nonExistentSize = 1L
        def anyUser = new User()

        given:
        itemServiceMock.findFirstAvailable(nonExistentProduct, nonExistentSize) >> Optional.empty()

        when:
        sut.addItem(nonExistentProduct, nonExistentSize, anyUser)

        then:
        thrown(ProductNotFoundException)
    }

    def "Пользователь добавляет доступный товар, который уже есть в корзине"() {
        given:
        def existingProductId = 1L
        def existingSizeId = 1L
        def availableProductItem = new ProductItem(product: new Product())
        def anyUser = new User()

        itemServiceMock.findFirstAvailable(existingProductId, existingSizeId) >> Optional.of(availableProductItem)
        orderRepositoryMock.exists(_) >> true

        when:
        sut.addItem(existingProductId, existingSizeId, anyUser)

        then:
        1 * itemServiceMock.findFirstAvailable(existingProductId, existingSizeId)

        def ex = thrown(ProductCanNotBeAddedToCartException)
        ex.message == 'В корзине'
    }

    def "Гость добавляет доступный товар в корзину в первый раз"() {

        given:
        def existingProductId = 1L
        def existingSizeId = 1L
        def availableProductItem = new ProductItem(product: new Product())
        def guestToken = '1'

        orderRepositoryMock.exists(_) >> false
        itemServiceMock.findFirstAvailable(existingProductId, existingSizeId) >> Optional.of(availableProductItem)

        when:
        sut.addItem(existingProductId, existingSizeId, guestToken)

        then:
        1 * itemServiceMock.findFirstAvailable(existingProductId, existingSizeId)

        1 * orderRepositoryMock.save( {
            it instanceof Order &&
                    it.state == OrderState.CREATED &&
                    it.amount == null &&
                    it.createTime != null &&
                    it.stateTime != null &&
                    it.orderPositions[0].productItem == availableProductItem
        })
    }

    def "Если корзина пустая, то нельзя добавить скидку и адрес доставки"() {}
    def "Если корзина пустая, то ее нельзя оплатить"() {}

    def "Если корзина полностью недоступна, то к ней нельзя добавить скидку и доставку"() {}
    def "Если корзина полностью недоступна, то ее нельзя оплатить"() {}
    def "Если корзина полностью недоступна и на нее назначена скидка, то скидку можно удалить"() {}
    def "Если на корзину назначена скидка, срок действия которой истек, то корзину оплатить нельзя"() {}
    def "Если в корзине не указан полностью адрес доставки, то оплатить корзину нельзя"() {}

    def "Если пользователь удаляет товар из корзины, то нужно обновлять время изменения корзины"() {}

}
