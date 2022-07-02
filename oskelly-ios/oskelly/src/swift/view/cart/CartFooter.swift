//
//  CartFooter.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 06.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CartFooter: OrderBaseFooter {

    var cart: Cart!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        GlobalProvider.instance.cartProvider.getCart(completionHandler: { c in
            self.cart = c
            self.payButton.addTarget(self, action: #selector(self.payButtonClicked(_:)), for: .touchUpInside)
        })
    }
    
    func render(cart : Cart) {
        self.cart = cart
        let deliveryCost = cart.effectiveItems.reduce(0, { $0 + ($1.deliveryCost ?? 0) })
        deliveryPriceLabel.text = String(deliveryCost) + " Р"
        let productPrice = cart.effectiveItems.reduce(0, { $0 + ($1.productPrice ?? 0) })
        productPriceLabel.text = String(productPrice) + " Р"
        totalPriceLabel.text = String(productPrice + deliveryCost) + " Р"
        countLabel.text = "Всего товаров: " + String(cart.effectiveItems.count)
        if(cart.effectiveItems.isEmpty) {
            payButton.disable()
        }
        else {
            payButton.enable()
        }
    }

    func payButtonClicked(_ sender: Any) {
        GlobalProvider.instance.getApiRequester().createOrder(
                cartItems: cart.effectiveItems.map({$0.itemId}),
                completionHandler: { order in
                    GlobalProvider.instance.orderProvider.setCurrent(order: order)
                    let storyboard = UIStoryboard(name: "Main", bundle: nil)
                    var c = storyboard.instantiateViewController(withIdentifier: "OrderCertificateController")
                    self.controller.navigationController?.pushViewController(c, animated: true)
                    GlobalProvider.instance.cartProvider.synchronizeCart()
                }
        )
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
