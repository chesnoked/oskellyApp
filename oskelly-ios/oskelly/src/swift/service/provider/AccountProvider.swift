//
// Created by Виталий Хлудеев on 13.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class AccountProvider {

    private var account: Account?

    private let apiRequester: ApiRequester

    init(apiRequester: ApiRequester) {
        self.apiRequester = apiRequester
    }

    func synchronize() {
        apiRequester.getAccount(completionHandler: {a in
            var orders : [Order] = []
            if let ac = self.account {
                orders = ac.orders
            }
            self.account = a
            self.account?.orders = orders
        })
    }

    func setCurrent(account: Account?) {
        self.account = account
    }

    func getCurrent(completionHandler: @escaping (Account) -> (), needSynchronize: Bool) {
        if let a = account {
            completionHandler(a)
            if (needSynchronize) {
                self.synchronize()
            }
        }
        else {
            apiRequester.getAccount(completionHandler: {a in
                self.account = a
                completionHandler(a)
            })
        }
    }
}