//
// Created by Виталий Хлудеев on 02.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class OrderProvider {

    private var current: Order?

    func setCurrent(order: Order?) -> Order? {
        self.current = order
        return self.current
    }

    func getCurrent() -> Order? {
        return self.current
    }
}