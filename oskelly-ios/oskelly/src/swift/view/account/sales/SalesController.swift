//
// Created by Виталий Хлудеев on 18.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SalesController : UITableViewController {

    var saleGroup : SaleGroup!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.separatorStyle = .none
        tableView.register(SaleListRow.self, forCellReuseIdentifier: "Row")
        tableView.allowsSelection = false
        navigationItem.title = saleGroup.groupName //+ " (" + String(saleGroup.sales.count) + ")"
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SaleListRow
        cell.render(sale: saleGroup.sales[indexPath.row], controller: self)
        return cell
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return saleGroup.sales.count
    }

    public override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let sale = saleGroup.sales[indexPath.row]
        var height : CGFloat = 245
        if(sale.size == nil) {
            height = height - 22
        }
        return height
    }
}