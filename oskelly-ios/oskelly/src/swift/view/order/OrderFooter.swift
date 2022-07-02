//
// Created by Виталий Хлудеев on 01.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderFooter: OrderBaseFooter {

    var order: Order?

    var completionHandler: ((Void) -> ())?

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .white
    }

    func render(completionHandler: @escaping (() -> ()), buttonText: String) {
        self.completionHandler = completionHandler
        self.order = GlobalProvider.instance.orderProvider.getCurrent()
        let deliveryCost = order?.deliveryCost ?? 0
        deliveryPriceLabel.text = String(deliveryCost) + " ₽"
        let productPrice = order?.price ?? 0
        productPriceLabel.text = (order?.appliedDiscount?.updatedOrderAmount ?? String(productPrice)) + " ₽"
        totalPriceLabel.text = (order?.appliedDiscount?.finalOrderAmount ?? String(productPrice + deliveryCost)) + " ₽"
        countLabel.text = "Всего товаров: " + String(order?.items?.count ?? 0)
        self.payButton.addTarget(self, action: #selector(self.payButtonClicked(_:)), for: .touchUpInside)
        self.payButton.setTitle(buttonText, for: .normal)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func payButtonClicked(_ sender: Any) {
        completionHandler?()
    }
}