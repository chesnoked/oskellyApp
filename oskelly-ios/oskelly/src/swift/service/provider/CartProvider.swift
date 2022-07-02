//
// Created by Виталий Хлудеев on 30.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class CartProvider {

    private let apiRequester: ApiRequester

    private var cart: Cart?

    init(apiRequester: ApiRequester) {
        self.apiRequester = apiRequester
    }

    func synchronizeCart() {
        GlobalProvider.instance.getApiRequester().getCart(completionHandler: { c in
            self.cart = c
        })
    }

    func getCart(completionHandler: @escaping (Cart) -> ()) {
        if let c = cart {
            completionHandler(c)
            apiRequester.getCart(completionHandler: {c in
                self.cart = c
                completionHandler(c) //FIXME: что-то с этим сделать!!!
            })
        }
        else {
            apiRequester.getCart(completionHandler: {c in
                self.cart = c
                completionHandler(c)
            })
        }
    }
}