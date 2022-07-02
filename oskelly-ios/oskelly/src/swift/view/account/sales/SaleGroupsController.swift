//
// Created by Виталий Хлудеев on 17.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SaleGroupsController : UITableViewController {

    private var saleGroups : [SaleGroup] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(CatalogRow.self, forCellReuseIdentifier: "Row")
        tableView.separatorStyle = .none
        self.navigationItem.title = "Мои продажи"
        loadFromServer()
    }

    func loadFromServer() {
        GlobalProvider.instance.getApiRequester().getMySaleGroups(completionHandler: { saleGroups in
            self.saleGroups = saleGroups
            self.tableView.reloadData()
        })
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tableView.isUserInteractionEnabled = true
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 44.0
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return saleGroups.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! CatalogBasicRow
        cell.catalogNameLabel.text = saleGroups[indexPath.row].groupName + " (" + String(saleGroups[indexPath.row].sales.count) + ")"
        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        tableView.isUserInteractionEnabled = false
        let c = self.storyboard!.instantiateViewController(withIdentifier: "SalesController") as! SalesController
        c.saleGroup = saleGroups[indexPath.row]
        self.navigationController?.pushViewController(c, animated: true)
    }
}