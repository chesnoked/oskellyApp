//
// Created by Виталий Хлудеев on 11.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSortController : UITableViewController {

    var previousController : CatalogProductListController!

    struct CatalogSort {
        let name : String
        let value : String?

        init(_ name: String, _ value: String?) {
            self.name = name
            self.value = value
        }
    }

    let items = [
        CatalogSort("Сначала новые", "publish_time_desc"),
        CatalogSort("По возрастанию цены", "price"),
        CatalogSort("По убыванию цены", "price_desc")
    ]

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(CatalogSortRow.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        navigationItem.title = "Сотртировка"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Отмена", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.cancelButtonPressed))
        navigationItem.hidesBackButton = true
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! CatalogSortRow
        cell.catalogNameLabel.text = items[indexPath.row].name
        if(items[indexPath.row].value == previousController.request.sort) {
            tableView.selectRow(at: indexPath, animated: false, scrollPosition: .none)
        }
        return cell
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 44.0
    }

    func okPressed() {
        if let indexPath = tableView.indexPathForSelectedRow {
            previousController.request.sort = items[indexPath.row].value
            previousController.clearData()
            previousController.loadData()
        }
        navigationController?.popViewController(animated: true)
    }

    func cancelButtonPressed() {
        navigationController?.popViewController(animated: true)
    }
}