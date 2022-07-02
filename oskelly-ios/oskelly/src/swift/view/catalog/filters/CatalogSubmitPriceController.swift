//
// Created by Виталий Хлудеев on 14.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSubmitPriceController : UITableViewController {

    var previousController : CatalogFilterGroupsController!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(BaseTextFieldView.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        tableView.allowsSelection = false
        navigationItem.title = "Цена"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Очистить", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.clearPressed))
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.row {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! BaseTextFieldView
                let price = previousController.request.startPrice
                cell.render(name: "От", value: price == nil ? nil : String(price!), numbersOnly: true)
                return cell
            default:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! BaseTextFieldView
                let price = previousController.request.endPrice
                cell.render(name: "До", value: price == nil ? nil : String(price!), numbersOnly: true)
                return cell
        }
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 66.0
    }

    func okPressed() {
        let startPriceCell = tableView.cellForRow(at: IndexPath(row: 0, section: 0)) as! BaseTextFieldView
        let endPriceCell = tableView.cellForRow(at: IndexPath(row: 1, section: 0)) as! BaseTextFieldView
        let startPrice = startPriceCell.getValue()
        let endPrice = endPriceCell.getValue()
        if(startPrice != nil && startPrice != "") {
            previousController.request.startPrice = Float(startPrice!)
        }
        if(endPrice != nil && endPrice != "") {
            previousController.request.endPrice = Float(endPrice!)
        }
        self.previousController.tableView.reloadData()
        navigationController?.popViewController(animated: true)
    }

    func clearPressed() {
        previousController.request.startPrice = nil
        previousController.request.endPrice = nil
        tableView.reloadData()
    }
}