//
// Created by Виталий Хлудеев on 07.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class GlobalProvider {

    private init() {
        passwordEncoder = PasswordEncoder()
        apiRequester = ApiRequester()
        accountManager = AccountManager(apiRequester: apiRequester)
        draftProvider = DraftProvider(apiRequester: apiRequester)
        directoriesProvider = DirectoriesProvider(apiRequester: apiRequester)
        cartProvider = CartProvider(apiRequester: apiRequester)
        orderProvider = OrderProvider()
        paymentRequestProvider = PaymentRequestProvider()
        dateTimeConverter = DateTimeConverter()
        profileProvider = ProfileProvider(apiRequester: apiRequester)
        accountProvider = AccountProvider(apiRequester: apiRequester)
        cartIconService = CartIconService()
        navigator = Navigator()
    }

    let navigator: Navigator
    let cartIconService: CartIconService
    let accountProvider: AccountProvider
    let profileProvider: ProfileProvider
    let dateTimeConverter: DateTimeConverter
    let draftProvider: DraftProvider
    let directoriesProvider: DirectoriesProvider
    let cartProvider: CartProvider
    let orderProvider: OrderProvider
    let paymentRequestProvider: PaymentRequestProvider

    private let accountManager: AccountManager

    func getAccountManager() -> AccountManager {
        return accountManager
    }

    private let passwordEncoder: PasswordEncoder

    func getPasswordEncoder() -> PasswordEncoder {
        return passwordEncoder
    }

    private let apiRequester: ApiRequester

    func getApiRequester() -> ApiRequester {
        return apiRequester
    }

    static let instance = GlobalProvider()

    func synchronizeData() {
        GlobalProvider.instance.directoriesProvider.synchronizeBrands()
        GlobalProvider.instance.directoriesProvider.synchronizeCategories()
        GlobalProvider.instance.directoriesProvider.synchronizeConditions()
        GlobalProvider.instance.cartProvider.synchronizeCart()
        GlobalProvider.instance.profileProvider.synchronizeProfile()
        GlobalProvider.instance.accountProvider.synchronize()
    }
}