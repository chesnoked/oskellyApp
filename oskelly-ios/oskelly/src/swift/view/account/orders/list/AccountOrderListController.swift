//
// Created by Виталий Хлудеев on 28.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountOrderListController : UITableViewController {

    var account: Account!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(AccountOrderListRow.self, forCellReuseIdentifier: "Row")
        tableView.separatorStyle = .none
        navigationItem.title = "Мои заказы"
        GlobalProvider.instance.accountProvider.getCurrent(completionHandler: { a in
            self.account = a
            GlobalProvider.instance.getApiRequester().getMyOrders(completeHandler: {orders in
                self.account.orders = orders
                self.navigationItem.title = "Мои заказы (" +  String(self.account.orders.count) + ")"
                self.tableView.reloadData()
            })
        }, needSynchronize: false)
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return account != nil ? account.orders.count : 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! AccountOrderListRow
        cell.render(order: account.orders[indexPath.row], controller: self)
        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let c = storyboard?.instantiateViewController(withIdentifier: "AccountOrderController") as! AccountOrderController
        c.order = account.orders[indexPath.row]
        navigationController?.pushViewController(c, animated: true)
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 400.0
    }
}