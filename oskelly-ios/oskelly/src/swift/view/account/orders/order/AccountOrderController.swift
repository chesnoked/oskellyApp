//
// Created by Виталий Хлудеев on 04.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountOrderController : UITableViewController {

    var order : Order?
    var orderId: Int?

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(AccountOrderRow.self, forCellReuseIdentifier: "Row")
        tableView.register(AccountOrderFooter.self, forCellReuseIdentifier: "Footer")
        tableView.separatorStyle = .none
        navigationItem.title = "Заказ " + ((orderId == nil) ? (order.map({String($0.id)}) ?? "") : String(orderId!))
        if (order == nil) {
            if let id = orderId {
                GlobalProvider.instance.getApiRequester().getOrder(orderId: id, completionHandler: {o in
                    self.order = o
                    self.tableView.reloadData()
                })
            }
        }
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return order != nil ? (section == 0 ? (order?.items?.count ?? 0) : 1) : 0
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let o = order {
            switch (indexPath.section) {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! AccountOrderRow
                cell.render(orderPosition: o.items[indexPath.row])
                return cell
            case 1:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Footer", for: indexPath as IndexPath) as! AccountOrderFooter
                cell.render(order: o)
                return cell
            default:
                assert(false)
            }
        }
        return tableView.dequeueReusableCell(withIdentifier: "", for: indexPath as IndexPath)
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
}