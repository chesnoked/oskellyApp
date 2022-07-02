//
// Created by Виталий Хлудеев on 02.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class PaymentRequestProvider {

    private var current: PaymentRequest!

    func setCurrent(paymentRequest: PaymentRequest) -> PaymentRequest {
        self.current = paymentRequest
        return self.current
    }

    func getCurrent() -> PaymentRequest {
        return self.current
    }
}