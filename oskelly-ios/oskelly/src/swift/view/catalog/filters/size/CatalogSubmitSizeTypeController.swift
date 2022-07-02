//
// Created by Виталий Хлудеев on 13.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSubmitSizeTypeController : UITableViewController {

    var sizes : [Size] = []
    var previousController : CatalogSubmitSizeController!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(SubmitAttributeRowWithChecker.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        self.navigationItem.title = "Тип размера"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Назад", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.backPressed))
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sizes.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! SubmitAttributeRow
        cell.render(attributeName: sizes[indexPath.row].type, selectedValue: nil)
        if(previousController.selectedSize.type == sizes[indexPath.row].type) {
            tableView.selectRow(at: indexPath, animated: false, scrollPosition: .none)
        }
        return cell
    }

    func okPressed() {
        if let indexPath = tableView.indexPathForSelectedRow {
            previousController.selectedSize = sizes[indexPath.row]
            previousController.tableView.reloadData()
        }
        navigationController?.popViewController(animated: true)
    }

    func backPressed() {
        self.navigationController?.popViewController(animated: true)
    }
}