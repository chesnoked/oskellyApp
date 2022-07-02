//
// Created by Виталий Хлудеев on 01.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderBaseController : UITableViewController {

    public var order: Order?
    let footer = OrderFooter()

    override func viewDidLoad() {
        super.viewDidLoad()
        if(order == nil) {
            order = GlobalProvider.instance.orderProvider.getCurrent()
        }
        tableView.separatorStyle = .none
        tableView.allowsSelection = false
        footer.controller = self
        renderFooter()
    }

    func renderFooter() -> Void {
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        order = GlobalProvider.instance.orderProvider.getCurrent()
        renderFooter()
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return BaseTextFieldView.height
    }

    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool
    {
        return true
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
        return cell

    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 0
    }

    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return footer
    }

    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return OrderBaseFooter.height
    }
}