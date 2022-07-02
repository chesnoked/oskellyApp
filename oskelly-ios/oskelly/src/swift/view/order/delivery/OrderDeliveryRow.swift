//
// Created by Виталий Хлудеев on 31.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderDeliveryRow : BaseTextFieldView {

    var completionHandler: ((DeliveryRequisite) -> ())!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    func render(name: String, value: String?, numbersOnly: Bool, completionHandler: @escaping (DeliveryRequisite) -> ()) {
        super.render(name: name, value: value, numbersOnly: numbersOnly)
        self.completionHandler = completionHandler
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func onChange() {
        let order = GlobalProvider.instance.orderProvider.getCurrent()
        if(order?.deliveryRequisite == nil) {
            order?.deliveryRequisite = DeliveryRequisite()
        }
        completionHandler(order!.deliveryRequisite!)
        GlobalProvider.instance.orderProvider.setCurrent(order: order)
        GlobalProvider.instance.getApiRequester().updateDeliveryAddress(orderId: order!.id, deliveryRequisite: order!.deliveryRequisite!)
    }
}