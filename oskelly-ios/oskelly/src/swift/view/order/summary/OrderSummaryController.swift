//
// Created by Виталий Хлудеев on 23.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderSummaryController : UITableViewController {

    var order: Order?

    override func viewDidLoad() {
        super.viewDidLoad()
        if(order == nil) {
            order = GlobalProvider.instance.orderProvider.getCurrent()
        }
        tableView.separatorStyle = .none
        tableView.register(AccountOrderRow.self, forCellReuseIdentifier: "Row")
        tableView.register(OrderSummaryDeliveryRow.self, forCellReuseIdentifier: "OrderSummaryDeliveryRow")
        tableView.register(OrderSummaryDiscountRow.self, forCellReuseIdentifier: "OrderSummaryDiscountRow")
        tableView.register(AccountOrderFooter.self, forCellReuseIdentifier: "AccountOrderFooter")
        tableView.register(OrderSummaryPayButtonRow.self, forCellReuseIdentifier: "OrderSummaryPayButtonRow")
        navigationItem.title = "Оформление заказа"
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 5
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch (section) {
            case 0:
                return order?.items?.count ?? 0
            case 1:
                return order?.deliveryRequisite != nil ? 1 : 0
            case 2:
                return order?.appliedDiscount != nil ? 1 : 0
            case 3, 4:
                return order != nil ? 1 : 0
            default:
                return 0
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch (indexPath.section) {
        case 0:
            let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! AccountOrderRow
            if let item = order?.items?[indexPath.row] {
                cell.render(orderPosition: item)
            }
            return cell
        case 1:
            let cell = tableView.dequeueReusableCell(withIdentifier: "OrderSummaryDeliveryRow", for: indexPath as IndexPath) as! OrderSummaryDeliveryRow
            if let d = order?.deliveryRequisite {
                cell.render(d)
            }
            return cell
        case 2:
            let cell = tableView.dequeueReusableCell(withIdentifier: "OrderSummaryDiscountRow", for: indexPath as IndexPath) as! OrderSummaryDiscountRow
            if let d = order?.appliedDiscount {
                cell.render(d)
            }
            return cell
        case 3:
            let cell = tableView.dequeueReusableCell(withIdentifier: "AccountOrderFooter", for: indexPath as IndexPath) as! AccountOrderFooter
            if let o = order {
                cell.render(order: o, addSeparators: false)
            }
            return cell
        case 4:
            let cell = tableView.dequeueReusableCell(withIdentifier: "OrderSummaryPayButtonRow", for: indexPath as IndexPath) as! OrderSummaryPayButtonRow
            if let o = order {
                cell.render(controller: self)
            }
            return cell
        default:
            assert(false)
        }
        return tableView.dequeueReusableCell(withIdentifier: "", for: indexPath as IndexPath)
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
}