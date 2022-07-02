//
// Created by Виталий Хлудеев on 14.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSubmitConditionController : SubmitConditionController {

    var previousController : CatalogFilterGroupsController!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.allowsMultipleSelection = true
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Очистить", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.clearPressed))
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if(previousController.request.productCondition.contains(conditions[indexPath.row].id)) {
            tableView.selectRow(at: indexPath, animated: false, scrollPosition: .none)
        }
        return super.tableView(tableView, cellForRowAt: indexPath)
    }

    func okPressed() {
        self.previousController.request.productCondition.removeAll()
        if let indexPaths = tableView.indexPathsForSelectedRows {
            indexPaths.forEach({i in
                self.previousController.request.productCondition.append(conditions[i.row].id)
            })
        }
        self.previousController.tableView.reloadData()
        navigationController?.popViewController(animated: true)
    }

    func clearPressed() {
        previousController.request.productCondition.removeAll()
        tableView.reloadData()
    }
}